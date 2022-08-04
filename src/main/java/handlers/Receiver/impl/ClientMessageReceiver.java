package handlers.Receiver.impl;

import exchanger.Exchanger;
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
    public boolean receive(Exchanger exchanger) throws IOException {
        if(Receiver.matchCommand(exchanger, REQUIRED_COMMAND)){
            System.out.println("Вызываем обработчик ClientMessage: " + exchanger);
            processClientMessage(exchanger);
            return true;
        }
        return false;
    }

    private void processClientMessage(Exchanger exchanger) throws IOException {
//        String[] parts= Receiver.parseMessage(message, 2);

        mainHandler.broadcastMessage(exchanger.getMessage(), true);
    }
}
