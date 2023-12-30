package com.github.gluhov.repository.jdbc;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.WriterRepository;
import com.github.gluhov.util.DatabaseUtil;

import java.sql.*;
import java.util.*;

public class JdbcWriterRepositoryImpl implements WriterRepository {

    @Override
    public Optional<Writer> getById(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT w.*,\n"+
                     " wp.post_id, p.*,\n" +
                     " pl.label_id, l.*\n" +
                     "FROM writer w\n" +
                     "LEFT JOIN writer_post wp ON w.id = wp.writer_id\n" +
                     "LEFT JOIN post p ON wp.post_id = p.id\n" +
                     "LEFT JOIN post_label pl ON p.id = pl.post_id\n" +
                     "LEFT JOIN label l ON pl.label_id = l.id\n" +
                     "WHERE w.id = ?;")){
            preparedStatement.setLong(1, id);
            Writer writer = null;
            Map<Long, Post> postsMap = new HashMap<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (writer == null) {
                        writer = getWriter(resultSet);
                    }

                    long postId = resultSet.getLong("post_id");
                    if (!resultSet.wasNull() && !postsMap.containsKey(postId)) {
                        Post post = new Post();
                        post.setId(postId);
                        post.setContent(resultSet.getString("content"));
                        post.setStatus(PostStatus.valueOf(resultSet.getString("status").toUpperCase()));
                        post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                        post.setUpdated(resultSet.getTimestamp("updated").toLocalDateTime());
                        post.setLabels(new ArrayList<>());
                        postsMap.put(postId, post);
                    }

                    long labelId = resultSet.getLong("label_id");
                    if (!resultSet.wasNull()) {
                        Label label = new Label();
                        label.setId(labelId);
                        label.setName(resultSet.getString("name"));
                        postsMap.get(postId).getLabels().add(label);
                    }
                }
            }
            List<Post> writerPosts = new ArrayList<>();
            for (Map.Entry<Long, Post> p: postsMap.entrySet()) {
                writerPosts.add(p.getValue());
            }
            if (writer != null) {
                writer.setPosts(writerPosts);
                return Optional.of(writer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Writer WHERE id=?")){
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
    public Writer save(Writer writer) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Writer (firstName, lastName) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
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
                    for (Post post: writer.getPosts()) {
                        try (PreparedStatement addWriterPosts = connection.prepareStatement("INSERT INTO Writer_Post (writer_id, post_id) VALUES (?, ?);")) {
                            addWriterPosts.setLong(1, writer.getId());
                            addWriterPosts.setLong(2, post.getId());
                            int addedWriterPostRows = addWriterPosts.executeUpdate();
                            if (addedWriterPostRows == 0) {
                                throw new SQLException("Not all posts was saved for this writer.");
                            }
                        }
                    }
                    connection.commit();
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
        return writer;
    }

    @Override
    public boolean update(Writer writer) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Writer SET firstName = ?, lastName = ? WHERE id=?;");

                preparedStatement.setString(1, writer.getFirstName());
                preparedStatement.setString(2, writer.getLastName());
                preparedStatement.setLong(3, writer.getId());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    PreparedStatement deleteWriterPosts = connection.prepareStatement("DELETE FROM Writer_Post WHERE writer_id=?;");
                    deleteWriterPosts.setLong(1, writer.getId());
                    deleteWriterPosts.executeUpdate();
                    for (Post post: writer.getPosts()) {
                        PreparedStatement addWriterPosts = connection.prepareStatement("INSERT INTO Writer_Post (writer_id, post_id) VALUES (?, ?);");
                        addWriterPosts.setLong(1, writer.getId());
                        addWriterPosts.setLong(2, post.getId());
                        int addedWriterPostRows = addWriterPosts.executeUpdate();
                        if (addedWriterPostRows == 0) {
                            throw new SQLException("Not all posts was saved for this writer.");
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
    public List<Writer> findAll() {
        List<Writer> writers = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Writer");
            while (resultSet.next()) {
                writers.add(getWriter(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writers;
    }

    @Override
    public Boolean checkIfExists(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Writer WHERE id = ?;")){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Writer getWriter(ResultSet resultSet) throws SQLException {
        Writer writer = new Writer();
        writer.setId( resultSet.getLong("id"));
        writer.setFirstName(resultSet.getString("firstName"));
        writer.setLastName(resultSet.getString("lastName"));
        return writer;
    }
}
