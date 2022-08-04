package repository;

import model.User;

import java.sql.*;
import java.util.Optional;

public class JDBCRepository {
    Connection connection;
    
    private static Throwable lastError;
    

    PreparedStatement getUsernameByLoginAndPasswordStatement;
    PreparedStatement addUserStatement;
    PreparedStatement isUserPresentStatement;

    PreparedStatement chgUserNameStatement;
    PreparedStatement getUserIdByLoginAndPassword;
    private PreparedStatement findUserByLoginAndPassword;
    private PreparedStatement findUserByUsername;

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
            
            findUserByLoginAndPassword =connection.prepareStatement("SELECT * from users WHERE login=? AND password=?");
            findUserByUsername =connection.prepareStatement("Select * FROM users where username=?");
            
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

    synchronized public Optional<String> renameUser(String oldUserName, String newUserName) {
        try {
            chgUserNameStatement.setString(1, oldUserName);
            chgUserNameStatement.setString(2, newUserName);
            int result = chgUserNameStatement.executeUpdate();

        } catch (SQLException e) {
            lastError=e;
            e.printStackTrace();
            return Optional.empty();
        }

        try {
            findUserByUsername.setString(1, newUserName);
            ResultSet rs=findUserByUsername.executeQuery();
            if(rs.next()){
                String nu=rs.getString("username");
                return Optional.of(nu);
            }
        } catch (SQLException e) {
            lastError=e;
            return Optional.empty();
        }
        return Optional.empty();
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

    public Optional<User> findUserByLoginAndPassword(User user) {
        try {
            findUserByLoginAndPassword.setString(1, user.getLogin());
            findUserByLoginAndPassword.setString(2, user.getPassword());
            ResultSet rs=findUserByLoginAndPassword.executeQuery();
            if(rs.next()){
                User newUser=new User((long) rs.getInt("id"), 
                        rs.getString("username"), 
                        rs.getString("login"),
                        rs.getString("password"));
                return Optional.of(newUser);
            }
        } catch (SQLException e) {
            lastError=e;
            return Optional.empty();
        }
        return Optional.empty();
    }

    public String getLastDBError() {
        return lastError.getMessage();
    }
}
