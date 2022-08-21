package ru.igojig.fxmessenger.exchanger.impl;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.igojig.fxmessenger.exchanger.ChatExchanger;
import ru.igojig.fxmessenger.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class UserListExchanger implements ChatExchanger, Serializable {
    @Serial
    private static final long serialVersionUID= -5434044936644418409L;

    private List<User> userList;
    private User changedUser;
    private Mode mode;


    public enum Mode{
        ADD,
        REMOVE,
        CHANGE_NAME
    }





}
