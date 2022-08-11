package ru.igojig.fxmessenger.services;

import ru.igojig.fxmessenger.model.User;

import java.util.Optional;

public interface AuthService {
     Optional<String> getUsernameByLoginAndPassword(String login, String password);
    Optional<User> addUser(String userName, String login, String password);


    Optional<String> renameUser(String oldUser, String newUser) ;

    public int getUserIdByLoginAndPassword(String login, String password);

    Optional<User> findUserByLoginAndPassword(String login, String password);

    String getLastDBError();
}
