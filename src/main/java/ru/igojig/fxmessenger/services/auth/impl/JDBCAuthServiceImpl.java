package ru.igojig.fxmessenger.services.auth.impl;

import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.repository.JDBCRepository;
import ru.igojig.fxmessenger.services.auth.AuthService;

import java.util.Optional;

public class JDBCAuthServiceImpl implements AuthService{

    private JDBCRepository repository;

    public JDBCAuthServiceImpl(JDBCRepository repository) {
        this.repository = repository;
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
    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        return repository.findUserByLoginAndPassword(login, password);
    }

    @Override
    public String getLastDBError() {
        return repository.getLastDBError();
    }
}
