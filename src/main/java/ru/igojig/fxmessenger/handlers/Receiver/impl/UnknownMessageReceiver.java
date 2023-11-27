package ru.igojig.fxmessenger.handlers.Receiver.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;

import java.io.IOException;

// для неизвестных комманд или из-за глюков
public class UnknownMessageReceiver extends Receiver {

    private static final Logger logger= LogManager.getLogger(UnknownMessageReceiver.class);
    public UnknownMessageReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    public UnknownMessageReceiver(){
        super(null);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        logger.error("Странно, неизвестное сообщение )): " +exchanger);
        return true;
    }
}
