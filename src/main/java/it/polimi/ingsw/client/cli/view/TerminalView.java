package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;

import java.io.InputStreamReader;
import java.util.Scanner;

public abstract class TerminalView {
	
	private final Scanner terminalScanner;
	/**
	 * constructor, add observers(one for each type of {@link it.polimi.ingsw.notifications.Notification Notification} we need) on  {@link it.polimi.ingsw.notifications.NotificationCenter Center} type of match
	 * for every (@Code Nofication) that arrive call a differrent method of class according to the name of (@Code Nofication)
	 */
	public TerminalView() {
		this.terminalScanner = new Scanner(new InputStreamReader(System.in));
		NotificationCenter.shared().addObserver(this, this::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidTimeoutNetwork, null);
	}
	/**
	 getter
	 @return (type terminalScanner)
	 */
	public Scanner getTerminalScanner() {
		return terminalScanner;
	}
	/**
	 abstract method
	 */
	public abstract void run();
	/**
	 abstract method
	 */
	protected abstract void didReceiveNetworkTimeoutNotification(Notification notification);
}
