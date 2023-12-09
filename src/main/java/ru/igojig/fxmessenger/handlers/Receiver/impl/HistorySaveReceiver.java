package ru.igojig.fxmessenger.handlers.Receiver.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.HistoryExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.List;

import static ru.igojig.fxmessenger.prefix.Prefix.CMD_HISTORY_SAVE;

public class HistorySaveReceiver extends Receiver {

    private static final Logger logger= LogManager.getLogger(HistorySaveReceiver.class);

    private static final Prefix REQUIRED_COMMAND = CMD_HISTORY_SAVE;

    public HistorySaveReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger exchanger) throws IOException {
        if (Receiver.matchCommand(exchanger, REQUIRED_COMMAND)) {
            logger.debug("Вызываем обработчик сохранения истории. Пользователь: " + mainHandler.getUser());
            processSaveHistory(exchanger);
            return true;
        }
        return false;
    }

    private void processSaveHistory(Exchanger exchanger) {
        List<String> history=exchanger.getChatExchanger(HistoryExchanger.class).getHistoryList();
        mainHandler.saveHistory(history);
    }
}
