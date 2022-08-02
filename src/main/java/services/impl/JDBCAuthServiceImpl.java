package services.impl;

import repository.JDBCRepository;
import services.AuthService;

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
    synchronized public boolean addUser(String userName, String login, String password) {
        return repository.addUser(userName, login, password);
    }

    @Override
    synchronized public boolean isUserPresentInDatabase(String username) {
        return repository.isUserPresentInDatabase(username);
    }

    @Override
    public boolean renameUser(String oldUsername, String newUsername) {
        return repository.renameUser(oldUsername, newUsername);
    }

    @Override
    public int getUserIdByLoginAndPassword(String login, String password) {
        return repository.getUserIdByLoginAndPassword(login, password);
    }
}
