package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;

import static prefix.Prefix.*;

public class StopServerReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = STOP_SERVER_CMD_PREFIX;


    public StopServerReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(String message) throws IOException {
        if (Receiver.matchCommand(message, REQUIRED_COMMAND)) {
            System.out.println("Вызываем остановку сервера: " + message);
            mainHandler.stop();
            return true;
        }
        return false;
    }
}
