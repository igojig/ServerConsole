package server;

import handlers.ClientHandler;

import static prefix.Prefix.*;

import services.AuthService;
import services.impl.SimpleAuthServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {


    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clientHandlers;

    public MyServer(int port) throws IOException {
        clientHandlers = new ArrayList<>();
        authService = new SimpleAuthServiceImpl();
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
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void waitAndEstablishConnection() throws IOException {
        Socket socket = waitSocket();

        establishConection(socket);

    }

    private Socket waitSocket() throws IOException {
        System.out.println("Ожидние клиента.....");
        Socket socket = serverSocket.accept();

        System.out.println("Клиент подключился");
        return socket;
    }

    private void establishConection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.doHandle();
    }

    synchronized public void subscribe(ClientHandler clientHandler) throws IOException {
        clientHandlers.add(clientHandler);
        sendLoggedUsers();
    }

    synchronized public void unsubscribe(ClientHandler clientHandler) throws IOException {
        clientHandlers.remove(clientHandler);
        sendLoggedUsers();
    }

    private void sendLoggedUsers() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.isLoggedIn()) {
                sb.append(clientHandler.getUserName()).append(" ");
            }
        }

        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.isLoggedIn()) {
                clientHandler.sendServerMessage(SERVER_MSG_CMD_PREFIX_LOGGED_USERS, sb.toString());
            }
        }
    }

    synchronized public boolean isUserOccupied(String username) {
        return clientHandlers.stream().anyMatch(o -> o.getUserName().equals(username));
    }

    synchronized public boolean sendPrivateMessage(String sendToUserName, String message, ClientHandler sender) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.isLoggedIn() && clientHandler.getUserName().equalsIgnoreCase(sendToUserName)) {
                clientHandler.sendMessage(sender.getUserName(), message, PRIVATE_MSG_CMD_PREFIX);
                return true;
            }
        }
        return false;
    }

    synchronized public void broadcastMessage(String mesage, ClientHandler sender) throws IOException {
        for (ClientHandler handler : clientHandlers) {
            if (handler.isLoggedIn()) {
                handler.sendMessage(sender.getUserName(), mesage, CLIENT_MSG_CMD_PREFIX);
            }
        }
    }


    synchronized public void stop() throws IOException {


        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.closeSocket();
        }

        serverSocket.close();
        System.out.println("-----------------");
        System.out.println("Сервер остановлен");
        System.exit(0);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
