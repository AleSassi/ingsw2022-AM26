package it.polimi.ingsw.notifications;

@FunctionalInterface
public interface NotificationCallback {

    void invokeCallback(Notification n);

}
