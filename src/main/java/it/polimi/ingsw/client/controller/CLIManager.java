package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.io.InputStreamReader;
import java.util.Scanner;

public class CLIManager {
	
	private static CLIManager instance;
	
	private Scanner terminalScanner;
	
	public static CLIManager shared() {
		if (instance == null) {
			instance = new CLIManager();
		}
		return instance;
	}
	
	public void startGameLoop() {
		// Ask the Player to log in, send to the server
		terminalScanner = new Scanner(new InputStreamReader(System.in));
		System.out.println(StringFormatter.formatWithColor("Enter your nickname:\t", ANSIColors.Green));
		String nickname = terminalScanner.nextLine();
		Integer numberOfPlayers = null;
		MatchVariant matchVariant = null;
		Wizard wizard = null;
		while (numberOfPlayers == null) {
			System.out.println(StringFormatter.formatWithColor("Enter the number of players you would like to play with [2~4]:\t", ANSIColors.Green));
			String numberOfPlayersStr = terminalScanner.nextLine();
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
			String inputVariant = terminalScanner.nextLine().toLowerCase();
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
			String inputWizard = terminalScanner.nextLine();
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
		
		LoginMessage loginMessage = new LoginMessage(nickname, numberOfPlayers, matchVariant, wizard);
		GameClient.shared().sendMessage(loginMessage);
	}
	
}
