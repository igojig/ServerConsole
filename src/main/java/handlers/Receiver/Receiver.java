package handlers.Receiver;

import exchanger.Exchanger;
import handlers.ClientHandler;

import java.io.IOException;

abstract public class Receiver {
    protected ClientHandler mainHandler;

    public abstract boolean receive(Exchanger ex) throws IOException;

    public Receiver(ClientHandler mainHandler) {
        this.mainHandler = mainHandler;
    }

    protected static String[] parseMessage(String message, int parts) {
        return message.split("\\s+", parts);
    }

    protected static String[] parseMessage(String message) {
        return message.split("\\s+");
    }

    protected static boolean matchCommand(Exchanger ex, String command) {
//        return parseMessage(message, 2)[0].equals(command);
        return ex.getCommand().equals(command);
    }

}
