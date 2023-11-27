package ru.igojig.fxmessenger.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.model.User;

import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Optional;


public class JDBCRepository {
//    Connection connection;

     private static final Logger logger= LogManager.getLogger(JDBCRepository.class);

    private static Throwable lastError;

    String getUsernameByLoginAndPasswordSQL = "SELECT username FROM users WHERE login=? AND password=?";

    String addUserSQL = "INSERT INTO users (login, password, username) VALUES (?,?,?)";

    String chgUserNameSQL = "UPDATE users SET username=? where username=?";
    String findUserByUsernameSQL = "Select * FROM users where username=?";
    String getUserIdByLoginAndPasswordSQL = "SELECT id FROM users WHERE login=? AND password=?";
    String findUserByLoginAndPasswordSQL = "SELECT * from users WHERE login=? AND password=?";

//    isUserPresentStatement =connection.prepareStatement("SELECT count(username) as qw FROM users where username=?");


//    PreparedStatement getUsernameByLoginAndPasswordStatement;
//    PreparedStatement addUserStatement;
//    PreparedStatement isUserPresentStatement;

//    PreparedStatement chgUserNameStatement;
//    PreparedStatement getUserIdByLoginAndPassword;
//    private PreparedStatement findUserByLoginAndPassword;
//    private PreparedStatement findUserByUsername;

    public JDBCRepository() throws SQLException, ClassNotFoundException {
//        openConnection();
        Class.forName("org.sqlite.JDBC");
//        createStatements();
        logger.info("Драйвер JDBC загружен");

    }

//    public void openConnection() throws ClassNotFoundException, SQLException {
//        Class.forName("org.sqlite.JDBC");
////        String dbLocation = getClass().getResource("users.db").toString();
////        System.out.println(dbLocation);
////        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
//
//    }

    public Connection getConnection() {
//        String connectionStr="jdbc:sqlite:users.db";
        String connectionStr="jdbc:sqlite::resource:users.db";

//        String dbName = getClass().getResource("/users.db").toString();
//
//        connectionStr+=dbName;
//        System.out.println(connectionStr);
        System.out.println(connectionStr);
        try {
            return DriverManager.getConnection(connectionStr);
        } catch (SQLException e) {
            logger.fatal("Ошибка получения соединения: " + connectionStr, e);
            throw new RuntimeException(e);
        }
    }

//
//    public void createStatements() {
//        try {
//            getUsernameByLoginAndPasswordStatement = connection.prepareStatement("SELECT username FROM users WHERE login=? AND password=?");
//            addUserStatement = connection.prepareStatement("INSERT INTO users (login, password, username) VALUES (?,?,?)");
//            isUserPresentStatement = connection.prepareStatement("SELECT count(username) as qw FROM users where username=?");
//            chgUserNameStatement = connection.prepareStatement("UPDATE users SET username=? where username=?");
//            getUserIdByLoginAndPassword = connection.prepareStatement("SELECT id FROM users WHERE login=? AND password=?");
//
//            findUserByLoginAndPassword = connection.prepareStatement("SELECT * from users WHERE login=? AND password=?");
//            findUserByUsername = connection.prepareStatement("Select * FROM users where username=?");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//
//    public void closeConnection() {
//
//        try {
//            if (isUserPresentStatement != null) {
//                isUserPresentStatement.close();
//            }
//            if (getUsernameByLoginAndPasswordStatement != null) {
//                getUsernameByLoginAndPasswordStatement.close();
//            }
//            if (addUserStatement != null) {
//                addUserStatement.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            if (connection != null) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Соединение с БД закрыто");
//    }

//
//    public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
//        try (
//                Connection connection = getConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement(getUsernameByLoginAndPasswordSQL);
//        ) {
//            preparedStatement.setString(1, login);
//            preparedStatement.setString(2, password);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    return Optional.of(resultSet.getString("username"));
//                } else {
//                    lastError=new Exception("Нет данных");
//                    return Optional.empty();
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            lastError = e;
//            return Optional.empty();
//        }


//        try {
//            getUsernameByLoginAndPasswordStatement.setString(1, login);
//            getUsernameByLoginAndPasswordStatement.setString(2, password);
//            ResultSet resultSet = getUsernameByLoginAndPasswordStatement.executeQuery();
//            if (resultSet.next()) {
//                return Optional.of(resultSet.getString("username"));
//            } else {
//                return Optional.empty();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }


    public Optional<User> addUser(String userName, String login, String password) {

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addUserSQL);) {
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

        } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("Ошибка добавления пользователя", e);
            lastError = e;
            return Optional.empty();
        }
        lastError=new Exception("Нет данных");
        return Optional.empty();

//        try {
//            addUserStatement.setString(1, login);
//            addUserStatement.setString(2, password);
//            addUserStatement.setString(3, userName);
//            int result = addUserStatement.executeUpdate();
//            if (result == 1) {
//                int id = getUserIdByLoginAndPassword(login, password);
//                if (id > 0) {
//                    User user = new User((long) id, userName, login, password);
//                    return Optional.of(user);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            lastError = e;
//            return Optional.empty();
//        }
//        return Optional.empty();
    }

//    // присутствует ли пользователь в базе данных
//
//    public boolean isUserPresentInDatabase(String username) {
//        int result = 0;
//        try {
//            isUserPresentStatement.setString(1, username);
//            ResultSet resultSet = isUserPresentStatement.executeQuery();
//            if (resultSet.next()) {
//                result = resultSet.getInt(1);
//            }
//            return result > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return true;
//        }
//    }

    synchronized public Optional<String> renameUser(String oldUserName, String newUserName) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(chgUserNameSQL);
        ) {
            preparedStatement.setString(2, oldUserName);
            preparedStatement.setString(1, newUserName);
            int result = preparedStatement.executeUpdate();
//            System.out.println(result);
            if (result == 0) {
                lastError=new Exception("Не удалось переименовать пользователя");
                logger.warn("Не удалось переименовать пользователя из: " + oldUserName + " в: " + newUserName);
                return Optional.empty();
            }

        } catch (SQLException e) {
            lastError = e;
//            e.printStackTrace();
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
        lastError=new Exception("Нет данных");
        return Optional.empty();

//
//        //___________________________
//        try {
//            chgUserNameStatement.setString(2, oldUserName);
//            chgUserNameStatement.setString(1, newUserName);
//            int result = chgUserNameStatement.executeUpdate();
//            System.out.println(result);
//
//        } catch (SQLException e) {
//            lastError = e;
//            e.printStackTrace();
//            return Optional.empty();
//        }
//
//        try {
//            findUserByUsername.setString(1, newUserName);
//            ResultSet rs = findUserByUsername.executeQuery();
//            if (rs.next()) {
//                String nu = rs.getString("username");
//                return Optional.of(nu);
//            }
//        } catch (SQLException e) {
//            lastError = e;
//            return Optional.empty();
//        }
//        return Optional.empty();
    }

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
//            e.printStackTrace();
            logger.warn("Не удалось найти UserId", e);
        }
        return id;


//        int id = -1;
//        try {
//            getUserIdByLoginAndPassword.setString(1, login);
//            getUserIdByLoginAndPassword.setString(2, password);
//            ResultSet resultSet = getUserIdByLoginAndPassword.executeQuery();
//
//            if (resultSet.next()) {
//                id = resultSet.getInt("id");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return id;
    }

    public Optional<User> findUserByLoginAndPassword(String login, String password) {

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findUserByLoginAndPasswordSQL);
        ) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            try (ResultSet rs = preparedStatement.executeQuery();) {
                if (rs.next()) {
                    User newUser = new User((long) rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("login"),
                            rs.getString("password"));
                    return Optional.of(newUser);
                }
                lastError=new Exception(String.format("Поользователя с логином:%s и паролем:%s не существует", login, password));
                logger.warn(String.format("Поользователя с логином:%s и паролем:%s не существует", login, password));
                return Optional.empty();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            logger.warn("Не удалось найи пользователя",e);
            lastError = e;
            return Optional.empty();
        }

//        try {
//            findUserByLoginAndPassword.setString(1, login);
//            findUserByLoginAndPassword.setString(2, password);
//            ResultSet rs = findUserByLoginAndPassword.executeQuery();
//            if (rs.next()) {
//                User newUser = new User((long) rs.getInt("id"),
//                        rs.getString("username"),
//                        rs.getString("login"),
//                        rs.getString("password"));
//                return Optional.of(newUser);
//            }
//        } catch (SQLException e) {
//            lastError = e;
//            return Optional.empty();
//        }
//        return Optional.empty();
    }

    public String getLastDBError() {

        return lastError.getMessage();

    }
}
