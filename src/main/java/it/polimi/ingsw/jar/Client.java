package it.polimi.ingsw.jar;

import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import it.polimi.ingsw.utils.cli.client.ClientCommandTag;
import it.polimi.ingsw.utils.cli.server.ServerCommandTag;
import it.polimi.ingsw.server.controller.network.GameServer;
import it.polimi.ingsw.server.exceptions.server.UnavailablePortException;
import it.polimi.ingsw.server.exceptions.server.UnrecognizedCommandException;

import java.util.regex.Pattern;

public class Client {
	
	private static final Pattern ipPattern = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	public static void main(String[] args) {
		if (args.length == 0) {
			// Print the help menu
			printHelpMenu();
		} else {
			ClientCommandTag activeTag = null;
			String serverIP = null, clientMode = "cli";
			Integer serverPort = null;
			for (String arg: args) {
				if (activeTag == null) {
					try {
						activeTag = ClientCommandTag.commandTagForString(arg);
						if (activeTag == ClientCommandTag.Help) {
							printHelpMenu();
							return;
						}
					} catch (UnrecognizedCommandException e) {
						System.out.println(StringFormatter.formatWithColor("Unrecognized parameter sent to the executable. Refer to the following help menu for guidance on which parameters are allowed by the Server", ANSIColors.Red));
						printHelpMenu();
						return;
					}
				} else {
					// Decode the contents
					switch (activeTag) {
						case ServerIP -> {
							serverIP = arg;
							if (!validate(serverIP)) {
								System.out.println(StringFormatter.formatWithColor("Invalid IP address", ANSIColors.Red));
								return;
							}
						}
						case ServerPort -> {
							try {
								serverPort = Integer.parseInt(arg);
								if (serverPort < Server.getMinPort() || serverPort > Server.getMaxPort()) {
									System.out.println(StringFormatter.formatWithColor("Allowed port values are between " + Server.getMinPort() + " and " + Server.getMaxPort(), ANSIColors.Red));
									return;
								}
							} catch (NumberFormatException e) {
								System.out.println(StringFormatter.formatWithColor("Expected an integer value for the Server Port argument", ANSIColors.Red));
								return;
							}
						}
						case ClientMode -> {
							clientMode = arg;
							if (clientMode == null || (!clientMode.equals("cli") && !clientMode.equals("gui"))) {
								System.out.println(StringFormatter.formatWithColor("Invalid client mode", ANSIColors.Red));
								return;
							}
						}
					}
					activeTag = null;
				}
			}
			// Start the Client
			if (serverIP == null || serverPort == null) {
				System.out.println(StringFormatter.formatWithColor("Missing some parameters required to start the client. See below for the list of parameters", ANSIColors.Red));
				printHelpMenu();
			} else {
				// Connect to the Server
				System.out.println(StringFormatter.formatWithColor("Connecting to " + serverIP + ":" + serverPort + "...", ANSIColors.Green));
				
			}
		}
	}
	
	private static void printHelpMenu() {
		System.out.println(StringFormatter.formatWithColor("Eriantys AM26 Client - Help Menu", ANSIColors.Yellow));
		System.out.println("This executable allows you to start a TCP client to play Eriantys with other players.");
		for (ClientCommandTag commandTag: ClientCommandTag.values()) {
			System.out.println(StringFormatter.formatWithColor(commandTag.getCommand() + ", " + commandTag.getShortCommand(), ANSIColors.Green) + "\t" + commandTag.getDescription());
		}
	}
	
	public static boolean validate(final String ip) {
		return ipPattern.matcher(ip).matches();
	}
	
}
