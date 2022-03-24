package it.polimi.ingsw.controller.notifications;
import java.util.*;

public class Notification {

    private NotificationName name;
    private HashMap<String, Object> userInfo;

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
