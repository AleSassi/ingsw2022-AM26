package it.polimi.ingsw.jar;

import it.polimi.ingsw.cli.ANSIColors;
import it.polimi.ingsw.cli.StringFormatter;
import it.polimi.ingsw.cli.server.ServerCommandTag;
import it.polimi.ingsw.server.controller.network.server.GameServer;
import it.polimi.ingsw.server.exceptions.server.UnavailablePortException;
import it.polimi.ingsw.server.exceptions.server.UnrecognizedCommandException;

public class Server {
	
	private static final int minPort = 1024;
	private static final int maxPort = 65535;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			// Print the help menu
			printHelpMenu();
		} else {
			ServerCommandTag activeTag = null;
			int serverPort = minPort + 1;
			for (String arg: args) {
				if (activeTag == null) {
					try {
						activeTag = ServerCommandTag.commandTagForString(arg);
						if (activeTag == ServerCommandTag.Help) {
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
					if (activeTag == ServerCommandTag.ServerPort) {
						try {
							serverPort = Integer.parseInt(arg);
							if (serverPort < minPort || serverPort > maxPort) {
								System.out.println(StringFormatter.formatWithColor("Allowed port values are between " + minPort + " and " + maxPort, ANSIColors.Red));
								return;
							}
						} catch (NumberFormatException e) {
							System.out.println(StringFormatter.formatWithColor("Expected an integer value for the Server Port argument", ANSIColors.Red));
							return;
						}
					}
				}
			}
			// Start the Server
			GameServer server = new GameServer(serverPort);
			try {
				server.startListeningIncomingConnections();
			} catch (UnavailablePortException e) {
				System.out.println(StringFormatter.formatWithColor("The port " + serverPort + " is already in use. Try another port", ANSIColors.Red));
			}
		}
	}
	
	private static void printHelpMenu() {
		System.out.println(StringFormatter.formatWithColor("Eriantys AM26 Server - Help Menu", ANSIColors.Yellow));
		System.out.println("This executable allows you to start a TCP server that listens to incoming connections and messages from Eriantys clients.");
		for (ServerCommandTag commandTag: ServerCommandTag.values()) {
			System.out.println(StringFormatter.formatWithColor(commandTag.getCommand() + ", " + commandTag.getShortCommand(), ANSIColors.Green) + "\t" + commandTag.getDescription());
		}
	}
	
}
