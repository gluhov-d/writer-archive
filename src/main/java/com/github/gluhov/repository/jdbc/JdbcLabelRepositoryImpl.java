package com.github.gluhov.repository.jdbc;

import com.github.gluhov.model.Label;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLabelRepositoryImpl implements LabelRepository {

    @Override
    public Optional<Label> getById(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Label WHERE id = ?;")){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Label label = getLabel(resultSet);
                return Optional.of(label);
            } else {
                System.out.println("Label with such ID " + id + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Label WHERE id=?;")){
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
    public Label save(Label label) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Label (name) VALUES (?);", Statement.RETURN_GENERATED_KEYS)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return label;
    }

    public boolean update(Label label) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Label SET name = ? WHERE id=?;")) {
            preparedStatement.setString(1, label.getName());
            preparedStatement.setLong(2, label.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Label with such ID " + label.getId() + " updated.");
                return true;
            } else {
                System.out.println("No label with such ID.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Label> findAll() {
        List<Label> labels = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name FROM Label;")) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()) {
                    labels.add(getLabel(resultSet));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return labels;
    }

    @Override
    public Boolean checkIfExists(Long id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Label WHERE id=?;")) {
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Label getLabel(ResultSet resultSet) throws SQLException {
        Label label = new Label();
        label.setId(resultSet.getLong("id"));
        label.setName(resultSet.getString("name"));
        return label;
    }
}
