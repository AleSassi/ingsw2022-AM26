package it.polimi.ingsw.notifications;
import java.util.*;

/**
 * A container for information broadcast through a notification center to all registered observers.
 */
public class Notification {

    private final NotificationName name;
    private final HashMap<String, Object> userInfo;

    /**
     * Constructor initializes the {@code Notification}
     * @param name (type String) {@code Notification's} name
     * @param userInfo (type HashMap(String, Object)) content of the {@code Notification}, the data it will share
     */
    public Notification(NotificationName name, HashMap<String, Object> userInfo) {
        this.name = name;
        this.userInfo = userInfo;
    }

    /**
     * Gets the {@code NotificationName}
     * @return (type NotificationName) returns the {@code NotificationName}
     */
    public NotificationName getName() {
        return name;
    }

    /**
     * Gets the {@code UserInfo}
     * @return (type HashMap(String, Object)) returns the {@code UserInfo}
     */
    public HashMap<String, Object> getUserInfo() {
        return userInfo;
    }
}
