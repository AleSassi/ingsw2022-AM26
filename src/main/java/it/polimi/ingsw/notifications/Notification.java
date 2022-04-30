package it.polimi.ingsw.notifications;
import java.util.*;

public class Notification {

    private final NotificationName name;
    private final HashMap<String, Object> userInfo;

    public Notification(NotificationName name, HashMap<String, Object> userInfo) {
        this.name = name;
        this.userInfo = userInfo;
    }

    public NotificationName getName() {
        return name;
    }

    public HashMap<String, Object> getUserInfo() {
        return userInfo;
    }
}
