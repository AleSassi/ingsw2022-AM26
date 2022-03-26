package it.polimi.ingsw.controller.notifications;

@FunctionalInterface
public interface NotificationCallback {

    public void invokeCallback(Notification n);

}
