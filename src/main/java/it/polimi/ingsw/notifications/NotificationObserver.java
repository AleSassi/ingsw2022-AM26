package it.polimi.ingsw.notifications;

import java.util.Objects;

class NotificationObserver {

	private final Object observer;
	private final Object observedObject;
	private final NotificationCallback callback;
	
	public NotificationObserver(Object observer, Object observedObject, NotificationCallback callback) {
		this.observedObject = observedObject;
		this.callback = callback;
		this.observer = observer;
	}
	
	public Object getObserver() {
		return observer;
	}
	
	public Object getObservedObject() {
		return observedObject;
	}
	
	public NotificationCallback getCallback() {
		return callback;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		NotificationObserver that = (NotificationObserver) o;
		
		if (!observer.equals(that.observer)) return false;
		if (!Objects.equals(observedObject, that.observedObject))
			return false;
		return callback.equals(that.callback);
	}
	
	@Override
	public int hashCode() {
		int result = observer.hashCode();
		result = 31 * result + (observedObject != null ? observedObject.hashCode() : 0);
		result = 31 * result + callback.hashCode();
		return result;
	}
}
