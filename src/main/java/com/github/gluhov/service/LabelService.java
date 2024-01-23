package com.github.gluhov.service;

import com.github.gluhov.model.Label;
import com.github.gluhov.repository.LabelRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;

    public Optional<Label> getById(Long id) { return labelRepository.getById(id);}
    public void deleteById(Long id) { labelRepository.deleteById(id);}
    public Optional<Label> save(Label label) { return labelRepository.save(label);}
    public Optional<Label> update(Label label) { return labelRepository.update(label);}
    public Boolean checkIfExists(Long id) { return labelRepository.checkIfExists(id);}
    public Optional<List<Label>> findAll() { return labelRepository.findAll();}
}
