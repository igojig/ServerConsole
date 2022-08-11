package ru.igojig.fxmessenger.handlers.Receiver.impl;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;

import java.io.IOException;

// для неизвестных комманд или из-за глюков
public class UnknownMessageReceiver extends Receiver {

    public UnknownMessageReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    public UnknownMessageReceiver(){
        super(null);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        System.out.println("Странно, неизвестное сообщение )): " +exchanger);
        return true;
    }
}
