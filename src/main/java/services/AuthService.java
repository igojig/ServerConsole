package services;

import java.util.Optional;

public interface AuthService {
     Optional<String> getUsernameByLoginAndPassword(String login, String password);
    boolean addUser(String userName, String login, String password);

    boolean isUserPresentInDatabase(String username);

    boolean renameUser(String oldUsername, String newUsername) ;

}
