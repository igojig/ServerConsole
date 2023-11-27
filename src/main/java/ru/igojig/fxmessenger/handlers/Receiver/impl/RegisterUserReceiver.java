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

    private static final Logger logger= LogManager.getLogger(RegisterUserReceiver.class);

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

//        UserExchanger userExchanger=(UserExchanger)exchanger.getChatExchanger();
        UserExchanger userExchanger=exchanger.getChatExchanger(UserExchanger.class);

        String login=userExchanger.getUser().getLogin();
        String password=userExchanger.getUser().getPassword();
        String username=userExchanger.getUser().getUsername();

        Optional<User> optUser=mainHandler.addUser(username, login, password);

        if(optUser.isEmpty()){
            String dbError= mainHandler.getLastDBError();
            Exchanger exAnswer=new Exchanger(REGISTER_ERR, dbError, null);
            mainHandler.writeObj(exAnswer);
            logger.warn("Ошибка добавления пользователя в базу данных: " + userExchanger.getUser() + ":" + dbError);
            return false;
        }

        mainHandler.user=optUser.get();

        Exchanger ex=new Exchanger(REGISTER_OK, "успешная регистрация", new UserExchanger(mainHandler.user));
        mainHandler.writeObj(ex);

        logger.info("Новый пользователь зарегистрировался" + optUser.get());
        mainHandler.subscribe();

        return true;
    }


}