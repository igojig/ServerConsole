package services;

import model.User;

import java.util.Optional;

public interface AuthService {
     Optional<String> getUsernameByLoginAndPassword(String login, String password);
    boolean addUser(String userName, String login, String password);

    boolean isUserPresentInDatabase(String username);

    Optional<String> renameUser(String oldUser, String newUser) ;

    public int getUserIdByLoginAndPassword(String login, String password);

    Optional<User> findUserByLoginAndPassword(User user);

    String getLastDBError();
}
