package com.losaxa.core.mongo;

import com.losaxa.core.mongo.constant.CommonField;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.bulk.WriteRequest;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.DEFAULT_CONTEXT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 基础 Repository 实现
 * @param <T>
 * @param <ID>
 */
public class BaseMongoRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements BaseMongoRepository<T, ID> {

    private final MongoEntityInformation<T, ID> metadata;
    private final MongoOperations               mongoOperations;
    private       String                        logicDeleteField;

    void setLogicDeleteField(String logicDeleteField) {
        this.logicDeleteField = logicDeleteField;
    }

    public BaseMongoRepositoryImpl(MongoEntityInformation<T, ID> metadata,
                                   MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.metadata = metadata;
    }

    //<editor-fold desc="基础方法">

    /**
     * 聚合分页查询,逻辑删除需要自己添加
     *
     * @param <E>
     * @param preCountOperations
     * @param afterCountOperations
     * @param outClass
     * @param pageable
     * @return
     */
    @Override
    public <E> Page<E> page(List<AggregationOperation> preCountOperations,
                            List<AggregationOperation> afterCountOperations,
                            Class<E> outClass,
                            Pageable pageable) {
        CountOperation countOperation = Aggregation.count().as("count");
        preCountOperations.add(countOperation);
        AggregationResults<Map> countResult = mongoOperations.aggregate(newAggregation(
                preCountOperations
        ), metadata.getCollectionName(), Map.class);
        if (CollectionUtils.isEmpty(countResult.getMappedResults()) || countResult.getMappedResults().get(0).get("count") == null) {
            return Page.empty(pageable);
        }
        long count = Long.parseLong(countResult.getMappedResults().get(0).get("count").toString());
        if (pageable.getSort().isSorted()) {
            afterCountOperations.add(Aggregation.sort(pageable.getSort()));
        }
        Optional<AggregationOperation> operation = afterCountOperations.stream()
                .filter(ele -> ele instanceof SortOperation).findFirst();
        Sort idSort = Sort.by(metadata.getIdAttribute());
        if (operation.isPresent()) {
            SortOperation sortOperation = (SortOperation) operation.get();
            if (!hashIdKey(sortOperation.toDocument(DEFAULT_CONTEXT))) {
                sortOperation.and(Sort.by(metadata.getIdAttribute()));
            }
        } else {
            afterCountOperations.add(sort(idSort));
        }
        afterCountOperations.add(skip(pageable.getOffset()));
        afterCountOperations.add(limit(pageable.getPageSize()));
        AggregationResults<E> dataResult = mongoOperations.aggregate(newAggregation(
                afterCountOperations
        ), metadata.getCollectionName(), outClass);
        return new PageImpl<>(dataResult.getMappedResults(), pageable, count);
    }

    /**
     * 聚合查询,逻辑删除需要自己添加
     *
     * @param outClass
     * @param operations
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> aggregate(Class<E> outClass,
                                 AggregationOperation... operations) {
        return mongoOperations.aggregate(newAggregation(
                operations
        ), metadata.getCollectionName(), outClass).getMappedResults();
    }

    @Override
    public BulkWriteResult bulkUpdate(BulkOperations.BulkMode bulkMode,
                                      List<Pair<Query, Update>> pairs) {
        BulkOperations bulkOperations = mongoOperations.bulkOps(bulkMode, getEntityClass());
        if (CollectionUtils.isEmpty(pairs)) {
            return BulkWriteResult.acknowledged(WriteRequest.Type.UPDATE, 0, 0, Collections.emptyList(), Collections.emptyList());
        }
        pairs.stream().map(Pair::getFirst).forEach(this::logicDeleteFilter);
        return bulkOperations.updateMulti(pairs).execute();
    }

    @Override
    public DeleteResult delete(Query query) {
        if (logicDeleteField != null) {
            Update       update = Update.update(this.logicDeleteField, 1);
            UpdateResult result = updateMulti(query, update);
            return DeleteResult.acknowledged(result.getModifiedCount());
        }
        return mongoOperations.remove(query, getEntityClass());
    }

    @Override
    public UpdateResult updateFirst(Query query,
                                    Update update) {
        logicDeleteFilter(query);
        return mongoOperations.updateFirst(query, update, getEntityClass());
    }

    @Override
    public UpdateResult updateMulti(Query query,
                                    Update update) {
        logicDeleteFilter(query);
        return mongoOperations.updateMulti(query, update, getEntityClass());
    }

    @Override
    public Optional<T> findOne(Query query) {
        logicDeleteFilter(query);
        return Optional.ofNullable(mongoOperations.findOne(query, this.getEntityClass()));
    }

    @Override
    public long count(Query query) {
        logicDeleteFilter(query);
        return mongoOperations.count(query, this.getEntityClass());
    }

    @Override
    public Optional<T> findAndModify(Query query,
                                     Update update) {
        logicDeleteFilter(query);
        return Optional.ofNullable(mongoOperations.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), this.getEntityClass()));
    }

    @Override
    public List<T> find(Query query) {
        logicDeleteFilter(query);
        defaultIdSort(query);
        return mongoOperations.find(query, this.getEntityClass());
    }

    @Override
    public List<T> find(Query query,
                        Pageable pageable) {
        query.with(pageable);
        return find(query);
    }

    @Override
    public Page<T> page(Query query,
                        Pageable pageable) {
        Class<T> entityClass = getEntityClass();
        logicDeleteFilter(query);
        long count = mongoOperations.count(query, entityClass);
        if (count < 1) {
            return Page.empty(pageable);
        }
        query.with(pageable);
        defaultIdSort(query);
        List<T> data = mongoOperations.find(query, entityClass);
        return new PageImpl<>(data, pageable, count);
    }

    private void defaultIdSort(Query query) {
        Document sortObject = query.getSortObject();
        if (!hashIdKey(sortObject)) {
            query.with(Sort.by(metadata.getIdAttribute()));
        }
    }

    private boolean hashIdKey(Document sortObject) {
        return sortObject.containsKey(metadata.getIdAttribute())
                || sortObject.containsKey(CommonField.ID);
    }
    //</editor-fold>

    //<editor-fold desc="基于基础方法封装">

    @Override
    public Page<T> pageAndDescOrder(Query query,
                                    int current,
                                    int size,
                                    String... fields) {
        return page(query, PageRequest.of(current, size, Sort.by(Sort.Direction.DESC, fields)));
    }

    @Override
    public Page<T> pageAndAscOrder(Query query,
                                   int current,
                                   int size,
                                   String... fields) {
        return page(query, PageRequest.of(current, size, Sort.by(Sort.Direction.ASC, fields)));
    }

    @Override
    public Page<T> page(Query query,
                        int current,
                        int size) {
        return page(query, PageRequest.of(current, size));
    }

    @Override
    public Optional<T> findAndModifyById(ID id,
                                         String field,
                                         Object value) {
        return findAndModifyById(id, update(field, value));
    }

    @Override
    public Optional<T> findAndModifyById(ID id,
                                         Update update) {
        return findAndModify(queryById(id), update);
    }

    @Override
    public Optional<T> findAndModifyAllById(ID id,
                                            T t) {
        Update update = UpdateUtil.updateAll(t);
        return findAndModify(queryById(id), update);
    }

    @Override
    public Optional<T> findAndModifyAllById(ID id,
                                            String shardKey,
                                            Object value,
                                            T t) {
        Update update = UpdateUtil.updateAll(t);
        Query  query  = queryById(id);
        query.addCriteria(where(shardKey).is(value));
        return findAndModify(query, update);
    }

    @Override
    public Optional<T> findAndModifyAllById(T t) {
        ID id = this.metadata.getId(t);
        return findAndModifyAllById(id, t);
    }

    @Override
    public Optional<T> findAndModifyById(T t) {
        ID     id     = this.metadata.getId(t);
        Update update = UpdateUtil.updateSet(t);
        return findAndModifyById(id, update);
    }

    @Override
    public Optional<T> findAndModify(String queryField1,
                                     Object queryValue1,
                                     String queryField2,
                                     Object queryValue2,
                                     String updateField1,
                                     Object updateValue1) {
        Query query = query(where(queryField1).is(queryValue1).and(queryField2).is(queryValue2));
        return findAndModify(query, update(updateField1, updateValue1));
    }

    @Override
    public Optional<T> findAndModify(String queryField1,
                                     Object queryValue1,
                                     String queryField2,
                                     Object queryValue2,
                                     String updateField1,
                                     Object updateValue1,
                                     String updateField2,
                                     Object updateValue2) {
        Query query = query(where(queryField1).is(queryValue1).and(queryField2).is(queryValue2));
        return findAndModify(query, update(updateField1, updateValue1).set(updateField2, updateValue2));
    }

    @Override
    public Optional<T> findOne(String queryField1,
                               Object queryValue1,
                               String queryField2,
                               Object queryValue2) {
        Query query = query(where(queryField1).is(queryValue1).and(queryField2).is(queryValue2));
        return findOne(query);
    }

    @Override
    public Optional<T> findOne(String queryField1,
                               Object queryValue1) {
        Query query = query(where(queryField1).is(queryValue1));
        return findOne(query);
    }

    @Override
    public Optional<T> findOne(Query query,
                               boolean isInclude,
                               String... fields) {
        if (fields == null) {
            return findOne(query);
        }
        fieldQuery(isInclude, query, fields);
        return findOne(query);
    }

    private Query queryById(ID id) {
        return query(where(metadata.getIdAttribute()).is(id));
    }

    @Override
    public List<T> find(String queryField1,
                        Object queryValue1) {
        return find(query(where(queryField1).is(queryValue1)));
    }

    @Override
    public List<T> find(Query query,
                        boolean isInclude,
                        String... fields) {
        fieldQuery(isInclude, query, fields);
        return find(query);
    }

    @Override
    public Optional<T> findById(ID id,
                                boolean isInclude,
                                String... fields) {
        if (fields == null) {
            return findById(id);
        }
        Query query = query(where(metadata.getIdAttribute()).is(id));
        fieldQuery(isInclude, query, fields);
        return findOne(query);
    }

    @Override
    public Optional<T> findById(ID id) {
        return findOne(queryById(id));
    }

    @Override
    public List<T> findByIds(Collection<ID> ids,
                             boolean isInclude,
                             String... fields) {
        Query query = queryByIds(ids);
        if (fields != null) {
            fieldQuery(isInclude, query, fields);
        }
        return find(query);
    }

    @Override
    public List<T> findByIds(Collection<ID> ids) {
        return findByIds(ids, false);
    }

    @Override
    public <E> List<T> findIn(Collection<E> values,
                              String field) {
        return find(query(where(field).in(values)));
    }

    @Override
    public UpdateResult updateFirst(Query query,
                                    T t) {
        Update update = UpdateUtil.updateSet(t);
        return updateFirst(query, update);
    }

    @Override
    public UpdateResult updateFirst(@NonNull String queryField1,
                                    @NonNull Object queryValue1,
                                    @NonNull String queryField2,
                                    @NonNull Object queryValue2,
                                    @NonNull String updateField1,
                                    @NonNull Object updateValue1) {
        Query query = query(where(queryField1).is(queryValue1).and(queryField2).is(queryValue2));
        return updateFirst(query, update(updateField1, updateValue1));
    }

    @Override
    public void updateFirst(String queryField1,
                            Object queryValue1,
                            String queryField2,
                            Object queryValue2,
                            T t) {
        Query query = query(where(queryField1).is(queryValue1).and(queryField2).is(queryValue2));
        updateFirst(query, t);
    }

    @Override
    public UpdateResult updateFirst(@NonNull String queryField1,
                                    @NonNull Object queryValue1,
                                    @NonNull String updateField1,
                                    @NonNull Object updateValue1) {
        Query query = query(where(queryField1).is(queryValue1));
        return updateFirst(query, update(updateField1, updateValue1));
    }

    @Override
    public UpdateResult updateMultiInId(Collection<ID> ids,
                                        String updateField1,
                                        Object updateValue1) {
        Query  query  = queryByIds(ids);
        Update update = Update.update(updateField1, updateValue1);
        return updateMulti(query, update);
    }

    @Override
    public BulkWriteResult bulkUpdateById(BulkOperations.BulkMode bulkMode,
                                          List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return BulkWriteResult.acknowledged(WriteRequest.Type.UPDATE, 0, 0, Collections.emptyList(), Collections.emptyList());
        }
        List<Pair<Query, Update>> pairs = list.stream().map(e -> {
            Query  query  = queryById(this.metadata.getId(e));
            Update update = UpdateUtil.updateSet(e);
            return Pair.of(query, update);
        }).collect(Collectors.toList());
        return bulkUpdate(bulkMode, pairs);
    }

    private Query queryByIds(Collection<ID> ids) {
        return query(where(metadata.getIdAttribute()).in(ids));
    }

    @Override
    public UpdateResult updateMulti(Query query,
                                    String updateField1,
                                    Object updateValue1) {
        return updateMulti(query, update(updateField1, updateValue1));
    }

    @Override
    public UpdateResult updateMulti(Query query,
                                    String updateField1,
                                    Object updateValue1,
                                    String updateField2,
                                    Object updateValue2) {
        return updateMulti(query, update(updateField1, updateValue1));
    }

    private Update update(String updateField1,
                          Object updateValue1) {
        return Update.update(updateField1, updateValue1);
    }

    private Update update(String updateField1,
                          Object updateValue1,
                          String updateField2,
                          Object updateValue2) {
        return Update.update(updateField1, updateValue1);
    }

    @Override
    public UpdateResult updateMulti(Query query,
                                    T t) {
        Update update = UpdateUtil.updateAll(t);
        return updateMulti(query, update);
    }

    @Override
    public UpdateResult updateMulti(String queryField1,
                                    Object queryValue1,
                                    String updateField1,
                                    Object updateValue1) {
        Query query = query(where(queryField1).is(queryValue1));
        return updateMulti(query, update(updateField1, updateValue1));
    }

    @Override
    public UpdateResult updateById(T t) {
        ID id = this.metadata.getId(t);
        return updateFirst(queryById(id), t);
    }

    @Override
    public UpdateResult updateById(T t,
                                   Update andUpdate) {
        ID       id           = this.metadata.getId(t);
        Update   update       = UpdateUtil.updateSet(t);
        Document updateObject = update.getUpdateObject();
        Document andUpdateDoc = andUpdate.getUpdateObject();
        if (andUpdateDoc.containsKey("$set") && updateObject.containsKey("$set")) {
            Object setDoc = andUpdateDoc.remove("$set");
            updateObject.get("$set", Document.class).putAll((Document) setDoc);
        }
        updateObject.putAll(andUpdateDoc);
        update = Update.fromDocument(updateObject);
        return updateFirst(queryById(id), update);
    }

    @Override
    public void pushElement(ID id,
                            String field,
                            Object value) {
        Update update = new Update();
        update.push(field, value);
        updateById(id, update);
    }

    @Override
    public <E> UpdateResult pullElementById(ID id,
                                            E elementId,
                                            String field) {
        Query    query    = queryById(id);
        Update   update   = new Update();
        Document document = new Document();
        document.put(metadata.getIdAttribute(), elementId);
        update.pull(field, document);
        return updateFirst(query, update);
    }

    @Override
    public <E> UpdateResult updateElementById(ID id,
                                              E elementId,
                                              String elemField,
                                              String updateField,
                                              Object value) {
        Query query = Query.query(Criteria
                .where(metadata.getIdAttribute()).is(id));
        Update update = new Update();
        update.filterArray("elem1." + CommonField.ID, elemField);
        update.set(String.format("%s.$[elem1].%s", elemField, updateField), value);
        return updateFirst(query, update);
    }

    @Override
    public <E> UpdateResult updateElementById(ID id,
                                              E elementId,
                                              String elemField,
                                              Object value) {
        Query query = Query.query(Criteria
                .where(metadata.getIdAttribute()).is(id));
        Update update = new Update();
        update.filterArray("elem1." + CommonField.ID, elementId);
        update.set(String.format("%s.$[elem1]", elemField), value);
        return updateFirst(query, update);
    }

    @Override
    public <E> void pushElement2(ID id,
                                 E element1Id,
                                 String element1Field,
                                 String field,
                                 Object value) {
        Update update = new Update();
        String key    = String.format("%s.$[elem1].%s", element1Field, field);
        update.filterArray("elem1." + CommonField.ID, element1Id);
        update.push(key, value);
        updateById(id, update);
    }

    @Override
    public <E, E2> UpdateResult pullElement2ById(ID id,
                                                 E element1Id,
                                                 E2 element2Id,
                                                 String element1Field,
                                                 String element2Field) {
        Query query = Query.query(Criteria
                .where(metadata.getIdAttribute()).is(id)
                .and(element1Field)
                .elemMatch(where(metadata.getIdAttribute()).is(element1Id)));
        Update   update   = new Update();
        Document document = new Document();
        document.put(metadata.getIdAttribute(), element2Id);
        String key = String.format("%s.$[].%s", element1Field, element2Field);
        update.pull(key, document);
        return updateFirst(query, update);
    }

    @Override
    public <E, E2> UpdateResult updateElement2ById(ID id,
                                                   E element1Id,
                                                   E2 element2Id,
                                                   String element1Field,
                                                   String element2Field,
                                                   String updateField,
                                                   Object value) {
        Query query = Query.query(Criteria
                .where(metadata.getIdAttribute()).is(id));
        //                .and(element1Field)
        //                .elemMatch(where(metadata.getIdAttribute()).is(element1Id)
        //                        .and(element2Field).elemMatch(where(metadata.getIdAttribute()).is(element2Field))
        //                ));
        Update update = new Update();
        String key    = String.format("%s.$[].%s.$[].%s", element1Field, element2Field, updateField);
        update.filterArray("elem1." + CommonField.ID, element1Id);
        update.filterArray("elem2." + CommonField.ID, element2Id);
        update.set(key, value);
        return updateFirst(query, update);
    }

    @Override
    public <E, E2> UpdateResult updateElement2ById(ID id,
                                                   E element1Id,
                                                   E2 element2Id,
                                                   String element1Field,
                                                   String element2Field,
                                                   Object value) {
        Query query = Query.query(Criteria
                .where(metadata.getIdAttribute()).is(id));
        //                .and(element1Field)
        //                .elemMatch(where(metadata.getIdAttribute()).is(element1Id)
        //                        .and(element2Field).elemMatch(where(metadata.getIdAttribute()).is(element2Id))
        //                ));
        Update update = new Update();
        String key    = String.format("%s.$[elem1].%s.$[elem2]", element1Field, element2Field);
        update.filterArray("elem1." + CommonField.ID, element1Id);
        update.filterArray("elem2." + CommonField.ID, element2Id);
        update.set(key, value);
        return updateFirst(query, update);
    }

    @Override
    public UpdateResult updateById(ID id,
                                   String field,
                                   Object value) {
        return updateById(id, update(field, value));
    }

    @Override
    public DeleteResult delete(String field,
                               Object value) {
        return delete(query(where(field).is(value)));
    }

    @Override
    public void deleteById(ID id) {
        delete(queryById(id));
    }

    @Override
    public DeleteResult delete(String field1,
                               Object value1,
                               String field2,
                               Object value2) {
        return delete(query(where(field1).is(value1).and(field2).is(value2)));
    }

    @Override
    public DeleteResult delete(String field1,
                               Object value1,
                               String field2,
                               Object value2,
                               String field3,
                               Object value3) {
        return delete(query(where(field1).is(value1)
                .and(field2).is(value2)
                .and(field3).is(value3)));
    }

    @Override
    public DeleteResult deleteByIds(Collection<ID> ids) {
        Query query = queryByIds(ids);
        return delete(query);
    }

    @Override
    public UpdateResult updateById(ID id,
                                   Update update) {
        return updateFirst(queryById(id), update);
    }

    @Override
    public UpdateResult updateAllById(T t) {
        Update update = UpdateUtil.updateAll(t);
        ID     id     = this.metadata.getId(t);
        return updateFirst(queryById(id), update);
    }

    @Override
    public UpdateResult incById(ID id,
                                String field,
                                int inc) {
        Update update = new Update();
        update.inc(field, inc);
        return updateFirst(query(where(metadata.getIdAttribute()).is(id)), update);
    }
    //</editor-fold>

    @Override
    public Class<T> getEntityClass() {
        return this.metadata.getJavaType();
    }

    private void fieldQuery(boolean isInclude,
                            Query query,
                            String[] fields) {
        Field qf = query.fields();
        if (isInclude) {
            for (String field : fields) {
                qf.include(field);
            }
        } else {
            for (String field : fields) {
                qf.exclude(field);
            }
        }
    }

    @Override
    public MongoOperations getMongoOperations() {
        return this.mongoOperations;
    }

    @Override
    public Update getUpdate(T t) {
        return UpdateUtil.updateSet(t);
    }

    private void logicDeleteFilter(Query query) {
        if (logicDeleteField != null) {
            query.addCriteria(where(this.logicDeleteField).is(0));
        }
    }
}
