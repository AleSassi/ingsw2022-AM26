package it.polimi.ingsw.notifications;

import java.util.*;

public class NotificationCenter {
	
	private static NotificationCenter shared;
	
	private final HashMap<NotificationName, List<NotificationObserver>> notificationToObserversMap;
	
	public NotificationCenter() {
		this.notificationToObserversMap = new HashMap<>();
	}
	
	public synchronized static NotificationCenter shared() {
		if (shared == null) {
			shared = new NotificationCenter();
		}
		return shared;
	}
	
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
	
	public synchronized void removeObserver(Object observer) {
		for (NotificationName notificationName: NotificationName.values()) {
			if (notificationToObserversMap.containsKey(notificationName)) {
				notificationToObserversMap.get(notificationName).removeIf((subscribedObserver) -> subscribedObserver.getObserver().equals(observer));
			}
		}
	}
	
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
