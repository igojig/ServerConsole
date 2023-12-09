package ru.igojig.fxmessenger.handlers.Receiver.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.UserChangeMode;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.CMD_REQUEST_USERS_LIST;

public class RequestUsersReceiver extends Receiver {

    private static final Logger logger= LogManager.getLogger(RequestUsersReceiver.class);

    private static final Prefix REQUIRED_COMMAND = CMD_REQUEST_USERS_LIST;

    public RequestUsersReceiver(ClientHandler mainHandler){
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик запроса списка пользователей: " + exchanger);
            mainHandler.sendLoggedUsers(UserChangeMode.ADD);
            return true;
        }
        return false;
    }
}
