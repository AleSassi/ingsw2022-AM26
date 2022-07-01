package it.polimi.ingsw.utils.cli;

/**
 * Utility class to format strings for the CLI
 */
public class StringFormatter {
	
	/**
	 * Formats a string with ANSI colors
	 * @param stringToFormat The string to format
	 * @param color The ANSI color to use
	 * @return The formatted string with ANSI colors
	 */
	public static String formatWithColor(String stringToFormat, ANSIColors color) {
		return color.getRawValue() + stringToFormat + ANSIColors.RESET;
	}
}
