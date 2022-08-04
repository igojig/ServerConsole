package handlers.Receiver.impl;

import exchanger.Exchanger;
import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;

import static prefix.Prefix.*;

public class PrivateMessageReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = PRIVATE_MSG_CMD_PREFIX;

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
//        String[] parts= Receiver.parseMessage(message, 3);
        if (!mainHandler.sendPrivateMessage(exchanger) {
//            mainHandler.out.writeUTF(SERVER_MSG_CMD_PREFIX + " пользователя: " + parts[.gitkeep] + " не существует");
//            Exchanger exchanger=new Exchanger(SERVER_MSG_CMD_PREFIX, exchanger.getMessage(), exchanger.getUser());
//            mainHandler.write(String.format("%s пользователя: %s не существует", SERVER_MSG_CMD_PREFIX, parts[1]));
        }
    }
}
