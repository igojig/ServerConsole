package services.impl;

import model.User;
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
    public Optional<String> renameUser(String oldUserName, String newUserName) {
        return repository.renameUser(oldUserName, newUserName);
    }

    @Override
    public int getUserIdByLoginAndPassword(String login, String password) {
        return repository.getUserIdByLoginAndPassword(login, password);
    }

    @Override
    public Optional<User> findUserByLoginAndPassword(User user) {
        return repository.findUserByLoginAndPassword(user);
    }

    @Override
    public String getLastDBError() {
        return repository.getLastDBError();
    }
}
