package ru.igojig.fxmessenger.handlers;

import ru.igojig.fxmessenger.exchanger.ChatObject;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.handlers.Receiver.*;
import ru.igojig.fxmessenger.handlers.Receiver.impl.*;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;
import ru.igojig.fxmessenger.server.MyServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClientHandler {


    // сколько ждем до авторизации клиента, потом отключаем Socket
    public static final int WAIT_TIMEOUT = 120 * 1000;


    private final MyServer myServer;
    private final Socket clientSocket;

//    private final DataInputStream in;
//    private final DataOutputStream out;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;


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

        objectInputStream=new ObjectInputStream(clientSocket.getInputStream());
        objectOutputStream=new ObjectOutputStream(clientSocket.getOutputStream());


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
                System.out.println("Поток-сторож запущен: " + this);
                Thread.sleep(WAIT_TIMEOUT);
                if (user == null) {
                    System.out.println("Поток-сторож определил что никто не авторизовался за " + WAIT_TIMEOUT / 1000 + "сек. Отключаем клиента");
                    //TODO
                    // out.writeUTF(CMD_SHUT_DOWN_CLIENT);
                    closeConnection();
                } else {
                    System.out.println("Поток-сторож определил что подключен пользователь: " + user + ". Продолжаем работу");
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                System.out.println(user + " Поток-сторож выбросил ошибку");
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

                    Object o= objectInputStream.readObject();
//                    System.out.println(o);
                    Exchanger ex=(Exchanger)o;
                    notifyReceiver(ex);
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
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
//        handleThread.setDaemon(true);
        handleThread.start();
    }






//    public void write(String message) throws IOException {
//        out.writeUTF(message);
//    }


    public void sendMessage(Prefix prefix, String message, ChatObject chatObject) throws IOException {
        Exchanger ex=new Exchanger(prefix, message, chatObject);

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
        return myServer.sendPrivateMessage(message,this, sendToUser);
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

        System.out.println("Пользователь: " + user + " вышел из системы");
        user = null;
//        isLoggedIn = false;
    }

//
//    public boolean isLoggedIn() {
//        return isLoggedIn;
//    }

    public void subscribe() throws IOException {
        myServer.subscribe(this);
    }

    public boolean isAlreadyLogin(User user) {
        return myServer.isAlreadyLogin(user);
    }

    public Optional<String> getUsernameByLoginAndPassword(String login, String password) {
        return myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
    }

    public Optional<String> changeUsername(String oldUserName, String newUserName){
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

    public int getUserIdByLoginAndPassword(String login, String password) {
       return myServer.getAuthService().getUserIdByLoginAndPassword(login, password);
    }

    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        return  myServer.getAuthService().findUserByLoginAndPassword(login, password);
    }

    public String getLastDBError() {
        return myServer.getLasetDBError();
    }

    public User getUser() {
        return user;
    }
}
