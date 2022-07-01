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
 * This Class represent the {@code TerminalView}
 * @author Alessandro Sassi
 */
public class LoginWaitingRoom extends TerminalView {
	
	private int numberOfPlayersToFill = 4;
	private boolean shouldQuit = false;
	private final MatchVariant chosenVariant;
	/**constructor
	 * set variant of match
	 * @param chosenVariant (type {@link it.polimi.ingsw.server.model.match.MatchVariant}) type of match
	 */
	public LoginWaitingRoom(MatchVariant chosenVariant) {
		this.chosenVariant = chosenVariant;
	}
	/**
	 * create a thread and add observers(one for each type of {@link it.polimi.ingsw.notifications.Notification Notification} we need) on  {@link it.polimi.ingsw.notifications.NotificationCenter Center} type of match
	 * for every (@Code Nofication) that arrive call a differrent method of class according to the name of (@Code Nofication)
	 if recive a (@Code Notification) with name ClientDidReceiveMatchTerminationMessage, end client otherwise call method (@code waitForPlayers() )
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, (notification) -> (new Thread(() -> otherPlayerLoggedInReceived(notification))).start(), NotificationName.ClientDidReceiveLoginResponse, null);
		NotificationCenter.shared().addObserver(this, (notification) -> {
			shouldQuit = true;
			if (numberOfPlayersToFill > 0) {
				MatchTerminationMessage message = (MatchTerminationMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
				System.out.println(StringFormatter.formatWithColor("The server ended the match. Reason: \"" + message.getTerminationReason() + "\"", ANSIColors.Red));
				GameClient.shared().terminate();
			}
			synchronized (this) {
				notify();
			}
		}, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
		waitForPlayers();
	}
	/**
	 *the method stand-by the client view until the as long as enough players have joined, if variable shouldQuit=1 end the client
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
	 * method called whan arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name otherPlayerLoggedInReceived
	 *the method print if the login is accepted and if this true,  the remaining number of player to, that need to start the game
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
	 * method called when arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name NetworkTimeoutNotification
	 *the method end the client
	 * @param notification (@code Notification) that contain the information of event
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
