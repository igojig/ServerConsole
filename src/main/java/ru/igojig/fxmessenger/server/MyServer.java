package ru.igojig.fxmessenger.server;

import ru.igojig.fxmessenger.exchanger.impl.UserListExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;

import static ru.igojig.fxmessenger.prefix.Prefix.*;


import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.services.auth.AuthService;
import ru.igojig.fxmessenger.services.auth.impl.JDBCAuthServiceImpl;
import ru.igojig.fxmessenger.repository.JDBCRepository;
import ru.igojig.fxmessenger.services.storage.HistoryService;
import ru.igojig.fxmessenger.services.storage.impl.FileHistoryServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MyServer {


    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clientHandlers;

    JDBCRepository repository;

    HistoryService historyService;

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
        historyService=new FileHistoryServiceImpl();

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
    synchronized private void sendLoggedUsers(boolean mode, ClientHandler clientHandler) throws IOException {

        UserListExchanger userListExchanger =new UserListExchanger();

        if (mode) {
            userListExchanger.setMode(UserListExchanger.Mode.ADD);
        } else {
            userListExchanger.setMode(UserListExchanger.Mode.REMOVE);
        }
        userListExchanger.setChangedUser(clientHandler.getUser());

        List<User> userList=new ArrayList<>();
        for (ClientHandler handler : clientHandlers) {
                userList.add(handler.getUser());
        }

        userListExchanger.setUserList(userList);

        for (ClientHandler handler : clientHandlers) {
                handler.sendMessage(LOGGED_USERS, "обновление списка пользователей", userListExchanger);
        }
    }

    // посылаем когда пльзователь изменил имя
    synchronized public void sendUpdateUsers() throws IOException {
        List<User> userList=new ArrayList<>();

        for (ClientHandler clientHandler : clientHandlers) {
                userList.add(clientHandler.getUser());
        }

        UserListExchanger userListExchanger =new UserListExchanger();
        userListExchanger.setUserList(userList);
        userListExchanger.setMode(UserListExchanger.Mode.CHANGE_NAME);

        for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.sendMessage(CHANGE_USERNAME_NEW_LIST, "пользователь сменил имя", userListExchanger);
        }
    }

    synchronized public boolean isAlreadyLogin(User user) {
//        return clientHandlers.stream().anyMatch(o -> o.user.equals(user));
        Optional<User> optionalUser=authService.findUserByLoginAndPassword(user.getLogin(), user.getPassword());
        return optionalUser.filter(value -> clientHandlers.stream().anyMatch(o -> o.user.getId().equals(value.getId()))).isPresent();

    }

    synchronized public boolean sendPrivateMessage(String message, ClientHandler sender, User sendToUser) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUser().getId().equals(sendToUser.getId())) {
                clientHandler.sendMessage(PRIVATE_MSG, message, new UserExchanger(sender.getUser()));

                //дублируем сообшение себе
                sender.sendMessage(CLIENT_MSG, message + "->" + sendToUser.getUsername(), new UserExchanger(sender.getUser()));
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
        } else {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != sender) {
                    clientHandler.sendMessage(prefix, message, new UserExchanger(sender.getUser()));
                }
            }
        }
    }
//
//    /**
//     * @param message
//     * @param sender
//     * @param mode    - true - сообщение рассылается всем, false - всем кроме себя
//     * @throws IOException
//     */
//    synchronized public void broadcastServerMessage(String message, ClientHandler sender, boolean mode) throws IOException {
//
////        List<ClientHandler> list = getLoggedInUsers();
//
//        if (mode) {
//            for (ClientHandler clientHandler : clientHandlers) {
//                clientHandler.sendMessage(SERVER_MSG, message, new UserExchanger(sender.getUser()));
//            }
//        } else {
//            for (ClientHandler clientHandler : clientHandlers) {
//                if (clientHandler != sender) {
//                    clientHandler.sendMessage(SERVER_MSG, message, new UserExchanger(sender.getUser()));
//                }
//            }
//        }

//        for (ClientHandler handler : clientHandlers) {
//            if (handler.isLoggedIn()) {
//                handler.sendClientMessage(sender.getUserName(), message, CLIENT_MSG_CMD_PREFIX);
//            }
//        }
//    }


    synchronized public void stop() throws IOException {


        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.closeSocket();
        }

        serverSocket.close();
//        ((JDBCRepository) repository).closeConnection();
        System.out.println("-----------------");
        System.out.println("Сервер остановлен");
        System.exit(0);
    }

    public AuthService getAuthService() {
        return authService;
    }
//
//    List<ClientHandler> getLoggedInUsers() {
//        return clientHandlers.stream().filter(ClientHandler::isLoggedIn).toList();
//    }

    public String getLastDBError() {
        return authService.getLastDBError();
    }

    synchronized public List<String> getHistory(ClientHandler clientHandler) {
        return historyService.getHistory(clientHandler.getUser());
    }

    public void saveHistory(List<String> history, ClientHandler clientHandler) {
        historyService.setHistory(clientHandler.getUser(), history);
    }
}
