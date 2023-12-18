package ru.igojig.fxmessenger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.server.MyServer;
import ru.igojig.fxmessenger.services.LocalFileService;

import java.io.IOException;

public class ServerApp {

    /**
     * удаление списка пользователей и создание дефолтных при перезапуске:
     * [password]  [login]  [username]
     * 1          1        One
     * 2          2        Two
     * 3          3        Three
     * 4          4        Four
     */
    private static final boolean INIT_DB = true;

    /**
     * очистка истории сообщений при перезапуске
     */
    private static final boolean CLEAR_HISTORY = true;

    public static final int DEFAULT_PORT = 8186;

    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    public static void main(String[] args) {
        try {
            MyServer myServer = new MyServer(DEFAULT_PORT);

            LocalFileService.initStorage();
            LocalFileService.clearHistory(CLEAR_HISTORY);
            LocalFileService.copyDB();

            myServer.initDB(INIT_DB);

            myServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
