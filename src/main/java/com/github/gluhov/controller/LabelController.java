package com.github.gluhov.controller;

import com.github.gluhov.model.Label;
import com.github.gluhov.repository.LabelRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LabelController {
    private final LabelRepository labelRepository;

    public Optional<Label> get(Long id) {
        return labelRepository.getById(id);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }

    public Label save(Label l) { return labelRepository.save(l); }

    public boolean update(Label l) { return labelRepository.update(l); }

    public List<Label> findAll() {
        return labelRepository.findAll();
    }
}
