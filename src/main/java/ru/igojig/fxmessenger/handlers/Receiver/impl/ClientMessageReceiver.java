package ru.igojig.fxmessenger.handlers.Receiver.impl;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class ClientMessageReceiver extends Receiver {

    private static final Prefix REQUIRED_COMMAND = CLIENT_MSG;

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

        mainHandler.broadcastMessage(CLIENT_MSG, exchanger.getMessage(), true);
    }
}
