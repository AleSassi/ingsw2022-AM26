package it.polimi.ingsw.notifications;

import java.util.*;

/**
 * Class {@code NotificationCenter} manages all the {@link Notification Notifications} between {@code Objects}
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
	 * Gets this instance of {@code NotificationCenter}
	 * @return (type NotificationCenter) return this {@code NotificationCenter}
	 */
	public synchronized static NotificationCenter shared() {
		if (shared == null) {
			shared = new NotificationCenter();
		}
		return shared;
	}

	/**
	 * Creates and adds the {@code Observer}
	 * @param observer (type Object)
	 * @param callback (type NotificationCallback) {@link NotificationCallback} of the observer
	 * @param name (type NotificationName) {@link NotificationName Notification's name}
	 * @param observedObject (type Object)
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
	 * Removes and deletes the {@code Observer}
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
	 * Posts the {@link Notification}
	 * @param name (type NotificationName) {@link NotificationName Notification's name}
	 * @param observedObject (type Object)
	 * @param userInfo (type HasMap(String, Object) content of the {@code Notification}
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
