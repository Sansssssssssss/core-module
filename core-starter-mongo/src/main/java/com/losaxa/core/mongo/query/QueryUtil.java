package com.losaxa.core.mongo.query;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 创建查询条件工具 , 配合 {@link AQuery} 注解使用
 */
@Component
@Slf4j
public class QueryUtil {

    /**
     * 注入映射上下文, 用于获取对象的属性和值等信息
     */
    private static MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

    /**
     * 类型转换器
     */
    private static ConversionService conversionService;

    @Autowired
    @Lazy
    public void setMappingContext(MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {
        QueryUtil.mappingContext = mappingContext;
    }

    @Autowired
    @Lazy
    public void setConversionService(ConversionService conversionService) {
        QueryUtil.conversionService = conversionService;
    }


    public static Query createQuery(Object obj) {
        Query query = new Query();
        if (null == obj) {
            return query;
        }
        MongoPersistentEntity<?>      entity           = mappingContext.getPersistentEntity(obj.getClass());
        PersistentPropertyAccessor<?> propertyAccessor = entity.getPropertyAccessor(obj);
        Criteria                      criteria         = new Criteria();
        List<Criteria>                criteriaList     = new LinkedList<>();
        for (MongoPersistentProperty pop : entity) {
            Object value = propertyAccessor.getProperty(pop);
            if (null == value || "".equals(value)) {
                continue;
            }
            AQuerys queryAnnotationList = pop.findAnnotation(AQuerys.class);
            AQuery  queryAnnotation     = pop.findAnnotation(AQuery.class);
            ASort   sortAnnotation      = pop.findAnnotation(ASort.class);
            if (queryAnnotationList != null && queryAnnotationList.value().length > 0) {
                addCriteria(criteriaList, pop, value, queryAnnotationList.value());
            } else if (queryAnnotation != null) {
                addCriteria(criteriaList, pop, value, new AQuery[]{queryAnnotation});
            } else if (sortAnnotation != null) {
                addSort(query, value, sortAnnotation);
            } else {
                criteria.and(pop.getFieldName()).is(value);
            }
        }
        if (criteriaList.size() > 0) {
            Criteria[] criteriaArray = new Criteria[criteriaList.size()];
            for (int i = 0; i < criteriaList.size(); i++) {
                criteriaArray[i] = criteriaList.get(i);
            }
            criteria.andOperator(criteriaArray);
        }
        query.addCriteria(criteria);
        return query;
    }

    private static void addSort(Query query,
                                Object value,
                                ASort sortAnnotation) {
        String      type                  = sortAnnotation.type();
        Sort        sort                  = null;
        ACriteria[] criteriaExtAnnotation = sortAnnotation.criteria();
        ACriteria   criteria              = getCriteria(value, criteriaExtAnnotation);
        if (criteria != null) {
            if ("desc".equalsIgnoreCase(type)) {
                sort = Sort.by(Sort.Direction.DESC, criteria.value());
            } else if ("asc".equalsIgnoreCase(type)) {
                sort = Sort.by(Sort.Direction.ASC, criteria.value());
            } else {
                sort = Sort.unsorted();
            }
            query.with(sort);
        }
    }

    private static void addCriteria(List<Criteria> criteriaList,
                                    MongoPersistentProperty pop,
                                    Object value,
                                    AQuery[] queryExtAnnotationArray) {
        for (AQuery queryExt : queryExtAnnotationArray) {
            if (null != queryExt) {
                ExpressionParser parser                  = new SpelExpressionParser();
                ACriteria[]      criteriaAnnotationArray = queryExt.criteria();
                ACriteria        criteriaAnnotation      = getCriteria(value, criteriaAnnotationArray);
                Object           criteriaValue           = criteriaAnnotation == null ? value : criteriaAnnotation.value();
                try {
                    Expression expression = parser.parseExpression(criteriaValue.toString());
                    criteriaValue = conversionService.convert(expression.getValue(), pop.getActualType());
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                    criteriaValue = conversionService.convert(criteriaValue.toString(), pop.getActualType());
                }
                if (queryExt.type() == QueryType.$regex) {
                    Pattern pattern = Pattern.compile("^.*" + criteriaValue + ".*$", Pattern.CASE_INSENSITIVE);
                    criteriaList.add(where(pop.getFieldName()).regex(pattern));
                } else if (QueryType.$ignore != queryExt.type()) {
                    criteriaList.add(where(pop.getFieldName()).is(new Document(queryExt.type().toString(), criteriaValue)));
                }
            }
        }
    }

    private static ACriteria getCriteria(Object value,
                                         ACriteria[] criteriaAnnotationArray) {
        if (criteriaAnnotationArray.length <= 0) {
            return null;
        }
        for (ACriteria criteriaExtAnnotation : criteriaAnnotationArray) {
            if (value.toString().equals(criteriaExtAnnotation.criteria())) {
                return criteriaExtAnnotation;
            }
        }
        return null;
    }

}
