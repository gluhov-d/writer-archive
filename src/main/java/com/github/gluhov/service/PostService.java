package com.github.gluhov.service;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LabelRepository labelRepository;

    public Optional<Post> getById(Long id) { return postRepository.getById(id);}
    public void deleteById(Long id) { postRepository.deleteById(id);}
    public Optional<List<Post>> findAll() { return postRepository.findAll();}

    public Boolean checkIfExists(Long id) { return postRepository.checkIfExists(id);}

    public Optional<Post> saveWithLabels(Post post, List<Long> labelsId) {
        saveLabels(post, labelsId);
        return postRepository.save(post);
    }

    private void saveLabels(Post post, List<Long> labelsId) {
        Map<Long, Label> labelsToSave = new HashMap<>();
        for (Long id: labelsId) {
            labelRepository.getById(id).ifPresent(l -> labelsToSave.put(id, l));
        }
        post.setLabels(new ArrayList<>(labelsToSave.values()));
    }

    public Optional<Post> updateWithLabels(Post post, List<Long> labelsId) {
        saveLabels(post, labelsId);
        return postRepository.update(post);
    }
}
