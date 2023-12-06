package ru.igojig.fxmessenger.handlers.Receiver.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.CMD_REQUEST_USERS;
import static ru.igojig.fxmessenger.prefix.Prefix.REGISTER_REQUEST;

public class RequestUsersReciever extends Receiver {

    private static final Logger logger= LogManager.getLogger(RequestUsersReciever.class);

    private static final Prefix REQUIRED_COMMAND = CMD_REQUEST_USERS;

    public RequestUsersReciever(ClientHandler mainHandler){
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик запроса списка пользователей: " + exchanger);
            mainHandler.sendLoggedUsers();
            return true;
        }
        return false;
    }
}
