package ru.igojig.fxmessenger.server;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.igojig.fxmessenger.ServerApp;
import ru.igojig.fxmessenger.exchanger.UserChangeMode;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserListExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.repository.JDBCRepository;
import ru.igojig.fxmessenger.services.auth.AuthService;
import ru.igojig.fxmessenger.services.auth.impl.JDBCAuthServiceImpl;
import ru.igojig.fxmessenger.services.storage.HistoryService;
import ru.igojig.fxmessenger.services.storage.impl.FileHistoryServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class MyServer {

    private static final Logger logger = LogManager.getLogger(MyServer.class);

    private final ServerSocket serverSocket;

    @Getter
    private final AuthService authService;
    private final List<ClientHandler> clientHandlers;

    private final JDBCRepository repository;

    private final HistoryService historyService;

    public MyServer(int port) throws IOException {
        clientHandlers = new ArrayList<>();

        try {
            repository = new JDBCRepository();
        } catch (SQLException | ClassNotFoundException e) {
            logger.fatal("Драйвер JDBC не загружен", e);
            throw new RuntimeException(e);
        }
        authService = new JDBCAuthServiceImpl(repository);
        historyService = new FileHistoryServiceImpl();

        serverSocket = new ServerSocket(port);
    }

    public void start() {
        logger.info("Сервер запущен");

        while (true) {
            try {
                waitAndEstablishConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitAndEstablishConnection() throws IOException {
        Socket socket = waitSocket();
        establishConnection(socket);
    }

    private Socket waitSocket() throws IOException {
        logger.debug("Ожидание клиента.....");
        Socket socket = serverSocket.accept();

        logger.debug("Клиент подключился");
        return socket;
    }

    private void establishConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.doHandle();
    }

    synchronized public void subscribe(ClientHandler clientHandler) throws IOException {
        clientHandlers.add(clientHandler);
    }

    synchronized public void unsubscribe(ClientHandler clientHandler) throws IOException {
        clientHandlers.remove(clientHandler);
    }

    synchronized public void sendLoggedUsers(ClientHandler clientHandler, UserChangeMode userChangeMode) throws IOException {
        UserListExchanger userListExchanger = new UserListExchanger();
        List<User> userList = new ArrayList<>();

        userListExchanger.setUserChangeMode(userChangeMode);
        userListExchanger.setChangedUser(clientHandler.getUser());

        clientHandlers.forEach(u -> userList.add(u.getUser()));

        userListExchanger.setUserList(userList);

        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(LOGGED_USERS, "обновление списка пользователей", userListExchanger);
        }
    }

    synchronized public boolean isAlreadyLogin(User user) {
        Optional<User> optionalUser = authService.findUserByLoginAndPassword(user.getLogin(), user.getPassword());
        return optionalUser.filter(value -> clientHandlers.stream().anyMatch(handler -> handler.getUser().getId().equals(value.getId()))).isPresent();
    }

    synchronized public boolean sendPrivateMessage(String message, ClientHandler sender, User sendToUser) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUser().getId().equals(sendToUser.getId())) {
                clientHandler.sendMessage(PRIVATE_MSG, message, new UserExchanger(sender.getUser()));

                //дублируем сообщение себе
                sender.sendMessage(CLIENT_MSG, message + "-->[" + sendToUser.getUsername() + "]", new UserExchanger(sender.getUser()));
                return true;
            }
        }
        return false;
    }

    /**
     * @param message
     * @param sender
     * @param mode    - true - сообщение рассылается всем, false - всем кроме себя
     * @throws IOException
     */
    synchronized public void broadcastMessage(Prefix prefix, String message, ClientHandler sender, boolean mode) throws IOException {

        if (mode) {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.sendMessage(prefix, message, new UserExchanger(sender.getUser()));
            }
            return;
        }

        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != sender) {
                clientHandler.sendMessage(prefix, message, new UserExchanger(sender.getUser()));
            }
        }
    }

    synchronized public void stop() throws IOException {

        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.closeSocket();
        }

        serverSocket.close();
        logger.info("Сервер остановлен");

        System.exit(0);
    }

    synchronized public String getLastDBError() {
        return authService.getLastDBError();
    }

    synchronized public List<String> getHistory(ClientHandler clientHandler) {
        return historyService.loadHistory(clientHandler.getUser());
    }

    synchronized public void saveHistory(List<String> history, ClientHandler clientHandler) {
        historyService.saveHistory(clientHandler.getUser(), history);
    }

    public void clearHistory(){
        historyService.clearHistory();
    }

    public void clearDB() {
        repository.initDatabase();
    }
}
