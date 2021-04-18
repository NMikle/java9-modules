package io.mikle.test.mds.api.service;

import io.mikle.test.mds.api.model.Entity;

import java.util.List;

public interface GeneralService<T extends Entity> {
    T save(T entity);

    T findById(Integer id);

    List<T> findAll();

    T delete(Integer id);
}
