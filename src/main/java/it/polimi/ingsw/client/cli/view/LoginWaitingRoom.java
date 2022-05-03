package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

public class LoginWaitingRoom extends TerminalView {
	
	private int numberOfPlayersToFill = 4;
	private boolean shouldQuit = false;
	private final MatchVariant chosenVariant;
	
	public LoginWaitingRoom(MatchVariant chosenVariant) {
		this.chosenVariant = chosenVariant;
	}
	
	@Override
	public void run() {
		NotificationCenter.shared().addObserver((notification) -> (new Thread(() -> otherPlayerLoggedInReceived(notification))).start(), NotificationName.ClientDidReceiveLoginResponse, null);
		NotificationCenter.shared().addObserver((notification) -> {
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
			System.out.println(StringFormatter.formatWithColor("The lobby is full. You are now ready to start the game", ANSIColors.Green));
			ActionView actionView = new ActionView(chosenVariant);
			actionView.run();
		}
	}
	
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
}
