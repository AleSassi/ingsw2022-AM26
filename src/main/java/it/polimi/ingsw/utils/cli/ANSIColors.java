package it.polimi.ingsw.utils.cli;

/**
 * The list of ANSI colors for the CLI
 */
public enum ANSIColors {
	
	Red("\u001B[31m"),
	Green("\u001B[32m"),
	Yellow("\u001B[33m"),
	Blue("\u001B[34m"),
	Pink("\u001B[35m"),
	Unknown("\u001B[36m");
	
	/**
	 * The reset ANSI code
	 */
	static final String RESET = "\u001B[0m";
	
	private final String rawValue;
	
	/**
	 * Constructs the ANSI color from the string
	 * @param rawValue The ANSI code
	 */
	ANSIColors(String rawValue) {
		this.rawValue = rawValue;
	}
	
	/**
	 * Gets the ANSI string for the color
	 * @return The ANSI color
	 */
	public String getRawValue() {
		return rawValue;
	}
}
