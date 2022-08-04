package handlers.Receiver.impl;

import exchanger.Exchanger;
import handlers.ClientHandler;
import handlers.Receiver.Receiver;
import model.User;

import java.io.IOException;
import java.util.Optional;

import static prefix.Prefix.*;

public class ChangeUsernameReceiver extends Receiver {

    private static final String REQUIRED_COMMAND = CHANGE_USERNAME_REQUEST;
    public ChangeUsernameReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {

        if(Receiver.matchCommand(exchanger, REQUIRED_COMMAND)){
            System.out.println("Вызываем обработчик смены имени пользователя: " + exchanger);
               processChangeName(exchanger);
               return true;
        }
        return false;
    }

    public boolean processChangeName(Exchanger exchanger) throws IOException {
        Exchanger exAnswer;
//        String[] messageParts=Receiver.parseMessage(message, 2);
        Optional<String> opt =mainHandler.changeUsername(mainHandler.user.getUsername(), exchanger.getUser().getUsername());
        if(opt.isPresent()){
            System.out.println("Пользователь: " + mainHandler.user + " сменил имя на: " + opt.get());
            mainHandler.broadcastServerMessage("Пользователь: " + mainHandler.user.getUsername()+  " сменил имя на: " + opt.get(), false);
            mainHandler.user.setUsername(opt.get());
            exAnswer=new Exchanger(CHANGE_USERNAME_OK, null, mainHandler.user);

//            mainHandler.write(String.format("%s %s", CHANGE_USERNAME_OK, messageParts[1]));
//            mainHandler.broadcastMessage("Пользователь: " + mainHandler.userName+  " сменил имя на: " + messageParts[1], false);
//            mainHandler.broadcastServerMessage("Пользователь: " + mainHandler.userName+  " сменил имя на: " + messageParts[1], false);

//            mainHandler.userName=messageParts[1];
            mainHandler.sendUpdatedUserList();

            return true;
        }
        else{
            String dbError= mainHandler.getLastDBError();
            exAnswer=new Exchanger(CHANGE_USERNAME_ERR, dbError, null);
//            mainHandler.write(String.format("%s %s", CHANGE_USERNAME_ERR, "ошибка смены имени на: " + messageParts[1]));
            System.out.println("Не удалось сменить имя: " + exchanger + dbError);
            return false;
        }
    }

}
