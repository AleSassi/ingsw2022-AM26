package it.polimi.ingsw.utils.cli;

public enum ANSIColors {
	
	Red("\u001B[31m"),
	Green("\u001B[32m"),
	Yellow("\u001B[33m"),
	Blue("\u001B[34m"),
	Pink("\u001B[35m"),
	Unknown("\u001B[36m");
	
	static final String RESET = "\u001B[0m";
	
	private final String rawValue;
	
	ANSIColors(String rawValue) {
		this.rawValue = rawValue;
	}
	
	public String getRawValue() {
		return rawValue;
	}
}
