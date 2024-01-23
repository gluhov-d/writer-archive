package com.github.gluhov.repository.jdbc;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.WriterRepository;
import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.util.JdbcRepositoryUtil;

import java.sql.*;
import java.util.*;

public class JdbcWriterRepositoryImpl implements WriterRepository {
    private final String GET_BY_ID = """
                     SELECT w.*,
                      wp.post_id, p.*,
                      pl.label_id, l.*
                     FROM writer w
                     LEFT JOIN writer_post wp ON w.id = wp.writer_id
                     LEFT JOIN post p ON wp.post_id = p.id
                     LEFT JOIN post_label pl ON p.id = pl.post_id
                     LEFT JOIN label l ON pl.label_id = l.id
                     WHERE w.id = ?;""";
    private final String DELETE_BY_ID = "DELETE FROM Writer WHERE id=?;";
    private final String INSERT = "INSERT INTO Writer (firstName, lastName) VALUES (?, ?);";
    private final String INSERT_POSTS = "INSERT INTO Writer_Post (writer_id, post_id) VALUES (?, ?);";
    private final String UPDATE = "UPDATE Writer SET firstName = ?, lastName = ? WHERE id=?;";
    private final String WRITERS_POST_TO_UPDATE = "SELECT * FROM Writer_Post WHERE writer_id=?;";
    private final String DELETE_POST = "DELETE FROM Writer_Post WHERE writer_id=? AND post_id=?;";
    private final String GET_ALL = "SELECT * FROM Writer";
    private final String CHECK_EXISTS = "SELECT * FROM Writer WHERE id = ?;";

    @Override
    public Optional<Writer> getById(Long id) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)){
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.of(JdbcRepositoryUtil.getWriterWithPostsAndLabels(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID)){
            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Writer with such ID " + id + " deleted.");
            } else {
                System.out.println("No writer with such ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Writer> save(Writer writer) {
        Connection connection = DatabaseUtil.getInstance().getConnection(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1, writer.getFirstName());
                preparedStatement.setString(2, writer.getLastName());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long id = generatedKeys.getLong(1);
                            writer.setId(id);
                        } else {
                            throw new SQLException("Writer was saved, but can't get ID.");
                        }
                    }
                    saveWriterPosts(getPostsId(writer.getPosts()), writer.getId(), connection);
                    connection.commit();
                    return Optional.of(writer);
                }
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
    public Optional<Writer> update(Writer writer) {
        Connection connection = DatabaseUtil.getInstance().getConnection(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)){
                preparedStatement.setString(1, writer.getFirstName());
                preparedStatement.setString(2, writer.getLastName());
                preparedStatement.setLong(3, writer.getId());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try(PreparedStatement allWriterPosts = connection.prepareStatement(WRITERS_POST_TO_UPDATE)) {
                        allWriterPosts.setLong(1, writer.getId());
                        ResultSet postsToUpdate = allWriterPosts.executeQuery();
                        Set<Long> existingPosts = new HashSet<>();
                        while (postsToUpdate.next()) {
                            existingPosts.add(postsToUpdate.getLong("post_id"));
                        }
                        if (existingPosts.isEmpty()) {
                            saveWriterPosts(getPostsId(writer.getPosts()), writer.getId(), connection);
                        } else {
                            Set<Long> postsToSave = new HashSet<>();
                            writer.getPosts().forEach(p -> postsToSave.add(p.getId()));
                            Set<Long> postsToDelete = new HashSet<>(existingPosts);
                            postsToDelete.removeAll(postsToSave);
                            for (Long id: postsToDelete) {
                                try (PreparedStatement deleteWriterPosts = connection.prepareStatement(DELETE_POST)){
                                    deleteWriterPosts.setLong(1, writer.getId());
                                    deleteWriterPosts.setLong(2, id);
                                    int deletedPostsRows = deleteWriterPosts.executeUpdate();
                                    if (deletedPostsRows == 0) {
                                        throw new SQLException("Not all posts was saved for this writer.");
                                    }
                                }
                            }
                            postsToSave.removeAll(existingPosts);
                            if (!postsToSave.isEmpty()) {
                                saveWriterPosts(postsToSave, writer.getId(), connection);
                            }
                        }
                        connection.commit();
                        return Optional.of(writer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
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
    public Optional<List<Writer>> findAll() {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(GET_ALL);
            List<Writer> writers = new ArrayList<>();
            while (resultSet.next()) {
                writers.add(JdbcRepositoryUtil.getWriter(resultSet));
            }
            return Optional.of(writers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Boolean checkIfExists(Long id) {
        return JdbcRepositoryUtil.checkIfExists(id, CHECK_EXISTS);
    }

    private Set<Long> getPostsId(List<Post> post) {
        Set<Long> ids = new HashSet<>();
        post.forEach(p -> ids.add(p.getId()));
        return ids;
    }

    private void saveWriterPosts(Set<Long> posts, long writerId, Connection connection) throws SQLException {
        for (Long post: posts) {
            try (PreparedStatement addWriterPosts = connection.prepareStatement(INSERT_POSTS)) {
                addWriterPosts.setLong(1, writerId);
                addWriterPosts.setLong(2, post);
                int addedWriterPostRows = addWriterPosts.executeUpdate();
                if (addedWriterPostRows == 0) {
                    throw new SQLException("Not all posts was saved for this writer.");
                }
            }
        }
    }

}
