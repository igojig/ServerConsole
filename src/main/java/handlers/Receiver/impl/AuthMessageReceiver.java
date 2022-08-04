package handlers.Receiver.impl;

import exchanger.Exchanger;
import handlers.ClientHandler;
import handlers.Receiver.Receiver;
import model.User;

import java.io.IOException;
import java.util.Optional;

import static prefix.Prefix.*;

public class AuthMessageReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = AUTH_CMD_PREFIX;

    public AuthMessageReceiver(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public boolean receive(Exchanger ex) throws IOException {
        if (Receiver.matchCommand(ex, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик авторизации: " + ex);
            authenticateUser(ex);
            return true;
        }
        return false;
    }

    private void authenticateUser(Exchanger ex) throws IOException {

        // тут должна быть логика для отработки результатов
        processAuthentication(ex);
    }

    private boolean processAuthentication(Exchanger ex) throws IOException {
        System.out.println("Аутентификация: " + ex);
//        String[] messageParts = Receiver.parseMessage(message, 3);


//        String login = messageParts[1];
//        String password = messageParts[2];


//        Optional<String> optionalUsername = mainHandler.getUsernameByLoginAndPassword(login, password);
        Optional<User> optUser=mainHandler.findUserByLoginAndPassword(ex.getUser());
        if (optUser.isEmpty()) {
            String dbError=mainHandler.getLastDBError();
            System.out.println("Ошибка запроса пользователя " + dbError + ex.getUser());
//            mainHandler.write(String.format("%s %s %s", AUTH_ERR_CMD_PREFIX, "пользователь не найден", message));
            Exchanger exAnswer=new Exchanger(AUTH_ERR_CMD_PREFIX, dbError, null );
            mainHandler.writeObj(exAnswer);
            return false;
        }

//        // пользователь уже залогинен в системе
//        if (mainHandler.isAlreadyLogin(optionalUsername.get())) {
//            System.out.println("Пользователь: " + optionalUsername.get() + " уже залогинен в системе");
//            mainHandler.write(String.format("%s пользователь: %s уже залогинен в системе", AUTH_ERR_CMD_PREFIX, optionalUsername.get()));
//            return false;
//        }


//        int id=mainHandler.getUserIdByLoginAndPassword(login, password);

        System.out.println("id=" + optUser.get().getId());


//        mainHandler.userName = optionalUsername.get();
//        mainHandler.id=id;

        mainHandler.user=optUser.get();


        mainHandler.isLoggedIn = true;
//        mainHandler.write(String.format("%s %s %s", AUTH_OK_CMD_PREFIX, optionalUsername.get(), id));
        Exchanger ans=new Exchanger(AUTH_OK_CMD_PREFIX, null, optUser.get());

        mainHandler.writeObj(ans);
        mainHandler.subscribe();
//        System.out.println("Пользователь: " + optionalUsername.get() + " подключился. ID=" + id);
        System.out.println("Пользователь подключился: " + optUser.get());

        return true;

    }


}
