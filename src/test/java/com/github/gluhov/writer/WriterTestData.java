package com.github.gluhov.writer;

import com.github.gluhov.model.Writer;

import java.util.List;

import static com.github.gluhov.post.PostTestData.*;

public class WriterTestData {
    public static final long WRITER_ID = 1;
    public static final long WRITER_NOT_FOUND_ID = 100;

    public static final Writer writer1 = new Writer(WRITER_ID, "John", "Doe");
    public static final Writer writer2 = new Writer(WRITER_ID + 1, "Jane", "Smith");
    public static final Writer writer3 = new Writer(WRITER_ID + 2, "Emily", "Johnson");

    static {
        writer1.setPosts(List.of(post1, post3));
        writer2.setPosts(List.of(post2));
        writer3.setPosts(List.of(post3));
    }

}
