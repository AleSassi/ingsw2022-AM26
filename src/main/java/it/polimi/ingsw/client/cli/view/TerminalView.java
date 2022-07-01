package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;

import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * An abstract class that represents a view object interacting with the Terminal
 * It is used to build a CLI that can accept input from the user and display strings using the Terminal
 * @author Alessandro Sassi
 */
public abstract class TerminalView {
	
	private final Scanner terminalScanner;
	
	/**
	 * Default Terminal View constructor. It initializes the objects for reading input from the Terminal and subscribes the object to the Network Timeout notification, so that the CLI can properly terminate if the Network reports a timeout
	 */
	public TerminalView() {
		this.terminalScanner = new Scanner(new InputStreamReader(System.in));
		NotificationCenter.shared().addObserver(this, this::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidTimeoutNetwork, null);
	}
	
	/**
	 * Retrieves the scanner used to read input from the Terminal
	 * @return (type Scanner) The scanner used to read input from the Terminal
	 */
	public Scanner getTerminalScanner() {
		return terminalScanner;
	}
	
	/**
	 * Abstract method where subclasses will start their operations (e.g. start reading input from command line)
	 */
	public abstract void run();
	/**
	 * Abstract callback where subclasses can customize the behavior in response to network timout notifications
	 * @param notification (type Notification) the network timeout notification
	 */
	protected abstract void didReceiveNetworkTimeoutNotification(Notification notification);
}
