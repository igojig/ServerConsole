package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;

// для неизвестных комманд или из-за глюков
public class UnknownMessageReceiver extends Receiver {
//    private static final String REQUIRED_COMMAND = AUTH_CMD_PREFIX;


    public UnknownMessageReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    public UnknownMessageReceiver(){
        super(null);
    }

    @Override
    public boolean receive(String message) throws IOException {
        System.out.println("Странно, неизвестное сообщение )): " + message);
        return true;
    }
}
