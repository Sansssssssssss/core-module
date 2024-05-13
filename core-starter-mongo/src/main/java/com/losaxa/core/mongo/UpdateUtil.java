package com.losaxa.core.mongo;

import com.losaxa.core.common.DateUtil;
import com.losaxa.core.mongo.config.CoreDbProperties;
import com.losaxa.core.mongo.constant.CommonField;
import com.losaxa.core.security.UserContextHolder;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MongoDB 更新工具类
 */
@Component
public class UpdateUtil {

    private static MongoConverter converter;

    private static CoreDbProperties config;

    private static void setUpdateSetDocument(Document updateObjDoc, Document updateSetDoc, String prefixKey, List<String> ignore) {
        for (String field : updateObjDoc.keySet()) {
            if (ignore.contains(field)) {
                continue;
            }
            Object o = updateObjDoc.get(field);
            if (o instanceof Document) {
                setUpdateSetDocument((Document) o, updateSetDoc, prefixKey.concat(field.concat(".")), ignore);
            } else {
                updateSetDoc.put(prefixKey.concat(field), o);
            }
        }
    }

    /**
     * 将不为null的属性添加到$set操作符中,例子:$set:{ field : value} ; 如果存在内嵌文档将会转换成 $set:{ field.field : value} ;
     * 如果存在值是数组将会与普通属性一样处理(更新整个数组结构) $set:{ field : arrayValue}
     *
     * @param updateObj
     * @return
     */
    public static <T> Update updateSet(T updateObj, String... ignore) {
        return updateSet("", updateObj, ignore);
    }

    public static <T> Update updateSet(String prefixKey, T updateObj, String... ignore) {
        if (config.getAutoMatedata()) {
            //todo:luosx: MongoEventListener 优化
            MongoPersistentEntity<?> entity = converter.getMappingContext().getPersistentEntity(updateObj.getClass());
            PersistentPropertyAccessor<T> propertyAccessor = entity.getPropertyAccessor(updateObj);
            setUpdateBy(entity, propertyAccessor);
            setUpdateDate(entity, propertyAccessor);
        }
        Document updateObjDoc = new Document();
        converter.write(updateObj, updateObjDoc);
        Document     updateSetDoc = new Document();
        List<String> ignoreList   = new ArrayList<>();
        if (ignore != null && ignore.length > 0) {
            ignoreList = Arrays.asList(ignore);
        }
        setUpdateSetDocument(updateObjDoc, updateSetDoc, prefixKey, ignoreList);
        return Update.fromDocument(new Document("$set", updateSetDoc));
    }

    /**
     * 更新所有数据 , 包括为null的属性也会更新为null , 一些基础属性除外(id,createdTime,createdBy)
     *
     * @param <T>
     * @param updateObj
     * @return
     */
    public static <T> Update updateAll(T updateObj) {
        MongoPersistentEntity<?>      entity           = UpdateUtil.converter.getMappingContext().getPersistentEntity(updateObj.getClass());
        PersistentPropertyAccessor<T> propertyAccessor = entity.getPropertyAccessor(updateObj);
        if (config.getAutoMatedata()) {
            setUpdateBy(entity, propertyAccessor);
            setUpdateDate(entity, propertyAccessor);
        }
        Update                        update           = new Update();
        for (MongoPersistentProperty pop : entity) {
            String fieldName = pop.getFieldName();
            if (CommonField.ID.equals(fieldName)
                    || CommonField.CREATED_DATE.equals(fieldName)
                    || CommonField.CREATED_BY.equals(fieldName)) {
                continue;
            }
            Object value = propertyAccessor.getProperty(pop);
            update.set(fieldName, value);
        }
        return update;
    }

    private static <T> void setUpdateDate(MongoPersistentEntity<?> entity,
                                      PersistentPropertyAccessor<T> propertyAccessor) {
        MongoPersistentProperty date = entity.getPersistentProperty(CommonField.LAST_MODIFIED_DATE);
        if (date != null && propertyAccessor.getProperty(date) == null) {
            propertyAccessor.setProperty(date, DateUtil.currentTimeMillis());
        }
    }

    private static <T> void setUpdateBy(MongoPersistentEntity<?> entity,
                                      PersistentPropertyAccessor<T> propertyAccessor) {
        MongoPersistentProperty by = entity.getPersistentProperty(CommonField.LAST_MODIFIED_BY);
        if (by != null && propertyAccessor.getProperty(by) == null) {
            propertyAccessor.setProperty(by, UserContextHolder.getId());
        }
    }

    public static Update mixUpdate(Update update1, Update update2) {
        if (update1 == null && update2 == null) {
            return null;
        }
        if (update1 == null) {
            return update2;
        }
        if (update2 == null) {
            return update1;
        }
        Document updateObject = update1.getUpdateObject();
        Document andUpdateDoc = update2.getUpdateObject();
        if (andUpdateDoc.containsKey("$set") && updateObject.containsKey("$set")) {
            Object setDoc = andUpdateDoc.remove("$set");
            updateObject.get("$set", Document.class).putAll((Document) setDoc);
        }
        updateObject.putAll(andUpdateDoc);
        return Update.fromDocument(updateObject);
    }

    @Autowired
    public void setCoreDbProperties(CoreDbProperties coreDbProperties) {
        UpdateUtil.config = coreDbProperties;
    }

    @Autowired
    public void setMongoConverter(MongoConverter mongoConverter) {
        UpdateUtil.converter = mongoConverter;
    }
}
