package it.polimi.ingsw.utils.cli.server;

import it.polimi.ingsw.server.exceptions.server.UnrecognizedCommandException;
import org.jetbrains.annotations.NotNull;

public enum ServerCommandTag {
	
	Help("--help", "-h", "Prints the Help menu"),
	ServerPort("--port", "-p", "Used to specify the port the TCP server should use to listen to connections with clients");
	
	private final String command;
	private final String shortCommand;
	private final String description;
	
	ServerCommandTag(@NotNull String command, String shortCommand, @NotNull String description) {
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
	
	public static ServerCommandTag commandTagForString(@NotNull String commandString) throws UnrecognizedCommandException {
		for (ServerCommandTag commandTag: ServerCommandTag.values()) {
			if (commandString.equals(commandTag.command) || commandString.equals(commandTag.shortCommand)) {
				return commandTag;
			}
		}
		throw new UnrecognizedCommandException();
	}
}
