package ru.igojig.fxmessenger.handlers.Receiver;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;

abstract public class Receiver {
    protected ClientHandler mainHandler;

    public abstract boolean receive(Exchanger ex) throws IOException;

    public Receiver(ClientHandler mainHandler) {
        this.mainHandler = mainHandler;
    }

//    protected static String[] parseMessage(String message, int parts) {
//        return message.split("\\s+", parts);
//    }

//    protected static String[] parseMessage(String message) {
//        return message.split("\\s+");
//    }

    protected static boolean matchCommand(Exchanger ex, Prefix prefix) {
        return ex.getCommand() == prefix;
    }

}
