package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
/**
 * This Class represent the {@code TerminalView} which displays a waiting message with the number of Players remaining to fill the lobby
 * @author Alessandro Sassi
 */
public class LoginWaitingRoom extends TerminalView {
	
	private int numberOfPlayersToFill = 4;
	private boolean shouldQuit = false;
	private final MatchVariant chosenVariant;
	
	/** Initializes the view with the chosen match variant (basic or expert)
	 * @param chosenVariant (type {@link it.polimi.ingsw.server.model.match.MatchVariant}) The Match variant
	 */
	public LoginWaitingRoom(MatchVariant chosenVariant) {
		this.chosenVariant = chosenVariant;
	}
	
	/**
	 * Subscribes to the required {@link it.polimi.ingsw.notifications.Notification notifications} for getting event callbacks and starts waiting for the lobby to fill
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, (notification) -> (new Thread(() -> otherPlayerLoggedInReceived(notification))).start(), NotificationName.ClientDidReceiveLoginResponse, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveMatchTerminationMessage, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
		waitForPlayers();
	}
	
	/**
	 * Callback for a Match termination notification
	 * Terminates the match and exits from the program
	 * @param notification The notification with the event data
	 */
	private void didReceiveMatchTerminationMessage(Notification notification) {
		shouldQuit = true;
		if (numberOfPlayersToFill > 0) {
			MatchTerminationMessage message = (MatchTerminationMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
			System.out.println(StringFormatter.formatWithColor("The server ended the match. Reason: \"" + message.getTerminationReason() + "\"", ANSIColors.Red));
			GameClient.shared().terminate();
		}
		synchronized (this) {
			notify();
		}
	}
	
	/**
	 * Waits until all Players have filled a lobby, and prints a waiting message telling the user how many Players need to join the Lobby before starting a game
	 */
	private void waitForPlayers() {
		synchronized (this) {
			while (!shouldQuit && numberOfPlayersToFill > 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (!shouldQuit) {
			NotificationCenter.shared().removeObserver(this);
			System.out.println(StringFormatter.formatWithColor("The lobby is full. You are now ready to start the game", ANSIColors.Green));
			ActionView actionView = new ActionView(chosenVariant);
			actionView.run();
		} else {
			GameClient.shared().terminate();
		}
	}
	
	/**
	 * Callback for the {@link it.polimi.ingsw.notifications.Notification Notification} for a new player login. It checks whether the game can start or if it needs to keep waiting and print the message
	 * @param notification (@code Notification) that contain the information of event
	 */
	private synchronized void otherPlayerLoggedInReceived(Notification notification) {
		LoginResponse message = (LoginResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		boolean messageIsForPlayer = message.getNickname().equals(Client.getNickname());
		
		if (!message.getNickname().equals(Client.getNickname()) || message.isLoginAccepted()) {
			System.out.println(StringFormatter.formatWithColor("Waiting for more players: " + message.getNumberOfPlayersRemainingToFillLobby(), ANSIColors.Yellow));
			numberOfPlayersToFill = message.getNumberOfPlayersRemainingToFillLobby();
			if (numberOfPlayersToFill == 0) {
				notify();
			}
		} else if (messageIsForPlayer) {
			// Login rejected: print reason and quit the client
			System.out.println(StringFormatter.formatWithColor("Login rejected. Reason \"" + message.getRejectionReason() + "\"", ANSIColors.Red));
			shouldQuit = true;
			notify();
		}
	}
	
	/**
	 * Callback for a {@link it.polimi.ingsw.notifications.Notification Notification} with name NetworkTimeoutNotification
	 * Exits from the program
	 * @param notification (@code Notification) with the event data
	 */
	@Override
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
		shouldQuit = true;
		System.out.println(StringFormatter.formatWithColor("The Client encountered an error. Reason: Timeout. The network connection with the Server might have been interrupted, or the Server might be too busy to respond", ANSIColors.Red));
		GameClient.shared().terminate();
		synchronized (this) {
			notify();
		}
	}
}
