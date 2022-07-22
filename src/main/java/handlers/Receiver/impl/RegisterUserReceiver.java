package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;
import java.util.Optional;

import static prefix.Prefix.*;

public class RegisterUserReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = REGISTER_NEW_USER;

    public RegisterUserReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(String message) throws IOException {
        if (Receiver.matchCommand(message, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик регистрации нового пользователя: " + message);
            registerUser(message);
            return true;
        }
        return false;
    }


    void registerUser(String message) throws IOException {

        // должна быть логика обработки возврата метода
        doRegister(message);

    }

    private boolean doRegister(String message) throws IOException {

        String[] parts = Receiver.parseMessage(message, 4);

        String login = parts[1];
        String password = parts[2];
        String username = parts[3];

        Optional<String> optionalUsername = mainHandler.getUsernameByLoginAndPassword(login, password);
        // если есть что-то по логину и паролю
        if (optionalUsername.isPresent()) {
//            mainHandler.out.writeUTF(REGISTER_ERR + " " + "Логин и пароль уже занят" + " " + message);
            mainHandler.write(String.format("%s логин и пароль уже занят: %s", REGISTER_ERR, message));
            System.out.println("Логин и пароль уже занят" + " " + message);
            return false;
        }

        // если Username присутствует в базе данных
        if (mainHandler.isUserPresent(username)) {
//            mainHandler.out.writeUTF(REGISTER_ERR + " " + "Пользователь уже зарегистрирован в системе" + " " + username);
            mainHandler.write(String.format("%s пользователь уже зарегистрирован в системе: %s", REGISTER_ERR, username));
            System.out.println("Пользователь уже зарегистрирован в системе" + " " + username);
            return false;
        }

        // все в порядке - регистрируемся
        mainHandler.addUser(username, login, password);
        mainHandler.userName = username;
        mainHandler.isLoggedIn = true;
//        mainHandler.out.writeUTF(String.format("%s %s", REGISTER_OK, username));
        mainHandler.write(String.format("%s %s", REGISTER_OK, username));

        System.out.println("Новый пользователь зарегистрировался");
        System.out.println("Username: " + username);
        System.out.println("Login: " + login);
        System.out.println("Password: " + password);
//        mainHandler.myServer.subscribe(mainHandler);
        mainHandler.subscribe();

        return true;
    }


}