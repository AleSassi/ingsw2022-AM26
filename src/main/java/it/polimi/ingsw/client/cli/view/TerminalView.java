package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;

import java.io.InputStreamReader;
import java.util.Scanner;

public abstract class TerminalView {
	
	private final Scanner terminalScanner;
	
	public TerminalView() {
		this.terminalScanner = new Scanner(new InputStreamReader(System.in));
		NotificationCenter.shared().addObserver(this, this::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidTimeoutNetwork, null);
	}
	
	public Scanner getTerminalScanner() {
		return terminalScanner;
	}
	
	public abstract void run();
	protected abstract void didReceiveNetworkTimeoutNotification(Notification notification);
}
