package ru.igojig.fxmessenger.handlers.Receiver.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class AuthMessageReceiver extends Receiver {

    private static final Logger logger = LogManager.getLogger(AuthMessageReceiver.class);

    private static final Prefix REQUIRED_COMMAND = AUTH_REQUEST;

    public AuthMessageReceiver(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик авторизации: " + exchanger);
            authenticateUser(exchanger);
            return true;
        }
        return false;
    }

    private void authenticateUser(Exchanger ex) throws IOException {
        // тут должна быть логика для отработки результатов
        processAuthentication(ex);
    }

    private boolean processAuthentication(Exchanger exchanger) throws IOException {
        logger.debug("Аутентификация: " + exchanger);

        UserExchanger userExchanger = exchanger.getChatExchanger(UserExchanger.class);

        // пользователь уже зарегистрирован в системе
        if (mainHandler.isAlreadyLogin(userExchanger.getUser())) {
            logger.debug("Пользователь: " + userExchanger.getUser() + " уже зарегистрирован в системе");
            mainHandler.sendMessage(AUTH_ERR, "пользователь уже зарегистрирован в системе", new UserExchanger(userExchanger.getUser()));
            return false;
        }

        Optional<User> user = mainHandler.findUserByLoginAndPassword(userExchanger.getUser().getLogin(), userExchanger.getUser().getPassword());
        if (user.isEmpty()) {
            String dbError = mainHandler.getLastDBError();
            logger.debug(dbError +":" +userExchanger.getUser());
            mainHandler.sendMessage(AUTH_ERR, dbError, new UserExchanger(userExchanger.getUser()));
            return false;
        }

        mainHandler.setUser(user.get());

        mainHandler.subscribe();
        mainHandler.sendMessage(AUTH_OK, "успешная авторизация", new UserExchanger(mainHandler.getUser()));
        logger.info("Пользователь подключился: " + user.get());

        return true;
    }
}
