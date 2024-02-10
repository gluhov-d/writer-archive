package com.github.gluhov.service;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.repository.WriterRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Optional<List<Writer>> findAll() {
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
        Set<Post> postsToUpdate = new HashSet<>();
        for (Long id: postsId) {
            Optional<Post> existingPost = postRepository.getById(id);
            existingPost.ifPresent(postsToUpdate::add);
        }
        writer.setPosts(postsToUpdate);
    }
}
