package com.github.gluhov.post;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.github.gluhov.label.LabelTestData.*;

public class PostTestData {
    public static final long POST_ID = 1;
    public static final long POST_NOT_FOUND_ID = 100;

    public static final Post post1 = new Post(POST_ID, "This is the first post content", PostStatus.ACTIVE);
    public static final Post post2 = new Post(POST_ID + 1, "This is the second post content", PostStatus.UNDER_REVIEW);
    public static final Post post3 = new Post(POST_ID + 2, "Another post by a different writer", PostStatus.DELETED);
    public static final List<Post> allActivePosts = List.of(post1);

    public static Optional<Post> getUpdated() { return Optional.of(new Post(POST_ID, "Updated content", PostStatus.UNDER_REVIEW));}

    static {
        post1.setLabels(new HashSet<>(List.of(label1, label3)));
        post2.setLabels(new HashSet<>(List.of(label2)));
        post3.setLabels(new HashSet<>(List.of(label3)));
    }
}
