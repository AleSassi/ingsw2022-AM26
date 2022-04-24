package it.polimi.ingsw.controller.notifications;

@FunctionalInterface
public interface NotificationCallback {

    void invokeCallback(Notification n);

}
