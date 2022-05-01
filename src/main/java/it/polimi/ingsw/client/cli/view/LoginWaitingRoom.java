package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

public class LoginWaitingRoom extends TerminalView {
	
	private int numberOfPlayersToFill = 4;
	private boolean shouldQuit = false;
	
	@Override
	public void run() {
		NotificationCenter.shared().addObserver((notification) -> (new Thread(() -> otherPlayerLoggedInReceived(notification))).start(), NotificationName.ClientDidReceiveLoginResponse, null);
		waitForPlayers();
	}
	
	private synchronized void waitForPlayers() {
		while (!shouldQuit && numberOfPlayersToFill > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!shouldQuit) {
			System.out.println(StringFormatter.formatWithColor("The lobby is full. You are now ready to start the game", ANSIColors.Green));
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
