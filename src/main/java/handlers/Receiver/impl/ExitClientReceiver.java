package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;

import static prefix.Prefix.*;

public class ExitClientReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = END_CLIENT_CMD_PREFIX;

    public ExitClientReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(String message) throws IOException {
        if(Receiver.matchCommand(message, REQUIRED_COMMAND)){
            System.out.println("Вызываем обработчик ExitClient: " + message);
            mainHandler.closeConnection();
            return true;
        }
        return false;
    }
}
