package com.github.gluhov.service;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Set<Label> labelsToSave = new HashSet<>();
        for (Long id: labelsId) {
            Optional<Label>  existingLabel = labelRepository.getById(id);
            existingLabel.ifPresent(labelsToSave::add);
        }
        post.setLabels(labelsToSave);
    }

    public Optional<Post> updateWithLabels(Post post, List<Long> labelsId) {
        saveLabels(post, labelsId);
        return postRepository.update(post);
    }
}
