package it.polimi.ingsw.notifications;

/**
 * Interface NotificationCallback invokes the {@code Callback}
 */
@FunctionalInterface
public interface NotificationCallback {

    void invokeCallback(Notification n);

}
