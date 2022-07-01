package it.polimi.ingsw.notifications;

/**
 * Enum NotificationKeys are the {@code keys} for the {@code notification} contents in the userInfo
 */
public enum NotificationKeys {
	
	WinnerNickname("WinnerNickname"),
	WinnerTowerType("WinnerTowerType"),
	IncomingNetworkMessage("IncomingNetworkMessage"),
	ClickedStudentColor("ClickedStudentColor"),
	StudentDropTargets("ValidStudentDropTargets"),
	JavaFXPlayedCharacter("playedCharacter"),
	CharacterCardTargetIslandIndex("targetIslandIdx"),
	CharacterCardSourceStudent("srcStudentColor"),
	CharacterCardDestinationStudent("dstStudentColor");
	
	private final String rawValue;
	
	/**
	 * Creates the enum with a raw String value
	 * @param rawValue The raw String value
	 */
	NotificationKeys(String rawValue) {
		this.rawValue = rawValue;
	}
	
	/**
	 * Accesses the raw value of the enum
	 * @return The raw value of the enum
	 */
	public String getRawValue() {
		return rawValue;
	}
}
