package ru.igojig.fxmessenger.exchanger;

import lombok.*;
import ru.igojig.fxmessenger.exchanger.impl.UserExchanger;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exchanger implements Serializable {

    @Serial
    private static final long serialVersionUID= -3079205955494740913L;

    private Prefix command;
    private String message;

    private ChatObject chatObject;

    public void foo(ChatObject o, Class<? extends ChatObject> c){
        var r=o.getClass();
        var t=c.cast(o);
    }

    public void bar(){
        foo(new UserExchanger(), UserExchanger.class);
    }



}
