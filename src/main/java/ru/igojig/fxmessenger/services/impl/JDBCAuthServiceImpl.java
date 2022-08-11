package ru.igojig.fxmessenger.services.impl;

import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.repository.JDBCRepository;
import ru.igojig.fxmessenger.services.AuthService;

import java.util.Optional;

public class JDBCAuthServiceImpl implements AuthService{

    JDBCRepository repository;

    public JDBCAuthServiceImpl(JDBCRepository repository) {
        this.repository = repository;
    }


    @Override
    synchronized public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
        return  repository.getUsernameByLoginAndPassword(login, password);
    }

    @Override
    synchronized public Optional<User> addUser(String userName, String login, String password) {
        return repository.addUser(userName, login, password);
    }

    @Override
    public Optional<String> renameUser(String oldUserName, String newUserName) {
        return repository.renameUser(oldUserName, newUserName);
    }

    @Override
    public int getUserIdByLoginAndPassword(String login, String password) {
        return repository.getUserIdByLoginAndPassword(login, password);
    }

    @Override
    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        return repository.findUserByLoginAndPassword(login, password);
    }

    @Override
    public String getLastDBError() {
        return repository.getLastDBError();
    }
}
