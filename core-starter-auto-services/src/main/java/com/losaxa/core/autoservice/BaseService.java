package com.losaxa.core.autoservice;

import com.losaxa.core.mongo.BaseMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * service基类
 *
 * @param <E>
 * @param <ID>
 * @param <R>
 */
public interface BaseService<E, ID extends Serializable, R extends BaseMongoRepository<E, ID>> {

    E create(Object data);

    void update(Object data);

    void delete(ID id);

    Optional<E> getById(ID id);

    List<E> getById(List<ID> id);

    Page<E> page(Pageable page,
                 Object entity);

    List<E> list(E entity);

    List<E> list();
}
