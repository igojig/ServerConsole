package ru.igojig.fxmessenger.handlers.Receiver.impl;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class PrivateMessageReceiver extends Receiver {

    private static final Prefix REQUIRED_COMMAND = PRIVATE_MSG;

    public PrivateMessageReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик PrivateMessage: " + exchanger);
            processPrivateMessage(exchanger);
            return true;
        }
        return false;
    }

    private void processPrivateMessage(Exchanger exchanger) throws IOException {
//        UserExchanger userExchanger=(UserExchanger)exchanger.getChatExchanger();
        UserExchanger userExchanger=exchanger.getChatExchanger(UserExchanger.class);
        User sendToUser=userExchanger.getUser();

        if (!mainHandler.sendPrivateMessage(exchanger.getMessage(), sendToUser)) {
            System.out.println("Пользователя: "+ sendToUser + " не существует");
            Exchanger ex=new Exchanger(PRIVATE_MSG_ERR, "пользователь не найден", new UserExchanger(sendToUser));
            mainHandler.writeObj(ex);
        }
    }
}
