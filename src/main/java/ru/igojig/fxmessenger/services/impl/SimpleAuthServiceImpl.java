package ru.igojig.fxmessenger.services.impl;

import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.AuthService;

import java.util.List;
import java.util.Optional;

public class SimpleAuthServiceImpl implements AuthService {

    private static  List<User> clients;

//    static {
//        clients = new ArrayList<>(List.of(
//                new User("Ivanov", "ivanov", "1"),
//                new User("Petrov", "petrov", "1"),
//                new User("Sidorov", "sidorov", "1"),
//                new User("Fedorov", "fedorov", "1"),
//                new User("Smirnov", "smirnov", "1")
//        ));
//    }

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
    synchronized public Optional<User> addUser(String userName, String login, String password) {
        return Optional.empty();
    }

//    @Override
//    synchronized public boolean isUserPresentInDatabase(String username) {
//       return clients.stream()
//                .map(User::getUsername)
//                .anyMatch(o->o.equalsIgnoreCase(username));
//    }

    @Override
    public Optional<String> renameUser(String oldUserName, String newUsername) {
        return Optional.empty();
    }

    @Override
    public int getUserIdByLoginAndPassword(String login, String password) {
        return -1;
    }

    @Override
    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        return Optional.empty();
    }

    @Override
    public String getLastDBError() {
        return null;
    }
}
