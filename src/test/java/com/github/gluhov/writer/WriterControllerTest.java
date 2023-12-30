package com.github.gluhov.writer;

import com.github.gluhov.controller.WriterController;
import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.jdbc.JdbcWriterRepositoryImpl;
import com.github.gluhov.service.WriterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.github.gluhov.post.PostTestData.post1;
import static com.github.gluhov.post.PostTestData.post3;
import static com.github.gluhov.writer.WriterTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WriterControllerTest {

    @Mock
    private JdbcWriterRepositoryImpl writerRepository;
    @Mock
    private WriterService writerService;
    @InjectMocks
    private WriterController writerController;

    @Test
    void getByIdFound() {
        when(writerRepository.getById(WRITER_ID)).thenReturn(Optional.of(writer1));
        Optional<Writer> result = writerController.get(WRITER_ID);
        assertTrue(result.isPresent());
        assertEquals(writer1.getId(), result.get().getId().longValue());
        assertEquals(writer1.getFirstName(), result.get().getFirstName());
        assertEquals(writer1.getLastName(), result.get().getLastName());
        assertEquals(writer1.getPosts().size(), result.get().getPosts().size());
    }

    @Test
    void getByIdNotFound() {
        when(writerRepository.getById(WRITER_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<Writer> result = writerController.get(WRITER_NOT_FOUND_ID);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteById() {
        doNothing().when(writerRepository).deleteById(WRITER_ID);
        writerController.delete(WRITER_ID);
        verify(writerRepository, times(1)).deleteById(WRITER_ID);
    }

    @Test
    void save() {
        List<Long> postsId = List.of(post1.getId(), post3.getId());
        when(writerService.saveWithPosts(writer1, postsId)).thenReturn(writer1);
        Writer result = writerController.saveWithPosts(writer1, postsId);
        assertNotNull(result);
        assertEquals(writer1.getFirstName(), result.getFirstName());
        assertEquals(writer1.getLastName(), result.getLastName());
        assertEquals(writer1.getPosts().size(), result.getPosts().size());
    }

    @Test
    void updateSuccess() {
        List<Long> postsId = List.of(post1.getId(), post3.getId());
        when(writerService.updateWithPosts(writer1, postsId)).thenReturn(true);
        boolean result = writerController.updateWithPosts(writer1, postsId);
        assertTrue(result);
    }

    @Test
    void updateFailure() {
        List<Long> postsId = List.of(post1.getId(), post3.getId());
        when(writerService.updateWithPosts(writer1, postsId)).thenReturn(false);
        boolean result = writerController.updateWithPosts(writer1, postsId);
        assertFalse(result);
    }

    @Test
    void checkIfExists() {
        when(writerRepository.checkIfExists(WRITER_ID)).thenReturn(true);
        boolean result = writerController.checkIfWriterExists(WRITER_ID);
        assertTrue(result);
    }
}
