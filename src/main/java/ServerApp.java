import server.MyServer;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class ServerApp {

    public static final int DEFAULT_PORT = 8186;


    public static void main(String[] args) {
        try {
            MyServer myServer = new MyServer(DEFAULT_PORT);
            myServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
