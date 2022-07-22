package services.impl;

import model.User;
import services.AuthService;

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

    @Override
    synchronized public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
        return clients.stream().
                filter(o -> o.login().equalsIgnoreCase(login) && o.password().equalsIgnoreCase(password))
                .findAny()
                .map(User::userName);
    }

    @Override
    synchronized public void addUser(String userName, String login, String password) {
        clients.add(new User(userName, login, password));
    }

    @Override
    synchronized public boolean isUserPresent(String username) {
       return clients.stream()
                .map(User::userName)
                .anyMatch(o->o.equalsIgnoreCase(username));
    }
}
