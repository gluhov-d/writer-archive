package com.github.gluhov.post;

import com.github.gluhov.controller.PostController;
import com.github.gluhov.model.Post;
import com.github.gluhov.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.github.gluhov.label.LabelTestData.LABEL_NOT_FOUND_ID;
import static com.github.gluhov.label.LabelTestData.label3;
import static com.github.gluhov.post.PostTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    void getByIdFound() {
        when(postService.getById(POST_ID)).thenReturn(Optional.of(post1));
        Optional<Post> result = postController.get(POST_ID);
        assertTrue(result.isPresent());
        assertEquals(post1.getId(), result.get().getId().longValue());
        assertEquals(post1.getContent(), result.get().getContent());
        assertEquals(post1.getStatus(), result.get().getStatus());
        assertEquals(post1.getLabels().size(), result.get().getLabels().size());
    }

    @Test
    void getByIdNotFound() {
        when(postService.getById(POST_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<Post> result = postController.get(POST_NOT_FOUND_ID);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteById() {
        doNothing().when(postService).deleteById(POST_ID);
        postController.delete(POST_ID);
        verify(postService, times(1)).deleteById(POST_ID);
    }

    @Test
    void save() {
        List<Long> labelsId = List.of(label3.getId());
        when(postService.saveWithLabels(post3, labelsId)).thenReturn(Optional.of(post3));
        Optional<Post> result = postController.saveWithLabels(post3, labelsId);
        assertTrue(result.isPresent());
        assertEquals(post3.getContent(), result.get().getContent());
        assertEquals(post3.getStatus(), result.get().getStatus());
        assertEquals(post3.getLabels().size(), result.get().getLabels().size());
    }

    @Test
    void updateSuccess() {
        List<Long> labelsId = List.of(label3.getId());
        when(postService.updateWithLabels(post3, labelsId)).thenReturn(getUpdated());
        Optional<Post> result = postController.updateWithLabels(post3, labelsId);
        assertTrue(result.isPresent());
    }

    @Test
    void updateFailure() {
        List<Long> labelsId = List.of(LABEL_NOT_FOUND_ID);
        when(postService.updateWithLabels(post3, labelsId)).thenReturn(Optional.empty());
        Optional<Post> result = postController.updateWithLabels(post3, labelsId);
        assertFalse(result.isPresent());
    }

    @Test
    void checkIfExists() {
        when(postService.checkIfExists(POST_ID)).thenReturn(true);
        boolean result = postController.checkIfPostExists(POST_ID);
        assertTrue(result);
    }

    @Test
    void findAll() {
        when(postService.findAll()).thenReturn(Optional.of(allActivePosts));
        Optional<List<Post>> result = postController.findAll();
        assertFalse(result.isEmpty());
        assertEquals(allActivePosts.size(), result.get().size());
    }

}
