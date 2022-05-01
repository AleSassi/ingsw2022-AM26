package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.view.LoginView;
import it.polimi.ingsw.client.cli.view.LoginWaitingRoom;
import it.polimi.ingsw.client.cli.view.TerminalView;
import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.io.InputStreamReader;
import java.util.Scanner;

public class CLIManager {
	
	private static CLIManager instance;
	
	public static CLIManager shared() {
		if (instance == null) {
			instance = new CLIManager();
		}
		return instance;
	}
	
	public void startGameLoop() {
		// Ask the Player to log in, send to the server
		TerminalView activeView = new LoginView();
		activeView.run();
		// Once the LoginView finished, we start the LoginWaitingRoom
		activeView = new LoginWaitingRoom();
		activeView.run();
	}
	
}
