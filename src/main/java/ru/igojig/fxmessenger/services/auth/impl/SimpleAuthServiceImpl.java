package ru.igojig.fxmessenger.services.auth.impl;

import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.auth.AuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*

 */
public class SimpleAuthServiceImpl implements AuthService {

    private static  List<User> clients;

    static {
        clients = new ArrayList<>(List.of(
                new User(1L,"Ivanov", "ivanov", "1"),
                new User(2L,"Petrov", "petrov", "1"),
                new User(3L,"Sidorov", "sidorov", "1"),
                new User(4L,"Fedorov", "fedorov", "1"),
                new User(5L,"Smirnov", "smirnov", "1")
        ));
    }

    public SimpleAuthServiceImpl()  {

    }

    @Override
    synchronized public Optional<User> addUser(String userName, String login, String password) {
        return Optional.empty();
    }

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

    @Override
    public void initDB(boolean doInit) {

    }
}
