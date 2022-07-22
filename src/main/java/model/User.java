package model;

import java.util.Objects;

public record User(String userName, String login, String password) {
    @Override
    public boolean equals(Object obj) {
        if (obj==null)
            return false;

        if(obj instanceof User user) {
            return this.userName.equals(user.userName);
        }
        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, login, password);
    }
}
