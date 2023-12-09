package ru.igojig.fxmessenger.handlers.Receiver.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.HistoryExchanger;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.List;

import static ru.igojig.fxmessenger.prefix.Prefix.*;

public class HistoryRequestReceiver extends Receiver {

    private static final Logger logger= LogManager.getLogger(HistoryRequestReceiver.class);

    private static final Prefix REQUIRED_COMMAND = CMD_HISTORY_REQUEST;

    public HistoryRequestReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            UserExchanger userExchanger=exchanger.getChatExchanger(UserExchanger.class);
            logger.debug("Вызываем обработчик получения истории. Пользователь: " + userExchanger.getUser());
            getHistory(exchanger);
            return true;
        }
        return false;
    }

    public void getHistory(Exchanger exchanger){
        List<String> userHistoryList=mainHandler.loadHistory();
        HistoryExchanger historyExchanger=new HistoryExchanger();
        historyExchanger.setHistoryList(userHistoryList);
//        Exchanger ex=new Exchanger(CMD_HISTORY_LOAD, "отправляем историю сообщений", historyExchanger);
        try {
            mainHandler.sendMessage(CMD_HISTORY_LOAD, "отправляем историю сообщений", historyExchanger);
//            mainHandler.writeObj(ex);
        } catch (IOException e) {
            logger.warn("Не удалось отправить файл с историей: ", e);
        }
    }
}
