package com.losaxa.core.autoservice;

import com.losaxa.core.autoservice.support.DoDataInit;
import com.losaxa.core.common.AppContextHolder;
import com.losaxa.core.common.converter.ObjectConverter;
import com.losaxa.core.mongo.BaseMongoRepository;
import com.losaxa.core.mongo.query.QueryUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * service基类实现
 *
 * @param <E>
 * @param <ID>
 * @param <R>
 */
@AllArgsConstructor
public class BaseServiceImpl<E, ID extends Serializable, R extends BaseMongoRepository<E, ID>> implements BaseService<E, ID, R> {

    protected final R repository;

    private final Class<E> entityClass;

    @Override
    public E create(Object data) {
        E entity = convert(data);
        if (entity instanceof DoDataInit) {
            ((DoDataInit) entity).beforeInsertDataInit();
        }
        return repository.insert(entity);
    }

    protected E convert(Object data) {
        if (Objects.isNull(data)) {
            throw new NullPointerException();
        }
        E        entity;
        Class<?> dataClass = data.getClass();
        //如果参数 data 类型不等于实体类型 , 则根据 data 的 className 获取转换器
        if (!dataClass.equals(entityClass)) {
            char[] classChar = dataClass.getSimpleName().toCharArray();
            classChar[0] |= 32;
            String                     converterName = String.valueOf(classChar) + "ConverterImpl";
            ObjectConverter<Object, E> converter     = (ObjectConverter<Object, E>) AppContextHolder.getBean(converterName);
            entity = converter.to(data);
        } else {
            entity = (E) data;
        }
        return entity;
    }

    @Override
    public void update(Object data) {
        E entity = convert(data);
        repository.updateById(entity);
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<E> getById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<E> getById(List<ID> id) {
        return repository.findByIds(id);
    }

    @Override
    public Page<E> page(Pageable page,
                        Object entity) {
        Query query = QueryUtil.createQuery(entity);
        return repository.page(query, page);
    }

    @Override
    public List<E> list(E entity) {
        Query query = QueryUtil.createQuery(entity);
        return repository.find(query);
    }

    @Override
    public List<E> list() {
        return repository.findAll();
    }

}
