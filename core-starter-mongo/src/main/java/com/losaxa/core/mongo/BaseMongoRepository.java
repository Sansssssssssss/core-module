package com.losaxa.core.mongo;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 基础 Repository 接口
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface BaseMongoRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

    Page<T> page(Query query, Pageable pageable);

    Page<T> pageAndDescOrder(Query query, int current, int size, String... fields);

    Page<T> pageAndAscOrder(Query query, int current, int size, String... fields);

    Page<T> page(Query query, int current, int size);

    UpdateResult updateFirst(Query query, T t);

    UpdateResult updateFirst(Query query, Update update);

    UpdateResult updateFirst(String queryField1, Object queryValue1, String queryField2, Object queryValue2, String updateField1, Object updateValue1);

    void updateFirst(String queryField1, Object queryValue1, String queryField2, Object queryValue2, T t);

    UpdateResult updateFirst(String queryField1, Object queryValue1, String updateField1, Object updateValue1);

    UpdateResult updateMulti(Query query, Update update);

    UpdateResult updateMultiInId(Collection<ID> ids, String updateField1, Object updateValue1);

    UpdateResult updateMulti(Query query, String updateField1, Object updateValue1);

    UpdateResult updateMulti(Query query, String updateField1, Object updateValue1, String updateField2, Object updateValue2);

    UpdateResult updateMulti(Query query, T t);

    BulkWriteResult bulkUpdate(BulkOperations.BulkMode bulkMode, List<Pair<Query, Update>> pairs);

    BulkWriteResult bulkUpdateById(BulkOperations.BulkMode bulkMode, List<T> list);

    UpdateResult updateMulti(String queryField1, Object queryValue1, String updateField1, Object updateValue1);

    UpdateResult updateById(T t);

    /**
     * 更新对象 t 属性的值并且更新 andUpdate
     *
     * @param t
     * @param andUpdate
     * @return
     */
    UpdateResult updateById(T t, Update andUpdate);

    UpdateResult updateById(ID id, Update update);

    UpdateResult updateAllById(T t);

    UpdateResult updateById(ID id, String field, Object value);

    DeleteResult delete(Query query);

    DeleteResult delete(String field, Object value);

    DeleteResult delete(String field1, Object value1, String field2, Object value2);

    DeleteResult delete(String field1, Object value1, String field2, Object value2, String field3, Object value3);

    DeleteResult deleteByIds(Collection<ID> ids);

    MongoOperations getMongoOperations();

    Class<T> getEntityClass();

    Optional<T> findAndModifyById(ID id, String field, Object value);

    Optional<T> findAndModifyAllById(ID id, T t);

    Optional<T> findAndModifyAllById(ID id, String shardKey, Object value, T t);

    Optional<T> findAndModifyAllById(T t);

    Optional<T> findAndModifyById(T t);

    Optional<T> findAndModifyById(ID id, Update update);

    Optional<T> findAndModify(Query query, Update update);

    Optional<T> findAndModify(String queryField1, Object queryValue1, String queryField2, Object queryValue2, String updateField1, Object updateValue1);

    Optional<T> findAndModify(String queryField1, Object queryValue1, String queryField2, Object queryValue2, String updateField1, Object updateValue1, String updateField2, Object updateValue2);

    long count(Query query);

    Optional<T> findOne(String queryField1, Object queryValue1, String queryField2, Object queryValue2);

    Optional<T> findOne(String queryField1, Object queryValue1);

    Optional<T> findOne(Query query);

    Optional<T> findOne(Query query, boolean isInclude, String... fields);

    List<T> find(Query query);

    List<T> find(Query query,Pageable pageable);

    List<T> find(Query query, boolean isInclude, String... fields);

    List<T> find(String queryField1, Object queryValue1);

    Optional<T> findById(ID id, boolean isInclude, String... fields);

    List<T> findByIds(Collection<ID> ids, boolean isInclude, String... fields);

    List<T> findByIds(Collection<ID> ids);

    <E> List<T> findIn(Collection<E> values, String field);

    void pushElement(ID id, String field, Object value);

    <E> UpdateResult pullElementById(ID id, E elementId, String field);

    <E> UpdateResult updateElementById(ID id, E elementId, String elemField, String updateField, Object value);

    <E> UpdateResult updateElementById(ID id, E elementId, String elemField, Object value);

    <E> void pushElement2(ID id, E element1Id, String element1Field, String field, Object value);

    <E, E2> UpdateResult pullElement2ById(ID id, E element1Id, E2 element2Id, String element1Field, String element2Field);

    <E, E2> UpdateResult updateElement2ById(ID id, E element1Id, E2 element2Id, String element1Field, String element2Field, Object value);

    <E, E2> UpdateResult updateElement2ById(ID id, E element1Id, E2 element2Id, String element1Field, String element2Field, String updateField, Object value);

    Update getUpdate(T t);

    <E> Page<E> page(List<AggregationOperation> preCountOperations,
                           List<AggregationOperation> afterCountOperations,
                           Class<E> outClass,
                           Pageable pageable);

    <E> List<E> aggregate(Class<E> outClass, AggregationOperation... operations);

    UpdateResult incById(ID id, String field, int inc);


    //<editor-fold desc="基础方法">
    static Criteria eleExist(String field, String index, Criteria... criteria) {
        if (criteria == null || criteria[0] == null) {
            criteria = new Criteria[]{new Criteria()};
        }
        return criteria[0].and(String.join(".", field, index)).exists(true);
    }
    //</editor-fold>

    static Criteria arrExist(String field, Criteria... criteria) {
        return eleExist(field, "0", criteria);
    }

    /*default Query eq(String field, Object value) {
        return query(where(field).is(value));
    }

    default Query lt(String field, Object value) {
        return query(where(field).lt(value));
    }

    default Query lte(String field, Object value) {
        return query(where(field).lte(value));
    }

    default Query gt(String field, Object value) {
        return query(where(field).gt(value));
    }

    default Query gte(String field, Object value) {
        return query(where(field).gte(value));
    }

    default Query in(String field, Object value) {
        return query(where(field).in(value));
    }

    default Query ne(String field, Object value) {
        return query(where(field).ne(value));
    }*/

}
