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
    public boolean receive(Exchanger ex) throws IOException {
        if (Receiver.matchCommand(ex, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик авторизации: " + ex);
            authenticateUser(ex);
            return true;
        }
        return false;
    }

    private void authenticateUser(Exchanger ex) throws IOException {

        // тут должна быть логика для отработки результатов
        processAuthentication(ex);
    }

    private boolean processAuthentication(Exchanger ex) throws IOException {
        logger.debug("Аутентификация: " + ex);

        UserExchanger userExchanger = ex.getChatExchanger(UserExchanger.class);

        // пользователь уже зарегистрирован в системе
        if (mainHandler.isAlreadyLogin(userExchanger.getUser())) {
            logger.debug("Пользователь: " + userExchanger.getUser() + " уже залогинен в системе");
            Exchanger exAnswer = new Exchanger(AUTH_ERR, "пользователь уже залогинен в системе", new UserExchanger(userExchanger.getUser()));
            mainHandler.writeObj(exAnswer);
            return false;
        }

        Optional<User> optUser = mainHandler.findUserByLoginAndPassword(userExchanger.getUser().getLogin(), userExchanger.getUser().getPassword());
        if (optUser.isEmpty()) {
            String dbError = mainHandler.getLastDBError();
            logger.debug(dbError +":" +userExchanger.getUser());
            Exchanger exAnswer = new Exchanger(AUTH_ERR, dbError, new UserExchanger(userExchanger.getUser()));
            mainHandler.writeObj(exAnswer);
            return false;
        }

        mainHandler.setUser(optUser.get());

        Exchanger ans = new Exchanger(AUTH_OK, "успешная авторизация", new UserExchanger(mainHandler.getUser()));

        mainHandler.writeObj(ans);
        mainHandler.subscribe();
        logger.info("Пользователь подключился: " + optUser.get());

        return true;

    }
}
