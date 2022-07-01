package it.polimi.ingsw.notifications;

import java.util.Objects;

/**
 * Class {@code NotificationObserver} represent the {@code Observer} who listens to the {@link Notification Notifications}
 */
class NotificationObserver {

	private final Object observer;
	private final Object observedObject;
	private final NotificationCallback callback;

	/**
	 * Constructor that initializes the observer with a specific callback
	 * @param observer	(type Object) The observer object
	 * @param observedObject (type Object) The object to observe for {@code Notifications}
	 * @param callback (type {@link NotificationCallback}) The callback of the notification for this observer
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
