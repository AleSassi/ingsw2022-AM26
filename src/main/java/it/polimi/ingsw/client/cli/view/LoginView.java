package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
/**
 * This Class represent the {@code LoginView}, which handles the initial input for logging the player into a Lobby and connecting to the server
 * @author Alessandro Sassi
 */
public class LoginView extends TerminalView {
	
	/**
	 * Asks the player their login data, validates them and then sends a {@link it.polimi.ingsw.server.controller.network.messages.LoginMessage message} to the server to log the Player in. Finally, if the login was successful it redirects to the {@link it.polimi.ingsw.client.cli.view.LoginWaitingRoom waiting room}
	 */
	@Override
	public void run() {
		System.out.println(StringFormatter.formatWithColor("Enter your nickname:\t", ANSIColors.Green));
		String nickname = getTerminalScanner().nextLine();
		Integer numberOfPlayers = null;
		MatchVariant matchVariant = null;
		Wizard wizard = null;
		while (numberOfPlayers == null) {
			System.out.println(StringFormatter.formatWithColor("Enter the number of players you would like to play with [2~4]:\t", ANSIColors.Green));
			String numberOfPlayersStr = getTerminalScanner().nextLine();
			try {
				numberOfPlayers = Integer.parseInt(numberOfPlayersStr);
				if (numberOfPlayers < 2 || numberOfPlayers > 4) {
					System.out.println(StringFormatter.formatWithColor("Invalid input: the desired number of players must be between 2 and 4 (included)", ANSIColors.Red));
					numberOfPlayers = null;
				}
			} catch (NumberFormatException e) {
				System.out.println(StringFormatter.formatWithColor("Invalid input: not a number", ANSIColors.Red));
			}
		}
		while (matchVariant == null) {
			System.out.println(StringFormatter.formatWithColor("Enter the match variant you would like to play in [Expert, Basic]:\t", ANSIColors.Green));
			String inputVariant = getTerminalScanner().nextLine().toLowerCase();
			if (inputVariant.equals("expert")) {
				matchVariant = MatchVariant.ExpertRuleSet;
			} else if (inputVariant.equals("basic")) {
				matchVariant = MatchVariant.BasicRuleSet;
			} else {
				System.out.println(StringFormatter.formatWithColor("Invalid input: \"" + inputVariant + "\" is not a valid match variant", ANSIColors.Red));
			}
		}
		while (wizard == null) {
			System.out.println(StringFormatter.formatWithColor("Enter Wizard type [1~4]:\t", ANSIColors.Green));
			String inputWizard = getTerminalScanner().nextLine();
			try {
				int inputWizardID = Integer.parseInt(inputWizard);
				if (inputWizardID < 1 || inputWizardID > 4) {
					System.out.println(StringFormatter.formatWithColor("Invalid input: the wizard ID must be between 1 and 4 (included)", ANSIColors.Red));
				} else {
					wizard = Wizard.values()[inputWizardID - 1];
				}
			} catch (NumberFormatException e) {
				System.out.println(StringFormatter.formatWithColor("Invalid input: not a number", ANSIColors.Red));
			}
		}
		Client.setNickname(nickname);
		LoginMessage loginMessage = new LoginMessage(nickname, numberOfPlayers, matchVariant, wizard);
		GameClient.shared().sendMessage(loginMessage);
		GameClient.shared().startPongTimeoutTimer();
		// Once the LoginView finished, we start the LoginWaitingRoom
		NotificationCenter.shared().removeObserver(this);
		LoginWaitingRoom loginWaitingRoom = new LoginWaitingRoom(matchVariant);
		loginWaitingRoom.run();
	}

	/**
	 * Callback for a {@link it.polimi.ingsw.notifications.Notification Notification} with name NetworkTimeoutNotification
	 * Terminates the program after a network timeout
	 * @param notification (@code Notification) the event data
	 */
	@Override
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
		System.out.println(StringFormatter.formatWithColor("The Client encountered an error. Reason: Timeout. The network connection with the Server might have been interrupted, or the Server might be too busy to respond", ANSIColors.Red));
		GameClient.shared().terminate();
	}
}
