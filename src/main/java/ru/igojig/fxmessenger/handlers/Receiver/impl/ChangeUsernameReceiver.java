package ru.igojig.fxmessenger.handlers.Receiver.impl;

import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.Optional;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class ChangeUsernameReceiver extends Receiver {

    private static final Prefix REQUIRED_COMMAND = CHANGE_USERNAME_REQUEST;
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
        UserExchanger userExchanger=(UserExchanger)exchanger.getChatObject();

//        String[] messageParts=Receiver.parseMessage(message, 2);
        String newUserName=userExchanger.getUser().getUsername();
        Optional<String> optUsername =mainHandler.changeUsername(mainHandler.user.getUsername(), newUserName);
        if(optUsername.isPresent()){
            System.out.println("Пользователь: " + mainHandler.user + " сменил имя на: " + optUsername.get());
            mainHandler.broadcastMessage(SERVER_MSG,
                    "Пользователь: " + mainHandler.user.getUsername()+  " сменил имя на: " + optUsername.get(), false);
            mainHandler.user.setUsername(optUsername.get());
            Exchanger exAnswer=new Exchanger(CHANGE_USERNAME_OK, null, new UserExchanger(mainHandler.user));
            mainHandler.writeObj(exAnswer);
            mainHandler.sendUpdatedUserList();
            return true;
        }
        else{
            String dbError= mainHandler.getLastDBError();
            Exchanger exAnswer=new Exchanger(CHANGE_USERNAME_ERR, dbError, null);
            mainHandler.writeObj(exAnswer);
            System.out.println("Не удалось сменить имя: " + exchanger + dbError);
            return false;
        }
    }

}
