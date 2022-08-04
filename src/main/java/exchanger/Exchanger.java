package exchanger;

import lombok.Getter;
import lombok.ToString;
import model.User;

import java.io.Serializable;

@Getter
@ToString
public class Exchanger implements Serializable {
    String command;
    String message;
    User user;

    public Exchanger(String command, String message, User user) {
        this.command = command;
        this.message=message;
        this.user=user;
    }



}
