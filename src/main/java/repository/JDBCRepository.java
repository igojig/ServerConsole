package repository;

import java.sql.*;
import java.util.Optional;

public class JDBCRepository {
    Connection connection;

    PreparedStatement getUsernameByLoginAndPasswordStatement;
    PreparedStatement addUserStatement;
    PreparedStatement isUserPresentStatement;

    PreparedStatement chgUserNameStatement;
    PreparedStatement getUserIdByLoginAndPassword;

    public JDBCRepository() throws SQLException, ClassNotFoundException {
        openConnection();
        createStatements();
        System.out.println("Соединение с базой данных установлено");

    }

    public void openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
//        String dbLocation = getClass().getResource("users.db").toString();
//        System.out.println(dbLocation);
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");

    }

    public void createStatements() {
        try {
            getUsernameByLoginAndPasswordStatement = connection.prepareStatement("SELECT username FROM users WHERE login=? AND password=?");
            addUserStatement = connection.prepareStatement("INSERT INTO users (login, password, username) VALUES (?,?,?)");
            isUserPresentStatement = connection.prepareStatement("SELECT count(username) as qw FROM users where username=?");
            chgUserNameStatement = connection.prepareStatement("UPDATE users SET username=? where username=?");
            getUserIdByLoginAndPassword = connection.prepareStatement("SELECT id FROM users WHERE login=? AND password=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void closeConnection() {

        try {
            if (isUserPresentStatement != null) {
                isUserPresentStatement.close();
            }
            if (getUsernameByLoginAndPasswordStatement != null) {
                getUsernameByLoginAndPasswordStatement.close();
            }
            if (addUserStatement != null) {
                addUserStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Соединение с БД закрыто");
    }


    public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
        try {
            getUsernameByLoginAndPasswordStatement.setString(1, login);
            getUsernameByLoginAndPasswordStatement.setString(2, password);
            ResultSet resultSet = getUsernameByLoginAndPasswordStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getString(1));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    public boolean addUser(String userName, String login, String password) {
        try {
            addUserStatement.setString(1, login);
            addUserStatement.setString(2, password);
            addUserStatement.setString(3, userName);
            int result = addUserStatement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    // присутствует ли пользователь в базе данных

    public boolean isUserPresentInDatabase(String username) {
        int result = 0;
        try {
            isUserPresentStatement.setString(1, username);
            ResultSet resultSet = isUserPresentStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    synchronized public boolean renameUser(String oldUsername, String newUsername) {
        try {
            chgUserNameStatement.setString(1, newUsername);
            chgUserNameStatement.setString(2, oldUsername);
            int result = chgUserNameStatement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    synchronized public int getUserIdByLoginAndPassword(String login, String password) {
        int id = -1;
        try {
            getUserIdByLoginAndPassword.setString(1, login);
            getUserIdByLoginAndPassword.setString(2, password);
            ResultSet resultSet = getUserIdByLoginAndPassword.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}
