package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;

import static prefix.Prefix.*;

public class ClientMessageReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = CLIENT_MSG_CMD_PREFIX;

    public ClientMessageReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(String message) throws IOException {
        if(Receiver.matchCommand(message, REQUIRED_COMMAND)){
            System.out.println("Вызываем обработчик ClientMessage: " + message);
            processClientMessage(message);
            return true;
        }
        return false;
    }

    private void processClientMessage(String message) throws IOException {
        String[] parts= Receiver.parseMessage(message, 2);
        mainHandler.broadcastMessage(parts[1], true);
    }
}
