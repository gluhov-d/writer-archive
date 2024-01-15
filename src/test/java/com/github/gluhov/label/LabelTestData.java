package com.github.gluhov.label;

import com.github.gluhov.model.Label;

import java.util.List;
import java.util.Optional;

public class LabelTestData {

    public static final long LABEL_ID = 1;
    public static final long LABEL_NOT_FOUND_ID = 100;

    public static final Label label1 = new Label(LABEL_ID, "Technology");
    public static final Label label2 = new Label(LABEL_ID + 1, "Health");
    public static final Label label3 = new Label(LABEL_ID + 2, "Lifestyle");

    public static Optional<Label> getUpdated() { return Optional.of(new Label(LABEL_ID, "Philosophy"));}

    public static final List<Label> allLabels = List.of(label1, label2, label3);
}
