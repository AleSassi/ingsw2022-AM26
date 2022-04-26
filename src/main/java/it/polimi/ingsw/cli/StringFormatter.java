package it.polimi.ingsw.cli;

public class StringFormatter {
	
	public static String formatWithColor(String stringToFormat, ANSIColors color) {
		return color.getRawValue() + stringToFormat + ANSIColors.RESET;
	}
	
}
