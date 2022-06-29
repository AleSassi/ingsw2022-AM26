package it.polimi.ingsw.notifications;

import java.util.Objects;

/**
 * Class {@code NotificationObserver} represent the {@code Observer} who listen to the {@link Notification Notifications}
 */
class NotificationObserver {

	private final Object observer;
	private final Object observedObject;
	private final NotificationCallback callback;

	/**
	 * Constructor that initializes the {@code observedObject, callback, observer}
	 * @param observer	(type Object) observer
	 * @param observedObject (type Object) object to observe for the {@code Notifications}
	 * @param callback (type NotificationCallback) callback of the {@link  NotificationCallback}
	 */
	public NotificationObserver(Object observer, Object observedObject, NotificationCallback callback) {
		this.observedObject = observedObject;
		this.callback = callback;
		this.observer = observer;
	}

	/**
	 * Gets the {@code Observer}
	 * @return (type Object) returns the {@code Observer}
	 */
	public Object getObserver() {
		return observer;
	}

	/**
	 * Gets the {@code ObservedObject}
	 * @return (type Object) returns the {@code Observed Object}
	 */
	public Object getObservedObject() {
		return observedObject;
	}

	/**
	 * Gets the {@link  NotificationCallback}
	 * @return (type NotificationCallback) returns the {@code NotificationCallback}
	 */
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
