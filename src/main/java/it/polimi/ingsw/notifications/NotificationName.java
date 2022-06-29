package it.polimi.ingsw.notifications;

/**
 * Enum NotificationName are all the {@code Notification's} names
 */
public enum NotificationName {
	ServerDidReceiveLoginMessage,
	ServerDidReceivePlayerActionMessage,
	ServerDidTerminateMatch,
	PlayerVictory,
	ClientDidReceiveLoginResponse,
	ClientDidReceiveActivePlayerMessage,
	ClientDidReceiveMatchStateMessage,
	ClientDidReceiveMatchTerminationMessage,
	ClientDidReceivePlayerActionMessage,
	ClientDidReceivePlayerActionResponse,
	ClientDidReceivePlayerStateMessage,
	ClientDidReceiveTableStateMessage,
	ClientDidReceiveVictoryMessage,
	ClientDidTimeoutNetwork,
	JavaFXDidStartMovingStudent,
	JavaFXWindowDidResize,
	JavaFXDidEndMovingStudent,
	JavaFXDidPlayCharacterCard,
	JavaFXDidEndCharacterCardLoop,
	JavaFXPlayedAssistantCard,
	JavaFXPlayedCharacterCard,
	JavaFXDidClickOnCloud,
	TestNotification
}
