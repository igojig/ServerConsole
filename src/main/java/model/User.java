package model;

import lombok.*;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -1584943797365343078L;

    Long id;

    @EqualsAndHashCode.Exclude
    String username;
    @EqualsAndHashCode.Exclude
    String login;
    @EqualsAndHashCode.Exclude
    String password;



}
