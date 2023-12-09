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

public class RegisterUserReceiver extends Receiver {

    private static final Logger logger = LogManager.getLogger(RegisterUserReceiver.class);

    private static final Prefix REQUIRED_COMMAND = REGISTER_REQUEST;

    public RegisterUserReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик регистрации нового пользователя: " + exchanger);
            registerUser(exchanger);
            return true;
        }
        return false;
    }


    void registerUser(Exchanger exchanger) throws IOException {

        // должна быть логика обработки возврата метода
        doRegister(exchanger);

    }

    private boolean doRegister(Exchanger exchanger) throws IOException {

        UserExchanger userExchanger = exchanger.getChatExchanger(UserExchanger.class);

        String login = userExchanger.getUser().getLogin();
        String password = userExchanger.getUser().getPassword();
        String username = userExchanger.getUser().getUsername();

        Optional<User> user = mainHandler.addUser(username, login, password);

        if (user.isEmpty()) {
            String dbError = mainHandler.getLastDBError();
//            Exchanger registerResponse = new Exchanger(REGISTER_ERR, dbError, null);
//            mainHandler.writeObj(registerResponse);
            mainHandler.sendMessage(REGISTER_ERR, dbError, null);
            logger.warn("Ошибка добавления пользователя в базу данных: " + userExchanger.getUser() + ":" + dbError);
            return false;
        }

        mainHandler.setUser(user.get());

        mainHandler.subscribe();
        mainHandler.sendMessage(REGISTER_OK, "успешная регистрация", new UserExchanger(mainHandler.getUser()));
//        Exchanger response = new Exchanger(REGISTER_OK, "успешная регистрация", new UserExchanger(mainHandler.getUser()));
//        mainHandler.writeObj(response);

        logger.info("Новый пользователь зарегистрировался" + user.get());

        return true;
    }
}