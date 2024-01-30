package com.github.gluhov.repository;

import com.github.gluhov.model.Label;

import java.util.Optional;

public interface LabelRepository extends GenericRepository<Label, Long> {

    Optional<Label> getByIdJoinFetch(Long id);
}