package it.polimi.ingsw.notifications;

/**
 * Functional interface that represents a callback invoked by the Notification Center when a Notification must be dispatched
 */
@FunctionalInterface
public interface NotificationCallback {
    
    /**
     * Executes the callback with the notification
     * @param n The notification which has been raised
     */
    void invokeCallback(Notification n);

}
