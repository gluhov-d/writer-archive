package com.github.gluhov.controller;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.model.Writer;
import com.github.gluhov.service.PostService;
import com.github.gluhov.service.WriterService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WriterController {
    private final WriterService writerService;
    private final PostService postService;

    public Optional<Writer> get(Long id) {
        return writerService.getById(id);
    }

    public boolean checkPostStatus(Long id) {
        Optional<Post> post = postService.getById(id);
        return post.map(value -> value.getStatus().equals(PostStatus.ACTIVE) || value.getStatus().equals(PostStatus.UNDER_REVIEW)).orElse(false);
    }

    public boolean checkIfWriterExists(Long id) { return writerService.checkIfExists(id); }

    public void delete(Long id) {
        writerService.deleteById(id);
    }

    public Optional<Writer> saveWithPosts(Writer w, List<Long> postsId) {
        return writerService.saveWithPosts(w, postsId);
    }

    public Optional<Writer> updateWithPosts(Writer w, List<Long> postsId) {
        return writerService.updateWithPosts(w, postsId);
    }

    public List<Writer> findAll() {
        return writerService.findAll();
    }
}
