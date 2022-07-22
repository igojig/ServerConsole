package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;
import java.util.Optional;

import static prefix.Prefix.*;

public class AuthMessageReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = AUTH_CMD_PREFIX;

    public AuthMessageReceiver(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public boolean receive(String message) throws IOException {
        if (Receiver.matchCommand(message, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик авторизации: " + message);
            authenticateUser(message);
            return true;
        }
        return false;
    }

    private void authenticateUser(String message) throws IOException {

        // тут должна быть логика для отработки результатов
        processAuthentication(message);
    }

    private boolean processAuthentication(String message) throws IOException {
        System.out.println("Аутентификация: " + message);
        String[] messageParts = Receiver.parseMessage(message, 3);

        String login = messageParts[1];
        String password = messageParts[2];

//        Optional<String> optionalUsername = mainHandler.myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
        Optional<String> optionalUsername = mainHandler.getUsernameByLoginAndPassword(login, password);
        if (optionalUsername.isEmpty()) {
            System.out.println("Пользователь не найден " + message);
            mainHandler.write(String.format("%s %s %s", AUTH_ERR_CMD_PREFIX, "пользователь не найден", message));
            return false;
        }

        if (mainHandler.isUserOccupied(optionalUsername.get())) {
            System.out.println("Пользователь: " + optionalUsername.get() + " уже залогинен в системе");
            mainHandler.write(String.format("%s пользователь: %s уже залогинен в системе", AUTH_ERR_CMD_PREFIX, optionalUsername.get()));
            return false;
        }

        mainHandler.userName = optionalUsername.get();
        mainHandler.isLoggedIn = true;
        mainHandler.write(String.format("%s %s", AUTH_OK_CMD_PREFIX, optionalUsername.get()));
//        mainHandler.myServer.subscribe(mainHandler);
        mainHandler.subscribe();
        System.out.println("Пользователь: " + optionalUsername.get() + " подключился");
        return true;

    }


}
