package com.github.gluhov.repository.jdbc;

import com.github.gluhov.model.Label;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.util.JdbcRepositoryUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLabelRepositoryImpl implements LabelRepository {
    private final String GET_BY_ID = "SELECT * FROM Label WHERE id = ?;";
    private final String DELETE_BY_ID = "DELETE FROM Label WHERE id=?;";
    private final String SAVE = "INSERT INTO Label (name) VALUES (?);";
    private final String UPDATE = "UPDATE Label SET name = ? WHERE id=?;";
    private final String FIND_ALL = "SELECT * FROM Label;";
    private final String CHECK_EXISTS = "SELECT * FROM Label WHERE id=?;";

    @Override
    public Optional<Label> getById(Long id) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)){
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery() ) {
                if (resultSet.next()) {
                    Label label = JdbcRepositoryUtil.getLabel(resultSet);
                    return Optional.of(label);
                } else {
                    System.out.println("Label with such ID " + id + " not found.");
                }
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
                    System.out.println("Label with such ID " + id + " deleted.");
                } else {
                    System.out.println("No label with such ID.");
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Label> save(Label label) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, label.getName());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long id = generatedKeys.getLong(1);
                            label.setId(id);
                        } else {
                            throw new SQLException("Label was saved, but can't get id.");
                        }
                    }
                }
                return Optional.of(label);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Label> update(Label label) {
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, label.getName());
            preparedStatement.setLong(2, label.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Label with such ID " + label.getId() + " updated.");
                return Optional.of(label);
            } else {
                System.out.println("No label with such ID.");
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Label> findAll() {
        List<Label> labels = new ArrayList<>();
        Connection connection = DatabaseUtil.getInstance().getConnection(true);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL)) {
                while(resultSet.next()) {
                    labels.add(JdbcRepositoryUtil.getLabel(resultSet));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return labels;
    }

    @Override
    public Boolean checkIfExists(Long id) {
        return JdbcRepositoryUtil.checkIfExists(id, CHECK_EXISTS);
    }
}
