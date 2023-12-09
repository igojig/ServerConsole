package ru.igojig.fxmessenger;

import ru.igojig.fxmessenger.server.MyServer;

import java.io.IOException;

public class ServerApp {

    /*  удаление списка пользователей и создание дефолтных при перезапуске:
        [password]  [login]  [username]
            1          1        One
            2          2        Two
            3          3        Three
            4          4        Four
     */
    private static final boolean CLEAR_DB = true;

    /*
        очистка истории сообщений при перезапуске
     */
    private static final boolean CLEAR_HISTORY = true;

    public static final int DEFAULT_PORT = 8186;

    public static void main(String[] args) {
        try {
            MyServer myServer = new MyServer(DEFAULT_PORT);

            if(CLEAR_HISTORY){
                myServer.clearHistory();
            }

            if(CLEAR_DB){
                myServer.clearDB();
            }

            myServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
