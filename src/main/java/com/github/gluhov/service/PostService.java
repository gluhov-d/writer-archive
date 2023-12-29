package com.github.gluhov.service;

import com.github.gluhov.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.repository.LabelRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LabelRepository labelRepository;

    public Post saveWithLabels(Post post, List<Long> labelsId) {
        List<Label> labelsToSave = new ArrayList<>();
        for (Long id: labelsId) {
            labelRepository.getById(id).ifPresent(labelsToSave::add);
        }
        post.setLabels(labelsToSave);
        return postRepository.save(post);
    }

    public boolean updateWithLabels(Post post, List<Long> labelsId) {
        List<Label> labelsToUpdate = new ArrayList<>();
        for (Long id: labelsId) {
            labelRepository.getById(id).ifPresent(labelsToUpdate::add);
        }
        post.setLabels(labelsToUpdate);
        return postRepository.update(post);
    }
}
