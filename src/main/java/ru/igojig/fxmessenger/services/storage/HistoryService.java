package ru.igojig.fxmessenger.services.storage;

import ru.igojig.fxmessenger.model.User;

import java.util.List;

public interface HistoryService {
    List<String> loadHistory(User user);
    void saveHistory(User user, List<String> history);

    void clearHistory();
}
