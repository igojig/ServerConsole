package ru.igojig.fxmessenger.handlers;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.ChatExchanger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.UserChangeMode;
import ru.igojig.fxmessenger.handlers.Receiver.*;
import ru.igojig.fxmessenger.handlers.Receiver.impl.*;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.server.MyServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientHandler {

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    // сколько ждем до авторизации клиента, потом отключаем Socket
    public static final int WAIT_USER_AUTHORISATION_TIMEOUT = 10 * 1000;

    private final MyServer myServer;
    private final Socket clientSocket;

    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;

    @Getter
    @Setter
    volatile private User user;

    // поток обработки сообщений
    private Thread handleThread;

    // поток-сторож для ожидания 120 сек и отключения хендлера
    private Thread waitTimeOutThread;

    // список наших "приемников" сообщения
    private final List<Receiver> receiverList = new ArrayList<>();

    public ClientHandler(MyServer myServer, Socket clientSocket) throws IOException {
        this.myServer = myServer;
        this.clientSocket = clientSocket;

        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        //запускаем поток-сторож для ожидания подключения в течение WAIT_USER_AUTHORISATION_TIMEOUT
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
                new RequestUsersReceiver(this),

                // должна быть последней строкой
                new UnknownMessageReceiver(this),
        };

        registerReceivers(receivers);

    }

    public void registerReceivers(Receiver... receivers) {
        receiverList.addAll(List.of(receivers));
//        receiverList.addAll(Arrays.asList(receiver));
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
                logger.debug("Поток-сторож запущен: " + this);
                Thread.sleep(WAIT_USER_AUTHORISATION_TIMEOUT);
                if (user == null) {
                    logger.debug("Поток-сторож определил что никто не авторизовался за " + WAIT_USER_AUTHORISATION_TIMEOUT / 1000 + "сек. Отключаем клиента");
//                    Exchanger ex = new Exchanger(Prefix.CMD_SHUT_DOWN_CLIENT, "никто не подключился. Отключаем клиента", null);
//                    objectOutputStream.reset();
//                    objectOutputStream.writeObject(ex);
//                    writeObj(ex);

                    sendMessage(Prefix.CMD_SHUT_DOWN_CLIENT, "никто не подключился. Отключаем клиента", null);
                    closeConnection();
                } else {
                    logger.debug("Поток-сторож определил что подключен пользователь: " + user + ". Продолжаем работу");
                }
            } catch (InterruptedException | IOException e) {
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
                    Exchanger exchanger = (Exchanger) objectInputStream.readObject();
                    notifyReceiver(exchanger);
                }
            } catch (IOException e) {
                logger.error("Ошибка ввода-вывода в doHandle()", e);
            } catch (ClassNotFoundException e) {
                logger.error("Ошибка ClassNotFoundException в doHandle()", e);
            }
        });
        handleThread.start();
    }

    public void sendMessage(Prefix prefix, String message, ChatExchanger chatExchanger) throws IOException {
        Exchanger exchanger = new Exchanger(prefix, message, chatExchanger);
        writeObj(exchanger);
    }

    private void writeObj(Exchanger exchanger) throws IOException {
        objectOutputStream.reset();
        objectOutputStream.writeObject(exchanger);
    }

    public void broadcastMessage(Prefix prefix, String message, boolean mode) throws IOException {
        myServer.broadcastMessage(prefix, message, this, mode);
    }

    public boolean sendPrivateMessage(String message, User sendToUser) throws IOException {
        return myServer.sendPrivateMessage(message, this, sendToUser);
    }

    public void closeSocket() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        clientSocket.close();
    }

    public void closeConnection() throws IOException {
        myServer.unsubscribe(this);
//        myServer.sendLoggedUsers(this, UserChangeMode.REMOVE);
        closeSocket();
        logger.info("Пользователь: " + user + " вышел из системы");
        user = null;
    }

    public boolean isAlreadyLogin(User user) {
        return myServer.isAlreadyLogin(user);
    }

    public Optional<String> changeUsername(String oldUserName, String newUserName) {
        return myServer.getAuthService().renameUser(oldUserName, newUserName);
    }

    public Optional<User> addUser(String userName, String login, String password) {
        return myServer.getAuthService().addUser(userName, login, password);
    }

    public void stop() throws IOException {
        myServer.stop();
    }

    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        return myServer.getAuthService().findUserByLoginAndPassword(login, password);
    }

    public String getLastDBError() {
        return myServer.getLastDBError();
    }

    public List<String> loadHistory() {
        return myServer.getHistory(this);
    }

    public void saveHistory(List<String> history) {
        myServer.saveHistory(history, this);
    }

//    public void ssssendUpdatedUserList() throws IOException {
//        myServer.sendUpdateUsers();
//    }

//    public void ssssendLoggedUsers() throws IOException {
//        myServer.sendLoggedUsers(true, this);
//    }

    public void sendLoggedUsers(UserChangeMode userChangeMode) throws IOException {
        myServer.sendLoggedUsers(this, userChangeMode);
    }


    public void subscribe() throws IOException {
        myServer.subscribe(this);
    }

    public void unsubscribe() throws IOException {
        myServer.unsubscribe(this);
    }
}
