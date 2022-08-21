package ru.igojig.fxmessenger.services.auth;

import ru.igojig.fxmessenger.model.User;

import java.util.Optional;

public interface AuthService {
//     Optional<String> getUsernameByLoginAndPassword(String login, String password);
    Optional<User> addUser(String username, String login, String password);


    Optional<String> renameUser(String oldUserName, String newUserName) ;

//    public int getUserIdByLoginAndPassword(String login, String password);

    Optional<User> findUserByLoginAndPassword(String login, String password);

    String getLastDBError();
}
