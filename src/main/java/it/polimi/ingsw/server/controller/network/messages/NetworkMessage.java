package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;

/**
 * Abstract class representing the network messages
 */
public abstract class NetworkMessage {
	
	private String classType;

	/**
	 * Constructor deserializes the string and creates a new instance of the message
	 * @param serializedString (type String) String to deserialize
	 * @throws MessageDecodeException whenever the deserialization process fails
	 */
	NetworkMessage(String serializedString) throws MessageDecodeException {
		deserialize(serializedString);
	}

	/**
	 * Constructor creates an empty message with the classType field filled, to enable decode
	 */
	NetworkMessage() {
		classType = getClass().getSimpleName();
	}

	/**
	 * Serializes the string so that it can be sent
	 * @return (type String) serialized string
	 */
	public abstract String serialize();

	/**
	 * Deserializes the string received from the network
	 * @param serializedString (type String) string to deserialize
	 * @throws MessageDecodeException whenever the deserialization process fails
	 */
	protected abstract void deserialize(String serializedString) throws MessageDecodeException;

	/**
	 * Gets the {@link it.polimi.ingsw.notifications.NotificationName NotificationName} relative to type of {@code NetworkMessage}
	 * @return (type NotificationName) returns the {@code NotificationName} relative to type of {@code NetworkMessage}
	 */
	public abstract NotificationName clientReceivedMessageNotification();

	/**
	 * Gets the classType
	 * @return (type String) returns the classType
	 */
	protected String getClassType() {
		return classType;
	}

	/**
	 * Sets the classType
	 * @param classType (type String) classType to set to
	 */
	protected void setClassType(String classType) {
		this.classType = classType;
	}
}
