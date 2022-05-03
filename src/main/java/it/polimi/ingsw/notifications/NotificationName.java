package it.polimi.ingsw.notifications;

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
	ClientDidTimeoutNetwork
}
