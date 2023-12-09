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

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class PrivateMessageReceiver extends Receiver {

    private static final Logger logger= LogManager.getLogger(PrivateMessageReceiver.class);

    private static final Prefix REQUIRED_COMMAND = PRIVATE_MSG;

    public PrivateMessageReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик PrivateMessage: " + exchanger);
            processPrivateMessage(exchanger);
            return true;
        }
        return false;
    }

    private void processPrivateMessage(Exchanger exchanger) throws IOException {
        UserExchanger userExchanger=exchanger.getChatExchanger(UserExchanger.class);
        User sendToUser=userExchanger.getUser();

        if (!mainHandler.sendPrivateMessage(exchanger.getMessage(), sendToUser)) {
            logger.warn("Пользователя: "+ sendToUser + " не существует");
            mainHandler.sendMessage(PRIVATE_MSG_ERR, "пользователь не найден", new UserExchanger(sendToUser));
//            Exchanger response=new Exchanger(PRIVATE_MSG_ERR, "пользователь не найден", new UserExchanger(sendToUser));
//            mainHandler.writeObj(response);
        }
    }
}
