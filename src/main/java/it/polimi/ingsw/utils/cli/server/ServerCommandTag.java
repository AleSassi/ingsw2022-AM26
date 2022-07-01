package it.polimi.ingsw.utils.cli.server;

import it.polimi.ingsw.server.exceptions.server.UnrecognizedCommandException;
import org.jetbrains.annotations.NotNull;

/**
 * The enum holds a list of constants representing possible Server parameters
 */
public enum ServerCommandTag {
	
	Help("--help", "-h", "Prints the Help menu"),
	ServerPort("--port", "-p", "Used to specify the port the TCP server should use to listen to connections with clients");
	
	private final String command;
	private final String shortCommand;
	private final String description;
	
	/**
	 * Constructs a server parameter constant
	 * @param command The parameter key
	 * @param shortCommand The shortened variant
	 * @param description The parameter description
	 */
	ServerCommandTag(@NotNull String command, String shortCommand, @NotNull String description) {
		this.command = command;
		this.shortCommand = shortCommand;
		this.description = description;
	}
	
	/**
	 * Gets the parameter ID
	 * @return The parameter ID
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Gets the parameter shortened command
	 * @return The shortened parameter
	 */
	public String getShortCommand() {
		return shortCommand;
	}
	
	/**
	 * Gets the parameter description
	 * @return The parameter description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Finds the command tag that matches a parameter string
	 * @param commandString The parameter string
	 * @return The parameter enum that matches the string
	 * @throws UnrecognizedCommandException If the parameter is unknown
	 */
	public static ServerCommandTag commandTagForString(@NotNull String commandString) throws UnrecognizedCommandException {
		for (ServerCommandTag commandTag: ServerCommandTag.values()) {
			if (commandString.equals(commandTag.command) || commandString.equals(commandTag.shortCommand)) {
				return commandTag;
			}
		}
		throw new UnrecognizedCommandException();
	}
}
