package ru.igojig.fxmessenger.handlers.Receiver.impl;


import ru.igojig.fxmessenger.exchanger.Exchanger;
import ru.igojig.fxmessenger.exchanger.impl.HistoryExchanger;
import ru.igojig.fxmessenger.handlers.ClientHandler;
import ru.igojig.fxmessenger.handlers.Receiver.Receiver;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.IOException;
import java.util.List;

import static ru.igojig.fxmessenger.prefix.Prefix.HISTORY_SAVE;

public class HistorySaveReceiver extends Receiver {

    private static final Prefix REQUIRED_COMMAND = HISTORY_SAVE;

    public HistorySaveReceiver(ClientHandler mainHandler) {
        super(mainHandler);
    }

    @Override
    public boolean receive(Exchanger ex) throws IOException {
        if (Receiver.matchCommand(ex, REQUIRED_COMMAND)) {
            System.out.println("Вызываем обработчик сохранения истории: " + ex);
            processSaveHistory(ex);
            return true;
        }
        return false;
    }

    private void processSaveHistory(Exchanger ex) {
        List<String> history=ex.getChatExchanger(HistoryExchanger.class).getHistoryList();
        mainHandler.saveHistory(history);
    }


}
