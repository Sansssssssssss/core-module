package com.losaxa.core.mongo;

import com.losaxa.core.common.DateUtil;
import com.losaxa.core.common.UidGenerator;
import com.losaxa.core.mongo.constant.CommonField;
import com.losaxa.core.security.UserContextHolder;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mapping.IdentifierAccessor;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.losaxa.core.common.DateUtil.*;

/**
 * mongo 事件监听器
 */
@AllArgsConstructor
@Component
@ConditionalOnProperty(prefix = "app.database", name = "autoMatedata", havingValue = "true", matchIfMissing = true)
public class MongoEventListener extends AbstractMongoEventListener<Object> {

    private final MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

    @Lazy
    private UidGenerator uidGenerator;

    /**
     * 元数据生成
     * @param event
     */
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        super.onBeforeConvert(event);
        Object                   source = event.getSource();
        MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        if (Objects.isNull(entity)) {
            return;
        }
        PersistentPropertyAccessor<?> property           = entity.getPropertyAccessor(source);
        IdentifierAccessor            identifierAccessor = entity.getIdentifierAccessor(source);
        boolean                       isInsert           = identifierAccessor.getIdentifier() == null;
        Long                          now                = currentTimeMillis();
        //如果id为空则认为是新增记录
        if (isInsert) {
            //生成id/设置id
            setId(entity, property);
            setOperator(entity, property, CommonField.CREATED_BY);
            setDate(entity, property, CommonField.CREATED_DATE, now);
        }
        setOperator(entity, property, CommonField.LAST_MODIFIED_BY);
        setDate(entity, property, CommonField.LAST_MODIFIED_DATE, now);
    }

    /**
     * 元数据生成
     * @param event
     */
    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        super.onBeforeSave(event);
        Object                   source = event.getSource();
        MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        if (Objects.isNull(entity)) {
            return;
        }
        PersistentPropertyAccessor<?> property = entity.getPropertyAccessor(source);
        setLastModifiedBy(entity, property, event.getDocument());
        setLastModifiedDate(entity, property, event.getDocument());
    }

    private void setId(MongoPersistentEntity<?> entity,
                       PersistentPropertyAccessor<?> property) {
        MongoPersistentProperty id = entity.getRequiredIdProperty();
        if (Long.class.isAssignableFrom(id.getFieldType())) {
            property.setProperty(id, uidGenerator.getUID());
        }
    }

    private void setLastModifiedBy(MongoPersistentEntity<?> entity,
                                   PersistentPropertyAccessor<?> propertyAccessor,
                                   Document document) {
        if (document.containsKey(CommonField.LAST_MODIFIED_BY)) {
            return;
        }
        MongoPersistentProperty operator = entity.getPersistentProperty(CommonField.LAST_MODIFIED_BY);
        if (Objects.isNull(operator)) {
            return;
        }
        Object value = propertyAccessor.getProperty(operator);
        if (value != null) {
            document.put(CommonField.LAST_MODIFIED_BY, value);
            return;
        }
        Long id = UserContextHolder.getId();
        if (id != null) {
            document.put(CommonField.LAST_MODIFIED_BY, id);
        }
    }

    private void setOperator(MongoPersistentEntity<?> entity,
                             PersistentPropertyAccessor<?> propertyAccessor,
                             String property) {
        MongoPersistentProperty operator = entity.getPersistentProperty(property);
        if (Objects.isNull(operator)) {
            return;
        }
        Object value = propertyAccessor.getProperty(operator);
        if (value != null) {
            return;
        }
        Long id = UserContextHolder.getId();
        if (id != null) {
            propertyAccessor.setProperty(operator, id);
        }
    }

    private void setLastModifiedDate(MongoPersistentEntity<?> entity,
                                     PersistentPropertyAccessor<?> propertyAccessor,
                                     Document document) {
        if (document.containsKey(CommonField.LAST_MODIFIED_DATE)) {
            return;
        }
        MongoPersistentProperty time = entity.getPersistentProperty(CommonField.LAST_MODIFIED_DATE);
        if (Objects.isNull(time)) {
            return;
        }
        Object value = propertyAccessor.getProperty(time);
        if (value != null) {
            document.put(CommonField.LAST_MODIFIED_DATE, DateUtil.currentTimeMillis());
            return;
        }
        document.put(CommonField.LAST_MODIFIED_DATE, DateUtil.currentTimeMillis());
    }

    private void setDate(MongoPersistentEntity<?> entity,
                         PersistentPropertyAccessor<?> propertyAccessor,
                         String property,
                         Long now) {
        MongoPersistentProperty time = entity.getPersistentProperty(property);
        if (Objects.isNull(time)) {
            return;
        }
        Object value = propertyAccessor.getProperty(time);
        if (value != null) {
            return;
        }
        propertyAccessor.setProperty(time, now);
    }


}
