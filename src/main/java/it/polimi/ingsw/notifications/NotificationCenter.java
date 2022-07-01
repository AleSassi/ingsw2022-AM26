package it.polimi.ingsw.notifications;

import java.util.*;

/**
 * A notification dispatch mechanism that enables the broadcast of information to registered observers.
 */
public class NotificationCenter {
	
	private static NotificationCenter shared;
	
	private final HashMap<NotificationName, List<NotificationObserver>> notificationToObserversMap;

	/**
	 * Constructor initializes the {@code NotificationCenter}
	 */
	public NotificationCenter() {
		this.notificationToObserversMap = new HashMap<>();
	}

	/**
	 * Gets the common instance of {@code NotificationCenter} with the Singleton pattern
	 * @return (type NotificationCenter) the shared instance of {@code NotificationCenter}
	 */
	public synchronized static NotificationCenter shared() {
		if (shared == null) {
			shared = new NotificationCenter();
		}
		return shared;
	}

	/**
	 * Creates and adds the {@code Observer}
	 * @param observer (type Object) a new notification observer
	 * @param callback (type NotificationCallback) The {@link NotificationCallback} of the observer, which will be invoked when the notification is posted
	 * @param name (type NotificationName) The {@link NotificationName name} of the notification linked to this callback
	 * @param observedObject (type Object) If null, the observer receives all notifications with the same name posted with any value of observed object. If specified, the callback will be invoked only if the <code>ObservedObject</code> parameter of the <code>post</code> method matches this value
	 */
	public synchronized void addObserver(Object observer, NotificationCallback callback, NotificationName name, Object observedObject) {
		// If the map already contains at least 1 observer for the same notification, we append the observer to the list. Otherwise, we create a new one
		NotificationObserver newObserver = new NotificationObserver(observer, observedObject, callback);
		if (notificationToObserversMap.containsKey(name)) {
			notificationToObserversMap.get(name).add(newObserver);
		} else {
			List<NotificationObserver> notificationObservers = new ArrayList<>();
			notificationObservers.add(newObserver);
			notificationToObserversMap.put(name, notificationObservers);
		}
	}

	/**
	 * Removes and deletes the {@code Observer} from the notification queue
	 * @param observer (type Object) {@code Observer} to delete
	 */
	public synchronized void removeObserver(Object observer) {
		for (NotificationName notificationName: NotificationName.values()) {
			if (notificationToObserversMap.containsKey(notificationName)) {
				notificationToObserversMap.get(notificationName).removeIf((subscribedObserver) -> subscribedObserver.getObserver().equals(observer));
			}
		}
	}

	/**
	 * Posts a {@link Notification}
	 * @param name (type NotificationName) The {@link NotificationName name} of the notification to post
	 * @param observedObject (type Object) The object with which observers must have registered their notifications in order to receive them
	 * @param userInfo (type HashMap(String, Object) The optional data posted with the {@code Notification}
	 */
	public synchronized void post(NotificationName name, Object observedObject, HashMap<String, Object> userInfo) {
		if (notificationToObserversMap.containsKey(name)) {
			Notification notification = new Notification(name, userInfo);
			List<NotificationObserver> observers = new ArrayList<>(notificationToObserversMap.get(name));
			for (NotificationObserver observer: observers) {
				if (observedObject == null || observedObject.equals(observer.getObservedObject())) {
					//Call the callback
					observer.getCallback().invokeCallback(notification);
				}
			}
		}
	}
}
