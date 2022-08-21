package ru.igojig.fxmessenger.services.storage;

import ru.igojig.fxmessenger.model.User;

import java.util.List;

public interface HistoryService {
    List<String> getHistory(User user);
    void setHistory(User user, List<String> history);
}
