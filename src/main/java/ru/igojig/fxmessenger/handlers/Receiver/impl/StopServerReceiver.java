package ru.igojig.fxmessenger.handlers.Receiver.impl;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class StopServerReceiver extends Receiver {

    private static final Prefix REQUIRED_COMMAND = STOP_SERVER;


    public StopServerReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            System.out.println("Вызываем остановку сервера: " + exchanger);
            mainHandler.stop();
            return true;
        }
        return false;
    }
}
