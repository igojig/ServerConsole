package services.impl;

import model.User;
import services.AuthService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimpleAuthServiceImpl implements AuthService {

    private static final List<User> clients;

    static {
        clients = new ArrayList<>(List.of(
                new User("Ivanov", "ivanov", "1"),
                new User("Petrov", "petrov", "1"),
                new User("Sidorov", "sidorov", "1"),
                new User("Fedorov", "fedorov", "1"),
                new User("Smirnov", "smirnov", "1")
        ));
    }

    public SimpleAuthServiceImpl()  {

    }

    @Override
    synchronized public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
        return clients.stream().
                filter(o -> o.getLogin().equalsIgnoreCase(login) && o.getPassword().equalsIgnoreCase(password))
                .findAny()
                .map(User::getUsername);
    }

    @Override
    synchronized public boolean addUser(String userName, String login, String password) {
        return clients.add(new User(userName, login, password));
    }

    @Override
    synchronized public boolean isUserPresentInDatabase(String username) {
       return clients.stream()
                .map(User::getUsername)
                .anyMatch(o->o.equalsIgnoreCase(username));
    }

    @Override
    public boolean renameUser(String oldUserName, String newUsername) {
        return false;
    }

    @Override
    public int getUserIdByLoginAndPassword(String login, String password) {
        return -1;
    }
}
