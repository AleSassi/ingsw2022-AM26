package it.polimi.ingsw.client.cli.view;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public abstract class TerminalView {
	
	private final Scanner terminalScanner;
	
	public TerminalView() {
		this.terminalScanner = new Scanner(new InputStreamReader(System.in));
	}
	
	public Scanner getTerminalScanner() {
		return terminalScanner;
	}
	
	public abstract void run();
}
