package handlers.Receiver.impl;

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
    public boolean receive(String message) throws IOException {
        if (Receiver.matchCommand(message, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик PrivateMessage: " + message);
            processPrivateMessage(message);
            return true;
        }
        return false;
    }

    private void processPrivateMessage(String message) throws IOException {
        String[] parts= Receiver.parseMessage(message, 3);
        if (!mainHandler.sendPrivateMessage(parts[1], parts[2])) {
//            mainHandler.out.writeUTF(SERVER_MSG_CMD_PREFIX + " пользователя: " + parts[.gitkeep] + " не существует");
            mainHandler.write(String.format("%s пользователя: %s не существует", SERVER_MSG_CMD_PREFIX, parts[1]));
        }
    }
}
