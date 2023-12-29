package com.github.gluhov.controller;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostRepository postRepository;
    public Optional<Post> get(Long id) {
        return postRepository.getById(id);
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    public Post saveWithLabels(Post p, List<Long> labelsId) {
        return postService.saveWithLabels(p, labelsId);
    }

    public boolean updateWithLabels(Post p, List<Long> labelsId) {
        return postService.updateWithLabels(p, labelsId);
    }

    public boolean checkIfPostExists(Long id) { return postRepository.checkIfExists(id); }

    public List<Post> findAll() {
        return postRepository.findAll().stream()
                .filter(p -> p.getStatus().equals(PostStatus.ACTIVE))
                .collect(Collectors.toList());
    }
}
