package ru.igojig.fxmessenger.handlers.Receiver.impl;


import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.HistoryExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.List;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class HistoryRequestReceiver extends Receiver {

    private static final Prefix REQUIRED_COMMAND = HISTORY_REQUEST;

    public HistoryRequestReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger ex) throws IOException {
        if (Receiver.matchCommand(ex, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик получения истории: " + ex);
            getHistory(ex);
            return true;
        }
        return false;
    }

    public void getHistory(Exchanger exchanger){
//        History history=exchanger.getChatObject(History.class);
        List<String> userHistory=mainHandler.getHistory();
        HistoryExchanger h=new HistoryExchanger();
        h.setHistoryList(userHistory);
        Exchanger ex=new Exchanger(HISTORY_LOAD, "отправляем историю сообшений", h);
        try {
            mainHandler.writeObj(ex);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось отправить файл с историей: " + ex);
        }


    }
}
