package com.github.gluhov.repository.jdbc;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.util.JdbcRepositoryUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class JdbcPostRepositoryImpl implements PostRepository {
    private final String GET_BY_ID = """
                       SELECT p.*, pl.label_id, l.name
                       FROM post p
                       LEFT JOIN post_label pl ON p.id = pl.post_id
                       LEFT JOIN label l ON pl.label_id = l.id
                       WHERE p.id = ?;""";
    private final String DELETE_BY_ID = "UPDATE Post SET status=? WHERE id=?;";
    private final String SAVE_POST = "INSERT INTO Post (content, status, created, updated) VALUES (?,?,?,?);";
    private final String SAVE_POST_LABELS = "INSERT INTO Post_Label (post_id, label_id) VALUES (?, ?);";
    private final String UPDATE_POST = "UPDATE Post SET content = ?, status = ?, updated = ? WHERE id=?;";
    private final String POST_LABELS_TO_UPDATE = "SELECT * FROM Post_Label WHERE post_id = ?;";
    private final String ALL_POSTS = "SELECT * FROM Post;";
    private final String CHECK_EXISTS = "SELECT * FROM Post WHERE id = ?;";
    private final String DELETE_LABEL = "DELETE FROM Post_Label WHERE post_id=? AND label_id=?";

    @Override
    public Optional<Post> getById(Long id) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    Post post = JdbcRepositoryUtil.getPostWithLabels(resultSet);
                    if (post != null) return Optional.of(post);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID)) {
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
    public Optional<Post> save(Post post) {
        Connection connection = DatabaseUtil.getInstance().getConnection(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_POST, Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1, post.getContent());
                preparedStatement.setString(2, post.getStatus().toString());
                LocalDateTime created = LocalDateTime.now();
                preparedStatement.setTimestamp(3, Timestamp.valueOf(created));
                preparedStatement.setTimestamp(4, Timestamp.valueOf(created));
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long id = generatedKeys.getLong(1);
                            post.setId(id);
                            post.setCreated(created);
                            post.setUpdated(created);
                        } else {
                            throw new SQLException("Post was saved , but can't get ID or created or updated time and save post labels.");
                        }
                        savePostLabels(getLabelsId(post.getLabels()), post.getId(), connection);
                        connection.commit();
                    }
                }
                connection.close();
                return Optional.of(post);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
                e.printStackTrace();
            }
        return Optional.empty();
    }

    @Override
    public Optional<Post> update(Post post) {
        Connection connection = DatabaseUtil.getInstance().getConnection(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POST)){
                preparedStatement.setString(1, post.getContent());
                preparedStatement.setString(2, post.getStatus().toString());
                LocalDateTime updated = LocalDateTime.now();
                preparedStatement.setTimestamp(3, Timestamp.valueOf(updated));
                preparedStatement.setLong(4, post.getId());


                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (PreparedStatement allPostLabels = connection.prepareStatement(POST_LABELS_TO_UPDATE)) {
                        allPostLabels.setLong(1, post.getId());
                        try (ResultSet labelsToUpdate = allPostLabels.executeQuery()) {
                            Set<Long> existingLabels = new HashSet<>();
                            while (labelsToUpdate.next()) {
                                existingLabels.add(labelsToUpdate.getLong("label_id"));
                            }
                            if (existingLabels.isEmpty()) {
                                savePostLabels(getLabelsId(post.getLabels()), post.getId(), connection);
                            } else {
                                Set<Long> labelsToSave = new HashSet<>();
                                post.getLabels().forEach(l -> labelsToSave.add(l.getId()));
                                Set<Long> labelsToDelete = new HashSet<>(existingLabels);
                                labelsToDelete.removeAll(labelsToSave);
                                labelsToSave.removeAll(existingLabels);

                                for (Long id: labelsToDelete) {
                                    try (PreparedStatement deleteLabel = connection.prepareStatement(DELETE_LABEL)) {
                                        deleteLabel.setLong(1, post.getId());
                                        deleteLabel.setLong(2, id);
                                        int deletedLabelsRows = deleteLabel.executeUpdate();
                                        if (deletedLabelsRows == 0) {
                                            throw new SQLException("Label with ID " + id + " was not deleted from post.");
                                        }
                                    }
                                }
                                if (!labelsToSave.isEmpty()) {
                                    savePostLabels(labelsToSave, post.getId(), connection);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                connection.commit();
                return Optional.of(post);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }

        return Optional.empty();
    }

    private void savePostLabels(Set<Long> labels, long postId, Connection connection) throws SQLException {
        for (Long labelId : labels) {
            try (PreparedStatement addPostLabels = connection.prepareStatement(SAVE_POST_LABELS)) {
                addPostLabels.setLong(1, postId);
                addPostLabels.setLong(2, labelId);
                int addedPostLabelRows = addPostLabels.executeUpdate();
                if (addedPostLabelRows == 0) {
                    throw new SQLException("Not all label was saved for this post.");
                }
            }
        }
    }

    private Set<Long> getLabelsId(List<Label> labels) {
        Set<Long> ids = new HashSet<>();
        labels.forEach(l -> ids.add(l.getId()));
        return ids;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        Connection connection = DatabaseUtil.getInstance().getConnection(true);

        try (PreparedStatement statement = connection.prepareStatement(ALL_POSTS);
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                posts.add(JdbcRepositoryUtil.getPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Boolean checkIfExists(Long id) {
        return JdbcRepositoryUtil.checkIfExists(id, CHECK_EXISTS);
    }
}
