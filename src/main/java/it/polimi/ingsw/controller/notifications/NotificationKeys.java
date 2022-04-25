package it.polimi.ingsw.controller.notifications;

public enum NotificationKeys {
	
	WinnerNickname("WinnerNickname"),
	WinnerTowerType("WinnerTowerType"),
	IncomingNetworkMessage("IncomingNetworkMessage");
	
	private final String rawValue;
	
	NotificationKeys(String rawValue) {
		this.rawValue = rawValue;
	}
	
	public String getRawValue() {
		return rawValue;
	}
}
