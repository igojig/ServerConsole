package services;

import java.util.Optional;

public interface AuthService {
    Optional<String> getUsernameByLoginAndPassword(String login, String password);
    void addUser(String userName, String login, String password);

    boolean isUserPresent(String username);

}
