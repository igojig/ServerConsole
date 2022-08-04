package handlers.Receiver.impl;

import exchanger.Exchanger;
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
    public boolean receive(Exchanger exchanger) throws IOException {
        if(Receiver.matchCommand(exchanger, REQUIRED_COMMAND)){
            System.out.println("Вызываем обработчик ExitClient: " + exchanger);
            mainHandler.closeConnection();
            return true;
        }
        return false;
    }
}
