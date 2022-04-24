package it.polimi.ingsw.controller.notifications;

class NotificationObserver {

	private final Object observedObject;
	private final NotificationCallback callback;
	
	public NotificationObserver(Object observedObject, NotificationCallback callback) {
		this.observedObject = observedObject;
		this.callback = callback;
	}
	
	public Object getObservedObject() {
		return observedObject;
	}
	
	public NotificationCallback getCallback() {
		return callback;
	}
}
