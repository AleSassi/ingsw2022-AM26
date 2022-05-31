package it.polimi.ingsw.jar;

import it.polimi.ingsw.client.cli.CLIManager;
import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import it.polimi.ingsw.utils.cli.client.ClientCommandTag;
import it.polimi.ingsw.server.exceptions.server.UnrecognizedCommandException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.regex.Pattern;

public class Client {
	
	private static final Pattern ipPattern = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	private static String nickname;
	private static String serverIP;
	private static int serverPort;
	
	public static void main(String[] args) {
		
		if (args.length == 0) {
			// Print the help menu
			printHelpMenu();
		} else {
			ClientCommandTag activeTag = null;
			String serverIP_fromCLI = null, clientMode = "cli";
			Integer serverPort_fromCLI = null;
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
							serverIP_fromCLI = arg;
							if (!validate(serverIP_fromCLI)) {
								System.out.println(StringFormatter.formatWithColor("Invalid IP address", ANSIColors.Red));
								return;
							}
						}
						case ServerPort -> {
							try {
								serverPort_fromCLI = Integer.parseInt(arg);
								if (serverPort_fromCLI < Server.getMinPort() || serverPort_fromCLI > Server.getMaxPort()) {
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
			if (serverIP_fromCLI == null || serverPort_fromCLI == null) {
				System.out.println(StringFormatter.formatWithColor("Missing some parameters required to start the client. See below for the list of parameters", ANSIColors.Red));
				printHelpMenu();
			} else {
				serverIP = serverIP_fromCLI;
				serverPort = serverPort_fromCLI;
				if (clientMode.equals("gui")) {
					GUI.main(args);
				} else {
					// Connect to the Server
					System.out.println(StringFormatter.formatWithColor("Connecting to " + serverIP_fromCLI + ":" + serverPort_fromCLI + "...", ANSIColors.Green));
					GameClient.createClient(serverIP_fromCLI, serverPort_fromCLI);
					try {
						GameClient.shared().connectToServer();
						System.out.println(StringFormatter.formatWithColor("Connected to " + serverIP_fromCLI + ":" + serverPort_fromCLI, ANSIColors.Green));
						// Start the CLI
						CLIManager.shared().startGameLoop();
					} catch (IOException e) {
						System.out.println(StringFormatter.formatWithColor("ERROR: Could not connect to the server. Please connect to the Internet, ensure that the IP and Port values are correct and try again", ANSIColors.Red));
					}
				}
			}
		}
		if (GameClient.shared() != null) {
			GameClient.shared().terminate();
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
	
	public static String getNickname() {
		return nickname;
	}
	
	public static String getServerIP() {
		return serverIP;
	}
	
	public static int getServerPort() {
		return serverPort;
	}
	
	public static void setNickname(String nickname) {
		Client.nickname = nickname;
	}
}
