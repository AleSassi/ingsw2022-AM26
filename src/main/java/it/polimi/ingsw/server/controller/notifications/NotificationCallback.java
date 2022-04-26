package it.polimi.ingsw.server.controller.notifications;

@FunctionalInterface
public interface NotificationCallback {

    void invokeCallback(Notification n);

}
