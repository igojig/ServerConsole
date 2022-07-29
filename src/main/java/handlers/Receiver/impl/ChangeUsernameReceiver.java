package handlers.Receiver.impl;

import handlers.ClientHandler;
import handlers.Receiver.Receiver;

import java.io.IOException;

import static prefix.Prefix.*;

public class ChangeUsernameReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = CHANGE_USERNAME_REQUEST;
    public ChangeUsernameReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(String message) throws IOException {

        if(Receiver.matchCommand(message, REQUIRED_COMMAND)){
            System.out.println("Вызываем обработчик смены имени пользователя: " + message);
               processChangeName(message);
               return true;
        }
        return false;
    }

    public boolean processChangeName(String message) throws IOException {
        String[] messageParts=Receiver.parseMessage(message, 2);
        boolean result=mainHandler.changeUsername(mainHandler.userName, messageParts[1]);
        if(result){
            mainHandler.write(String.format("%s %s", CHANGE_USERNAME_OK, messageParts[1]));
            System.out.println("Пользователь: " + mainHandler.userName + " сменил имя на: " + messageParts[1]);
//            mainHandler.broadcastMessage("Пользователь: " + mainHandler.userName+  " сменил имя на: " + messageParts[1], false);
            mainHandler.broadcastServerMessage("Пользователь: " + mainHandler.userName+  " сменил имя на: " + messageParts[1], false);

            mainHandler.userName=messageParts[1];
            mainHandler.sendUpdatedUserList();

            return true;
        }
        else{
            mainHandler.write(String.format("%s %s", CHANGE_USERNAME_ERR, "ошибка смены имени на: " + messageParts[1]));
            System.out.println("Ну удалось сменить имя: " + messageParts[1]);
            return false;
        }
    }

}
