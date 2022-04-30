package it.polimi.ingsw.utils.cli.client;

import it.polimi.ingsw.server.exceptions.server.UnrecognizedCommandException;
import org.jetbrains.annotations.NotNull;

public enum ClientCommandTag {
	
	Help("--help", "-H", "Prints the Help menu"),
	ServerIP("--ip", "-I", "Used to specify the IP address of the TCP server the client has to connect to"),
	ServerPort("--port", "-P", "Used to specify the port the TCP server should use to listen to connections with clients"),
	ClientMode("--mode", "-M", "Used to determine whether the client uses the CLI or GUI variant. Possible values: \"cli\" (to run as Command Line), \"gui\" (to run with a more user-friendly interface) [DEFAULT: \"cli\"]");
	
	private final String command;
	private final String shortCommand;
	private final String description;
	
	ClientCommandTag(@NotNull String command, String shortCommand, @NotNull String description) {
		this.command = command;
		this.shortCommand = shortCommand;
		this.description = description;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getShortCommand() {
		return shortCommand;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static ClientCommandTag commandTagForString(@NotNull String commandString) throws UnrecognizedCommandException {
		for (ClientCommandTag commandTag: ClientCommandTag.values()) {
			if (commandString.equals(commandTag.command) || commandString.equals(commandTag.shortCommand)) {
				return commandTag;
			}
		}
		throw new UnrecognizedCommandException();
	}
}
