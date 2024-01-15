package com.github.gluhov.service;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.repository.WriterRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository writerRepository;
    private final PostRepository postRepository;

    public Optional<Writer> getById(Long id) {
        return writerRepository.getById(id);
    }

    public void deleteById(Long id) {
        writerRepository.deleteById(id);
    }

    public List<Writer> findAll() {
        return writerRepository.findAll();
    }

    public Boolean checkIfExists(Long id) { return writerRepository.checkIfExists(id);}

    public Optional<Writer> saveWithPosts(Writer writer, List<Long> postsId) {
        savePosts(writer, postsId);
        return writerRepository.save(writer);
    }

    public Optional<Writer> updateWithPosts(Writer writer, List<Long> postsId) {
        savePosts(writer, postsId);
        return writerRepository.update(writer);
    }

    private void savePosts(Writer writer, List<Long> postsId) {
        Map<Long, Post> postsToUpdate = new HashMap<>();
        for (Long id: postsId) {
            postRepository.getById(id).ifPresent(p -> postsToUpdate.put(id, p));
        }
        writer.setPosts(new ArrayList<>(postsToUpdate.values()));
    }
}
