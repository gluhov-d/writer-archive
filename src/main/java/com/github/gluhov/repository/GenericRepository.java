package com.github.gluhov.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    Optional<T> getById(ID id);

    void deleteById(ID id);

    Optional<T> save(T entity);

    Optional<T> update(T entity);

    List<T> findAll();

    Boolean checkIfExists(ID id);
}