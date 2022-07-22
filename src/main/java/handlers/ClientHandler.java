package handlers;

import handlers.Receiver.*;
import handlers.Receiver.impl.*;
import server.MyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

public class ClientHandler {


    // сколько ждем до авторизации клиента, потом отключаем Socket
    public static final int WAIT_TIMEOUT = 120 * 1000;


    private final MyServer myServer;
    private final Socket clientSocket;

    private final DataInputStream in;
    private final DataOutputStream out;

    volatile public String userName;
    volatile public boolean isLoggedIn = false;

    // поток обработки сообшений
    private Thread handleThread;

    // поток-сторож для ожидания 120 сек и отключения хендлера
    private Thread waitTimeOutThread;

    // список наших "приемников" сообщения
    private final List<Receiver> receiverList = new ArrayList<>();

    public ClientHandler(MyServer myServer, Socket clientSocket) throws IOException {
        this.myServer = myServer;
        this.clientSocket = clientSocket;

        this.in = new DataInputStream(clientSocket.getInputStream());
        this.out = new DataOutputStream(clientSocket.getOutputStream());

        //запускам поток-сторож для ожидания подключения в течение 120 сек. (WAIT_TIMEOUT)
        startWaitTimeOutThread();

        Receiver[] receivers = {
                new AuthMessageReceiver(this),
                new RegisterUserReceiver(this),
                new ClientMessageReceiver(this),
                new ExitClientReceiver(this),
                new PrivateMessageReceiver(this),
                new StopServerReceiver(this),
                new UnknownMessageReceiver(this)
        };

        registerReceiver(receivers);

    }

    public void registerReceiver(Receiver... receiver) {
        receiverList.addAll(Arrays.asList(receiver));
    }

    public void notifyReceiver(String message) throws IOException {
        for (Receiver receiver : receiverList) {
            if (receiver.receive(message)) {
                break;
            }
        }
    }

    private void startWaitTimeOutThread() {
        waitTimeOutThread = new Thread(() -> {
            try {
                System.out.println("Поток-сторож запущен: " + this);
                Thread.sleep(WAIT_TIMEOUT);
                if (userName == null) {
                    System.out.println("Поток-сторож определил что никто не авторизовался за " + WAIT_TIMEOUT / 1000 + "сек. Отключаем клиента");
                    //TODO
                    // out.writeUTF(CMD_SHUT_DOWN_CLIENT);
                    closeConnection();
                } else {
                    System.out.println("Поток-сторож определил что подключен пользователь: " + getUserName() + ". Продолжаем работу");
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                System.out.println("Поток-сторож выбросил ошибку");
            }
        });
        waitTimeOutThread.setDaemon(true);
        waitTimeOutThread.start();
    }

    public void doHandle() {

        handleThread = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    notifyReceiver(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (!clientSocket.isClosed()) {
                        closeConnection();
                    }
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
            finally {

            }

        });
        handleThread.setDaemon(true);
        handleThread.start();
    }

    public void write(String message) throws IOException {
        out.writeUTF(message);
    }


    public void sendMessage(String sender, String message, String messageType) throws IOException {
        out.writeUTF(String.format("%s %s %s", messageType, sender, message));
        out.flush();
    }

    public void sendServerMessage(String messageType, String message) throws IOException {
        out.writeUTF(String.format("%s %s", messageType, message));
    }

    public void closeConnection() throws IOException {
        myServer.unsubscribe(this);
        waitTimeOutThread.interrupt();
        closeSocket();

        System.out.println("Пользователь: " + getUserName() + " вышел из системы");
        userName = null;
        isLoggedIn = false;
    }

    public void closeSocket() throws IOException {

//        handleThread.interrupt();

        in.close();
        out.close();
        clientSocket.close();
    }


    public String getUserName() {
        return userName;
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void subscribe() throws IOException {
        myServer.subscribe(this);
    }

    public boolean isUserOccupied(String username) {
        return myServer.isUserOccupied(username);
    }

    public void broadcastMessage(String message) throws IOException {
        myServer.broadcastMessage(message, this);
    }

    public boolean sendPrivateMessage(String sendToUserName, String message) throws IOException {
        return myServer.sendPrivateMessage(sendToUserName, message, this);
    }

    public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
        return myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
    }

    public boolean isUserPresent(String username) {
        return myServer.getAuthService().isUserPresent(username);
    }

    public void addUser(String userName, String login, String password) {
        myServer.getAuthService().addUser(userName, login, password);
    }

    public void stop() throws IOException {
        myServer.stop();
    }

}
