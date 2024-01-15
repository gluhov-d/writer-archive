package com.github.gluhov.controller;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    public Optional<Post> get(Long id) {
        return postService.getById(id);
    }

    public void delete(Long id) {
        postService.deleteById(id);
    }

    public Optional<Post> saveWithLabels(Post p, List<Long> labelsId) {
        return postService.saveWithLabels(p, labelsId);
    }

    public Optional<Post> updateWithLabels(Post p, List<Long> labelsId) {
        return postService.updateWithLabels(p, labelsId);
    }

    public boolean checkIfPostExists(Long id) { return postService.checkIfExists(id); }

    public List<Post> findAll() {
        return postService.findAll().stream()
                .filter(p -> p.getStatus().equals(PostStatus.ACTIVE))
                .collect(Collectors.toList());
    }
}
