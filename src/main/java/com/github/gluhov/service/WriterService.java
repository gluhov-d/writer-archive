package com.github.gluhov.service;

import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import com.github.gluhov.model.Post;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository writerRepository;
    private final PostRepository postRepository;

    public Writer saveWithPosts(Writer writer, List<Long> postsId) {
        List<Post> postsToSave = new ArrayList<>();
        for (Long id: postsId) {
            postRepository.getById(id).ifPresent(postsToSave::add);
        }
        writer.setPosts(postsToSave);
        return writerRepository.save(writer);
    }

    public boolean updateWithPosts(Writer writer, List<Long> postsId) {
        List<Post> postsToUpdate = new ArrayList<>();
        for (Long id: postsId) {
            postRepository.getById(id).ifPresent(postsToUpdate::add);
        }
        writer.setPosts(postsToUpdate);
        return writerRepository.update(writer);
    }
}
