package com.github.gluhov.label;

import com.github.gluhov.controller.LabelController;
import com.github.gluhov.model.Label;
import com.github.gluhov.service.LabelService;
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
    private LabelService labelService;

    @InjectMocks
    private LabelController labelController;

    @Test
    void getByIdFound() {
        when(labelService.getById(LABEL_ID)).thenReturn(Optional.of(label1));
        Optional<Label> result = labelController.get(LABEL_ID);
        assertTrue(result.isPresent());
        assertEquals(LABEL_ID, result.get().getId().longValue());
        assertEquals(label1.getName(), result.get().getName());

    }

    @Test
    void getByIdNotFound() {
        when(labelService.getById(LABEL_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<Label> result = labelController.get(LABEL_NOT_FOUND_ID);
        assertFalse(result.isPresent());
    }

    @Test
    void delete() {
        doNothing().when(labelService).deleteById(LABEL_ID);
        labelController.delete(LABEL_ID);
        verify(labelService, times(1)).deleteById(LABEL_ID);
    }

    @Test
    void save() {
        when(labelService.save(any(Label.class))).thenReturn(Optional.of(label3));
        Optional<Label> result = labelController.save(label3);
        assertTrue(result.isPresent());
        assertEquals(label3.getName(), result.get().getName());
    }

    @Test
    void updateSuccess() {
        when(labelService.update(any(Label.class))).thenReturn(getUpdated());
        Optional<Label> result = labelController.update(label2);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateFailure() {
        when(labelService.update(any(Label.class))).thenReturn(Optional.empty());
        Optional<Label> result = labelController.update(label2);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll() {
        when(labelService.findAll()).thenReturn(Optional.of(allLabels));
        Optional<List<Label>> result = labelController.findAll();
        assertFalse(result.isEmpty());
        assertEquals(allLabels.size(), result.get().size());
    }
}