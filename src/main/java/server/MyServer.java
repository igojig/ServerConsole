package server;

import exchanger.Exchanger;
import handlers.ClientHandler;

import static prefix.Prefix.*;


import model.User;
import services.AuthService;
import services.impl.JDBCAuthServiceImpl;
import repository.JDBCRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {


    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clientHandlers;

    JDBCRepository repository;

    public MyServer(int port) throws IOException {
        clientHandlers = new ArrayList<>();


        try {
            repository = new JDBCRepository();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка подключения к базе данных");
        }
//        authService = new SimpleAuthServiceImpl();
        authService = new JDBCAuthServiceImpl(repository);

        serverSocket = new ServerSocket(port);
    }

    public void start() {
        System.out.println("Сервер запущен");
        System.out.println("--------------");

        while (true) {
            try {
                waitAndEstablishConnection();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }

    }

    private void waitAndEstablishConnection() throws IOException {
        Socket socket = waitSocket();

        establishConnection(socket);

    }

    private Socket waitSocket() throws IOException {
        System.out.println("Ожидние клиента.....");
        Socket socket = serverSocket.accept();

        System.out.println("Клиент подключился");
        return socket;
    }

    private void establishConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.doHandle();
    }

    synchronized public void subscribe(ClientHandler clientHandler) throws IOException {
        clientHandlers.add(clientHandler);
        sendLoggedUsers(true, clientHandler);
    }

    synchronized public void unsubscribe(ClientHandler clientHandler) throws IOException {
        clientHandlers.remove(clientHandler);
        sendLoggedUsers(false, clientHandler);
    }

    /**
     * @param mode          - true - пользователь добавился, false - удалился
     * @param clientHandler -
     * @throws IOException
     */
    private void sendLoggedUsers(boolean mode, ClientHandler clientHandler) throws IOException {
        StringBuilder sb = new StringBuilder();

        if (mode) {
            sb.append("+");
        } else {
            sb.append("-");
        }
        sb.append(clientHandler.getUserName()).append(" ");

        for (ClientHandler handler : clientHandlers) {
            if (handler.isLoggedIn()) {
                sb.append(handler.getUserName()).append(" ");
            }
        }

        for (ClientHandler handler : clientHandlers) {

            if (handler.isLoggedIn()) {
                handler.sendServerMessage(SERVER_MSG_CMD_PREFIX_LOGGED_USERS, sb.toString());
            }
        }
    }

    // посылаем когда пльзователь изменил имя
    public void sendUpdateUsers() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.isLoggedIn()) {
                sb.append(clientHandler.getUserName()).append(" ");
            }
        }

        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.isLoggedIn()) {
                clientHandler.sendServerMessage(CHANGE_USERNAME_NEW_LIST, sb.toString());
            }
        }
    }

    synchronized public boolean isAlreadyLogin(String username) {
        return clientHandlers.stream().anyMatch(o -> o.getUserName().equals(username));
    }

    synchronized public boolean sendPrivateMessage(Exchanger exchanger, ClientHandler sender) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.isLoggedIn() && clientHandler.getUser().equals(exchanger.getUser())) {
                clientHandler.sendClientMessage(sender.getUser(), message, PRIVATE_MSG_CMD_PREFIX);

                //дублируем сообшение себе
                sender.sendClientMessage(sender.getUserName(), message + "->" + sendToUserName, CLIENT_MSG_CMD_PREFIX);
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
    synchronized public void broadcastMessage(String message, ClientHandler sender, boolean mode) throws IOException {

        List<ClientHandler> list = getLoggedInUsers();

        if (mode) {
            for (ClientHandler clientHandler : list) {
                clientHandler.sendClientMessage(sender.getUserName(), message, CLIENT_MSG_CMD_PREFIX);
            }
        } else {
            for (ClientHandler clientHandler : list) {
                if (clientHandler != sender) {
                    clientHandler.sendClientMessage(sender.getUserName(), message, CLIENT_MSG_CMD_PREFIX);
                }
            }
        }

//        for (ClientHandler handler : clientHandlers) {
//            if (handler.isLoggedIn()) {
//                handler.sendClientMessage(sender.getUserName(), message, CLIENT_MSG_CMD_PREFIX);
//            }
//        }
    }

    /**
     * @param message
     * @param sender
     * @param mode    - true - сообщение рассылается всем, false - всем кроме себя
     * @throws IOException
     */
    synchronized public void broadcastServerMessage(String message, ClientHandler sender, boolean mode) throws IOException {

        List<ClientHandler> list = getLoggedInUsers();

        if (mode) {
            for (ClientHandler clientHandler : list) {
                clientHandler.sendServerMessage(SERVER_MSG_CMD_PREFIX, message);
            }
        } else {
            for (ClientHandler clientHandler : list) {
                if (clientHandler != sender) {
                    clientHandler.sendServerMessage(SERVER_MSG_CMD_PREFIX, message);
                }
            }
        }

//        for (ClientHandler handler : clientHandlers) {
//            if (handler.isLoggedIn()) {
//                handler.sendClientMessage(sender.getUserName(), message, CLIENT_MSG_CMD_PREFIX);
//            }
//        }
    }


    synchronized public void stop() throws IOException {


        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.closeSocket();
        }

        serverSocket.close();
        ((JDBCRepository) repository).closeConnection();
        System.out.println("-----------------");
        System.out.println("Сервер остановлен");
        System.exit(0);
    }

    public AuthService getAuthService() {
        return authService;
    }

    List<ClientHandler> getLoggedInUsers() {
        return clientHandlers.stream().filter(ClientHandler::isLoggedIn).toList();
    }

    public String getLasetDBError() {
        return authService.getLastDBError();
    }
}
