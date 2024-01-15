package com.github.gluhov.controller;

import com.github.gluhov.model.Label;
import com.github.gluhov.service.LabelService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    public Optional<Label> get(Long id) {
        return labelService.getById(id);
    }

    public void delete(Long id) {
        labelService.deleteById(id);
    }

    public Optional<Label> save(Label l) { return labelService.save(l); }

    public Optional<Label> update(Label l) { return labelService.update(l); }

    public List<Label> findAll() {
        return labelService.findAll();
    }
}
