package it.polimi.ingsw.notifications;

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
	
	NotificationKeys(String rawValue) {
		this.rawValue = rawValue;
	}
	
	public String getRawValue() {
		return rawValue;
	}
}
