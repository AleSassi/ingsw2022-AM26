package it.polimi.ingsw.utils.ui;

public enum StudentDropTarget {
	ToEntrance,
	ToDiningRoom,
	ToIsland,
	ToCharacterCard;
	
	public static StudentDropTarget[] all() {
		return StudentDropTarget.values();
	}
}
