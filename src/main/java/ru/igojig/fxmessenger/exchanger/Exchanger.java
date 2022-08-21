package ru.igojig.fxmessenger.exchanger;

import lombok.*;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exchanger implements Serializable {

    @Serial
    private static final long serialVersionUID = -3079205955494740913L;

    private Prefix command;
    private String message;

//    @Getter(AccessLevel.NONE)
    private ChatExchanger chatExchanger;

    public <T extends ChatExchanger> T getChatExchanger(Class<T> tClass) {
        return tClass.cast(chatExchanger);
    }

//    public <T extends ChatObject> T foo(Class<T> c) {
//        var r = chatObject.getClass();
//        var t = c.cast(chatObject);
//        return t;
//    }
//
//    public void bar() {
//        var r = foo(History.class);
//    }

//
//    public ChatObject getChatObject() {
//        return chatObject;
//    }


}
