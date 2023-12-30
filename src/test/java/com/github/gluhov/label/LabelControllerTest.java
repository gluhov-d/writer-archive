package com.github.gluhov.label;

import com.github.gluhov.controller.LabelController;
import com.github.gluhov.model.Label;
import com.github.gluhov.repository.jdbc.JdbcLabelRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.github.gluhov.label.LabelTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelControllerTest {
    @Mock
    private JdbcLabelRepositoryImpl labelRepository;

    @InjectMocks
    private LabelController labelController;

    @Test
    void getByIdFound() {
        when(labelRepository.getById(LABEL_ID)).thenReturn(Optional.of(label1));
        Optional<Label> result = labelController.get(LABEL_ID);
        assertTrue(result.isPresent());
        assertEquals(LABEL_ID, result.get().getId().longValue());
        assertEquals(label1.getName(), result.get().getName());

    }

    @Test
    void getByIdNotFound() {
        when(labelRepository.getById(LABEL_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<Label> result = labelController.get(LABEL_NOT_FOUND_ID);
        assertFalse(result.isPresent());
    }

    @Test
    void delete() {
        doNothing().when(labelRepository).deleteById(LABEL_ID);
        labelController.delete(LABEL_ID);
        verify(labelRepository, times(1)).deleteById(LABEL_ID);
    }

    @Test
    void save() {
        when(labelRepository.save(any(Label.class))).thenReturn(label3);
        Label result = labelController.save(label3);
        assertNotNull(result);
        assertEquals(label3.getName(), result.getName());
    }

    @Test
    void updateSuccess() {
        when(labelRepository.update(any(Label.class))).thenReturn(true);
        boolean result = labelController.update(label2);
        assertTrue(result);
    }

    @Test
    void updateFailure() {
        when(labelRepository.update(any(Label.class))).thenReturn(false);
        boolean result = labelController.update(label2);
        assertFalse(result);
    }

    @Test
    void findAll() {
        when(labelRepository.findAll()).thenReturn(allLabels);
        List<Label> result = labelController.findAll();
        assertNotNull(result);
        assertEquals(allLabels.size(), result.size());
    }
}