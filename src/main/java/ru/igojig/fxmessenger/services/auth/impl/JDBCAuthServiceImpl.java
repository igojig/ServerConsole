package ru.igojig.fxmessenger.services.auth.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.auth.AuthService;
import ru.igojig.fxmessenger.services.LocalFileService;

import java.sql.*;
import java.util.Optional;

public class JDBCAuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(JDBCAuthServiceImpl.class);

    private static Throwable lastError;

    String addUserSQL = "INSERT INTO users (login, password, username) VALUES (?,?,?)";

    String chgUserNameSQL = "UPDATE users SET username=? where username=?";
    String findUserByUsernameSQL = "Select * FROM users where username=?";
    String getUserIdByLoginAndPasswordSQL = "SELECT id FROM users WHERE login=? AND password=?";
    String findUserByLoginAndPasswordSQL = "SELECT * from users WHERE login=? AND password=?";

    public JDBCAuthServiceImpl() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        logger.info("Драйвер JDBC загружен");
    }

    public Connection getConnection() {
        final String connectionStr = "jdbc:sqlite:" + LocalFileService.storagePath.toString() + "/users.db";
        try {
            return DriverManager.getConnection(connectionStr);
        } catch (SQLException e) {
            logger.fatal("Ошибка получения соединения: " + connectionStr, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    synchronized public Optional<User> addUser(String userName, String login, String password) {

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addUserSQL)
        ) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, userName);
            int result = preparedStatement.executeUpdate();

            if (result == 1) {
                int id = getUserIdByLoginAndPassword(login, password);
                if (id > 0) {
                    User user = new User((long) id, userName, login, password);
                    return Optional.of(user);
                }
            }
            lastError = new Exception("Неизвестная ошибка");
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Ошибка добавления пользователя", e);
            lastError = e;
            return Optional.empty();
        }
    }

    @Override
    synchronized public Optional<String> renameUser(String oldUserName, String newUserName) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(chgUserNameSQL);
        ) {
            preparedStatement.setString(2, oldUserName);
            preparedStatement.setString(1, newUserName);
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                lastError = new Exception("Не удалось переименовать пользователя");
                logger.warn("Не удалось переименовать пользователя из: " + oldUserName + " в: " + newUserName);
                return Optional.empty();
            }

        } catch (SQLException e) {
            lastError = e;
            logger.warn("Не удалось переименовать пользователя", e);
            return Optional.empty();
        }


        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findUserByUsernameSQL);
        ) {
            preparedStatement.setString(1, newUserName);
            try (ResultSet rs = preparedStatement.executeQuery();) {
                if (rs.next()) {
                    String nu = rs.getString("username");
                    return Optional.of(nu);
                }
            }

        } catch (SQLException e) {
            lastError = e;
            logger.warn("Не удалось переименовать пользователя", e);
            return Optional.empty();
        }
        lastError = new Exception("Нет данных");
        return Optional.empty();
    }

    @Override
    synchronized public int getUserIdByLoginAndPassword(String login, String password) {
        int id = -1;
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getUserIdByLoginAndPasswordSQL)
        ) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            lastError = e;
            logger.warn("Не удалось найти UserId", e);
        }
        return id;
    }

    @Override
    synchronized public Optional<User> findUserByLoginAndPassword(String login, String password) {

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findUserByLoginAndPasswordSQL);
        ) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    User newUser = new User((long) rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("login"),
                            rs.getString("password"));
                    return Optional.of(newUser);
                }
                lastError = new Exception(String.format("Пользователя с логином:%s и паролем:%s не существует", login, password));
                logger.warn(String.format("Пользователя с логином:%s и паролем:%s не существует", login, password));
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.warn("Ошибка поиска пользователя", e);
            lastError = e;
            return Optional.empty();
        }
    }

    @Override
    synchronized public String getLastDBError() {
        return lastError.getMessage();
    }

    @Override
    public void initDB(boolean doInit) {
        if(doInit){
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
            ) {
                statement.execute("Delete from users");
                // reset autoincrement counter
                statement.execute("delete from sqlite_sequence where name='users'");
                statement.execute("insert into users(login, password, username) values" +
                        "(1, 1, 'One')," +
                        "(2, 2, 'Two'), " +
                        "(3, 3, 'Three'), " +
                        "(4, 4, 'Four')");
                logger.debug("БД инициализирована");
            } catch (SQLException e) {
                logger.debug("Не удалось инициализировать БД", e);
                lastError = e;
            }
        }
    }
}
