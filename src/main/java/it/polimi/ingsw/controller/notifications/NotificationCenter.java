package it.polimi.ingsw.controller.notifications;

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
	
	public synchronized void addObserver(NotificationCallback callback, NotificationName name, Object observedObject) {
		// If the map already contains at least 1 observer for the same notification, we append the observer to the list. Otherwise, we create a new one
		NotificationObserver newObserver = new NotificationObserver(observedObject, callback);
		if (notificationToObserversMap.containsKey(name)) {
			notificationToObserversMap.get(name).add(newObserver);
		} else {
			List<NotificationObserver> notificationObservers = new ArrayList<>();
			notificationObservers.add(newObserver);
			notificationToObserversMap.put(name, notificationObservers);
		}
	}
	
	public synchronized void post(NotificationName name, Object observedObject, HashMap<String, Object> userInfo) {
		if (notificationToObserversMap.containsKey(name)) {
			Notification notification = new Notification(name, userInfo);
			List<NotificationObserver> observers = notificationToObserversMap.get(name);
			for (NotificationObserver observer: observers) {
				if (observedObject == null || observedObject.equals(observer.getObservedObject())) {
					//Call the callback
					observer.getCallback().invokeCallback(notification);
				}
			}
		}
	}
}
