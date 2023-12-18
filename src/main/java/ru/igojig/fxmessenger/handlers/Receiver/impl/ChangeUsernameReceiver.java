package ru.igojig.fxmessenger.handlers.Receiver.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.UserChangeMode;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class ChangeUsernameReceiver extends Receiver {

    private static final Logger logger = LogManager.getLogger(ChangeUsernameReceiver.class);

    private static final Prefix REQUIRED_COMMAND = CHANGE_USERNAME_REQUEST;

    public ChangeUsernameReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {

        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик смены имени пользователя: " + exchanger);
            processChangeName(exchanger);
            return true;
        }
        return false;
    }

    public boolean processChangeName(Exchanger exchanger) throws IOException {
        UserExchanger userExchanger = exchanger.getChatExchanger(UserExchanger.class);

        String oldUserName = userExchanger.getUser().getUsername();
        Optional<String> newUsername = mainHandler.changeUsername(mainHandler.getUser().getUsername(), oldUserName);
        if (newUsername.isPresent()) {
            logger.info("Пользователь: " + mainHandler.getUser() + " сменил имя на: " + newUsername.get());
            mainHandler.broadcastMessage(SERVER_MSG,
                    "Пользователь [" + mainHandler.getUser().getUsername() + "] сменил имя на [" + newUsername.get() + "]", false);
            mainHandler.getUser().setUsername(newUsername.get());

            mainHandler.sendMessage(CHANGE_USERNAME_OK, null, new UserExchanger(mainHandler.getUser()));
            mainHandler.sendLoggedUsers(UserChangeMode.CHANGE_NAME);
            return true;
        }

        String dbError = mainHandler.getLastDBError();
        mainHandler.sendMessage(CHANGE_USERNAME_ERR, dbError,
                new UserExchanger(new User(null, userExchanger.getUser().getUsername(), null, null)));
        logger.debug("Не удалось сменить имя: " + exchanger + dbError);
        return false;
    }
}
