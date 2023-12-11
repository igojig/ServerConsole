package ru.igojig.fxmessenger.handlers.Receiver.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.UserChangeMode;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class ExitClientReceiver extends Receiver {

    private static final Logger logger= LogManager.getLogger(ExitClientReceiver.class);
    private static final Prefix REQUIRED_COMMAND = END_CLIENT;

    public ExitClientReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if(Receiver.matchCommand(exchanger, REQUIRED_COMMAND)){
            logger.debug("Вызываем обработчик ExitClient: " + exchanger);
            mainHandler.stopWaitTimeOutThread();
            mainHandler.unsubscribe();
            mainHandler.sendLoggedUsers(UserChangeMode.REMOVE);
            mainHandler.closeConnection();
            return true;
        }
        return false;
    }
}
