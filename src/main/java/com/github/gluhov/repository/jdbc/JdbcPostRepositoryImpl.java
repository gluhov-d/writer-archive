package com.github.gluhov.repository.jdbc;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPostRepositoryImpl implements PostRepository {
    @Override
    public Optional<Post> getById(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.*, pl.label_id, l.name\n" +
                        "FROM post p\n" +
                        "LEFT JOIN post_label pl ON p.id = pl.post_id\n" +
                        "LEFT JOIN label l ON pl.label_id = l.id\n" +
                        "WHERE p.id = ?; ")) {
                preparedStatement.setLong(1, id);

                List<Label> labels = new ArrayList<>();
                Post post = null;

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        if (post == null) {
                            post = getPost(resultSet);
                        }

                        long labelId = resultSet.getLong("label_id");
                        if (!resultSet.wasNull()) {
                            Label label = new Label();
                            label.setId(labelId);
                            label.setName(resultSet.getString("name"));
                            labels.add(label);
                        }
                    }
                }
                if (post != null) {
                    post.setLabels(labels);
                    return Optional.of(post);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Post SET status=? WHERE id=?;")) {
            preparedStatement.setString(1, PostStatus.DELETED.toString());
            preparedStatement.setLong(2, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Post was marked as deleted.");
            } else {
                System.out.println("No post with such ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Post save(Post post) {
        try {
            Connection connection = DatabaseUtil.getConnection();
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Post (content, status) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, post.getContent());
                preparedStatement.setString(2, post.getStatus().toString());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long id = generatedKeys.getLong(1);
                            post.setId(id);
                        } else {
                            throw new SQLException("Post was saved , but can't get ID and save post labels.");
                        }
                    }
                    for (Label label: post.getLabels()) {
                        try(PreparedStatement addPostLabels = connection.prepareStatement("INSERT INTO Post_Label (post_id, label_id) VALUES (?, ?);")) {
                            addPostLabels.setLong(1, post.getId());
                            addPostLabels.setLong(2, label.getId());
                            int addedPostLabelRows = addPostLabels.executeUpdate();
                            if (addedPostLabelRows == 0) {
                                throw new SQLException("Not all labels was saved for this post.");
                            }
                        }
                    }
                    connection.commit();
                    return post;
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public boolean update(Post post) {
        try {
            Connection connection = DatabaseUtil.getConnection();
            try {
                connection.setAutoCommit(false);
                PreparedStatement updatePost = connection.prepareStatement("UPDATE Post SET content = ?, status = ? WHERE id=?;");
                updatePost.setString(1, post.getContent());
                updatePost.setString(2, post.getStatus().toString());
                updatePost.setLong(3, post.getId());
                int affectedRows = updatePost.executeUpdate();

                if (affectedRows > 0) {
                    PreparedStatement deletePostLabels = connection.prepareStatement("DELETE FROM Post_Label WHERE post_id=?;");
                    deletePostLabels.setLong(1, post.getId());
                    deletePostLabels.executeUpdate();
                    for (Label label: post.getLabels()) {
                        try (PreparedStatement addPostLabels = connection.prepareStatement("INSERT INTO Post_Label (post_id, label_id) VALUES (?, ?);")) {
                            addPostLabels.setLong(1, post.getId());
                            addPostLabels.setLong(2, label.getId());
                            int addedPostLabelRows = addPostLabels.executeUpdate();
                            if (addedPostLabelRows == 0) {
                                throw new SQLException("Not all label was saved for this post.");
                            }
                        }
                    }
                    connection.commit();
                    return true;
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Post")){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                posts.add(getPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Boolean checkIfExists(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Post WHERE id = ?;")){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Post getPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setContent(resultSet.getString("content"));
        post.setStatus(PostStatus.valueOf(resultSet.getString("status").toUpperCase()));
        post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        post.setUpdated(resultSet.getTimestamp("updated").toLocalDateTime());
        return post;
    }
}
