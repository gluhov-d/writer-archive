package com.github.gluhov.util;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.model.Writer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JdbcRepositoryUtil {

    public static Writer getWriterWithPostsAndLabels(ResultSet resultSet) throws SQLException {
        Writer writer = null;
        Map<Long, Post> postsMap = new HashMap<>();
        while (resultSet.next()) {
            if (writer == null) {
                writer = getWriter(resultSet);
            }
            if (hasColumn(resultSet, "post_id")) {
                long postId = resultSet.getLong("post_id");
                if (!resultSet.wasNull() && !postsMap.containsKey(postId)) {
                    Post post = getPost(resultSet);
                    post.setId(postId);
                    post.setLabels(new ArrayList<>());
                    postsMap.put(postId, post);
                }
                if (hasColumn(resultSet, "label_id")) {
                    long labelId = resultSet.getLong("label_id");
                    if (!resultSet.wasNull()) {
                        Label label = getLabel(resultSet);
                        label.setId(labelId);
                        postsMap.get(postId).getLabels().add(label);
                    }
                }
            }
        }

        if (writer != null && !postsMap.isEmpty()) {
            writer.setPosts(new ArrayList<>(postsMap.values()));
        }

        return writer;
    }

    public static Writer getWriter(ResultSet resultSet) throws SQLException {
        Writer writer = new Writer();
        writer.setId(resultSet.getLong("id"));
        writer.setFirstName(resultSet.getString("firstName"));
        writer.setLastName(resultSet.getString("lastName"));
        return writer;
    }

    public static Post getPostWithLabels(ResultSet resultSet) throws SQLException {
        Post post = null;
        Map<Long, Label> labels = new HashMap<>();
        while (resultSet.next()) {
            if (post == null) {
                post = getPost(resultSet);
            }
            if (hasColumn(resultSet, "label_id")) {
                long labelId = resultSet.getLong("label_id");
                if (!resultSet.wasNull() && !labels.containsKey(labelId)) {
                    Label label = getLabel(resultSet);
                    label.setId(labelId);
                    labels.put(labelId, label);
                }
            }
        }
        if (post != null && !labels.isEmpty()) post.setLabels(new ArrayList<>(labels.values()));
        return post;
    }

    public static Post getPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setContent(resultSet.getString("content"));
        post.setStatus(PostStatus.valueOf(resultSet.getString("status").toUpperCase()));
        post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        post.setUpdated(resultSet.getTimestamp("updated").toLocalDateTime());
        return post;
    }

    public static Label getLabel(ResultSet resultSet) throws SQLException {
        Label label = new Label();
        label.setId(resultSet.getLong("id"));
        label.setName(resultSet.getString("name"));
        return label;
    }

    public static Boolean checkIfExists(Long id, String query) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }
}
