package it.polimi.ingsw.controller.notifications;
import java.util.*;

public enum NotificationName {
	ServerDidReceiveLoginMessage,
	ServerDidReceivePlayerActionMessage,
	ServerDidTerminateMatch,
	PlayerVictory,
	ClientDidReceiveLoginMessage,
	ClientDidReceiveActivePlayerMessage,
	ClientDidReceiveMatchStateMessage,
	ClientDidReceiveMatchTerminationMessage,
	ClientDidReceivePlayerActionMessage,
	ClientDidReceivePlayerActionResponse,
	ClientDidReceivePlayerStateMessage,
	ClientDidReceiveTableStateMessage,
	ClientDidReceiveVictoryMessage,
}
