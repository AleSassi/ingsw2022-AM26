package it.polimi.ingsw.utils.ui;

/**
 * An enum representing the set of possible student movement targets
 */
public enum StudentDropTarget {
	ToEntrance,
	ToDiningRoom,
	ToIsland,
	ToCharacterCard;
	
	/**
	 * Gets the array of all possible targets
	 * @return The array of all possible targets
	 */
	public static StudentDropTarget[] all() {
		return StudentDropTarget.values();
	}
}
