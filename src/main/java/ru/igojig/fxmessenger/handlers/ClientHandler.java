package ru.igojig.fxmessenger.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.ChatExchanger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.Receiver.*;
import ru.igojig.fxmessenger.handlers.Receiver.impl.*;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.server.MyServer;
import ru.igojig.fxmessenger.services.storage.HistoryService;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClientHandler {


    private static final Logger logger = LogManager.getLogger(ClientHandler.class);


    // сколько ждем до авторизации клиента, потом отключаем Socket
    public static final int WAIT_TIMEOUT = 5*1000;


    private final MyServer myServer;
    private final Socket clientSocket;

//    private final DataInputStream in;
//    private final DataOutputStream out;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    HistoryService historyService;


    // Наш будущий объект User
//    volatile public String userName;
//    volatile public int id;

    volatile public User user;


//    volatile public boolean isLoggedIn = false;

    // поток обработки сообшений
    private Thread handleThread;

    // поток-сторож для ожидания 120 сек и отключения хендлера
    private Thread waitTimeOutThread;

    // список наших "приемников" сообщения
    private final List<Receiver> receiverList = new ArrayList<>();

    public ClientHandler(MyServer myServer, Socket clientSocket) throws IOException {
        this.myServer = myServer;
        this.clientSocket = clientSocket;

//        this.in = new DataInputStream(clientSocket.getInputStream());
//        this.out = new DataOutputStream(clientSocket.getOutputStream());

        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());


        //запускам поток-сторож для ожидания подключения в течение 120 сек. (WAIT_TIMEOUT)
        startWaitTimeOutThread();

        Receiver[] receivers = {
                new AuthMessageReceiver(this),
                new RegisterUserReceiver(this),
                new ClientMessageReceiver(this),
                new ExitClientReceiver(this),
                new PrivateMessageReceiver(this),
                new StopServerReceiver(this),
                new ChangeUsernameReceiver(this),
                new HistoryRequestReceiver(this),
                new HistorySaveReceiver(this),

                // должна быть последней строкой
                new UnknownMessageReceiver(this),
        };

        registerReceiver(receivers);

    }

    public void registerReceiver(Receiver... receiver) {
        receiverList.addAll(Arrays.asList(receiver));
    }

    public void notifyReceiver(Exchanger exchanger) throws IOException {
        for (Receiver receiver : receiverList) {
            if (receiver.receive(exchanger)) {
                break;
            }
        }
    }

    private void startWaitTimeOutThread() {
        waitTimeOutThread = new Thread(() -> {
            try {
//                System.out.println("Поток-сторож запущен: " + this);
                logger.debug("Поток-сторож запущен: " + this);
                Thread.sleep(WAIT_TIMEOUT);
                if (user == null) {
                    logger.debug("Поток-сторож определил что никто не авторизовался за " + WAIT_TIMEOUT / 1000 + "сек. Отключаем клиента");
                    //TODO
                    // out.writeUTF(CMD_SHUT_DOWN_CLIENT);
                    Exchanger ex = new Exchanger(Prefix.CMD_SHUT_DOWN_CLIENT, "нткто не подключился. Отключаем клиента", null);
                    objectOutputStream.reset();
                    objectOutputStream.writeObject(ex);
                    closeConnection();
                } else {
                    logger.debug("Поток-сторож определил что подключен пользователь: " + user + ". Продолжаем работу");
                }
            } catch (InterruptedException | IOException e) {
//                e.printStackTrace();
//                System.out.println("Поток-сторож выбросил ошибку, пользователь: " + user);
                logger.warn("Поток-сторож выбросил ошибку, пользователь: " + user, e);
            }
        });
        waitTimeOutThread.setDaemon(true);
        waitTimeOutThread.start();
    }

    public void doHandle() {

        handleThread = new Thread(() -> {
            try {
                while (true) {
//                    String message = in.readUTF();

                    Object o = objectInputStream.readObject();
//                    System.out.println(o);
                    Exchanger ex = (Exchanger) o;
                    notifyReceiver(ex);
                }
            } catch (IOException e) {
                logger.error("Ошибка ввода-вывода в doHandle()", e);
//                try {
//                    if (!clientSocket.isClosed()) {
//                        closeConnection();
//                    }
//                } catch (IOException ex) {
//                    logger.error("Ошибка закрытия сокета", ex);
//                }
            } catch (ClassNotFoundException e) {
                logger.error("Ошибка ClassNotFoundException в doHandle()", e);
            }
        });
//        handleThread.setDaemon(true);
        handleThread.start();
    }


//    public void write(String message) throws IOException {
//        out.writeUTF(message);
//    }


    public void sendMessage(Prefix prefix, String message, ChatExchanger chatExchanger) throws IOException {
        Exchanger ex = new Exchanger(prefix, message, chatExchanger);

        objectOutputStream.reset();
        objectOutputStream.writeObject(ex);
    }

//    public void sendServerMessage(Prefix messageType, String message, ChatObject chatObject) throws IOException {
//        Exchanger ex=new Exchanger(messageType, message, chatObject);
//
//        objectOutputStream.reset();
//        objectOutputStream.writeObject(ex);
//    }

    public void writeObj(Exchanger exAnswer) throws IOException {
        objectOutputStream.reset();
        objectOutputStream.writeObject(exAnswer);
    }

    public void broadcastMessage(Prefix prefix, String message, boolean mode) throws IOException {
        myServer.broadcastMessage(prefix, message, this, mode);
    }

//    public void broadcastServerMessage(String message, boolean mode) throws IOException {
//        myServer.broadcastServerMessage(message, this, mode);
//    }

    public boolean sendPrivateMessage(String message, User sendToUser) throws IOException {
        return myServer.sendPrivateMessage(message, this, sendToUser);
    }


    public void closeSocket() throws IOException {

//        handleThread.interrupt();

        objectOutputStream.close();
        objectInputStream.close();
        clientSocket.close();
    }

//    public String getUserName() {
//        return userName;

//    }

    public void closeConnection() throws IOException {
        myServer.unsubscribe(this);
        waitTimeOutThread.interrupt();

        closeSocket();

//        System.out.println("Пользователь: " + user + " вышел из системы");
        logger.info("Пользователь: " + user + " вышел из системы");
        user = null;
    }


    public void subscribe() throws IOException {
        myServer.subscribe(this);
    }

    public boolean isAlreadyLogin(User user) {
        return myServer.isAlreadyLogin(user);
    }

//    public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
//        return myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
//    }

    public Optional<String> changeUsername(String oldUserName, String newUserName) {
        return myServer.getAuthService().renameUser(oldUserName, newUserName);
    }


    public Optional<User> addUser(String userName, String login, String password) {
        return myServer.getAuthService().addUser(userName, login, password);
    }

    public void stop() throws IOException {
        myServer.stop();
    }

    public void sendUpdatedUserList() throws IOException {
        myServer.sendUpdateUsers();
    }
//
//    public int getUserIdByLoginAndPassword(String login, String password) {
//       return myServer.getAuthService().getUserIdByLoginAndPassword(login, password);
//    }

    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        return myServer.getAuthService().findUserByLoginAndPassword(login, password);
    }

    public String getLastDBError() {
        return myServer.getLastDBError();
    }

    public User getUser() {
        return user;
    }


    public List<String> loadHistory() {
        return myServer.getHistory(this);
    }

    public void saveHistory(List<String> history) {
        myServer.saveHistory(history, this);
    }
}
