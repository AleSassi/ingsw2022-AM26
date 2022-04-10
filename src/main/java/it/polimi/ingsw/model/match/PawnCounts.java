package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.InvalidPlayerCountException;

class PawnCounts {
	
	private final int playerCount;
	private final int cloudTileCount;
	private final int towersPerPlayer;
	private final int studentsDrawnFromCloud;
	private final int studentsPickedFromBag;
	private final int studentsMovedToRoom;
	
	public PawnCounts(int playerCount) throws InvalidPlayerCountException {
		if (playerCount < 2 || playerCount > 4) throw new InvalidPlayerCountException();
		
		this.playerCount = playerCount;
		this.cloudTileCount = initCloudTileCount();
		this.towersPerPlayer = initTowersPerPlayer();
		this.studentsDrawnFromCloud = initStudentsFromCloud();
		this.studentsPickedFromBag = initStudentsFromBag();
		this.studentsMovedToRoom = initStudentsToRoom();
	}
	
	private int initCloudTileCount() {
		return playerCount;
	}
	
	private int initTowersPerPlayer() {
		if (playerCount == 2 || playerCount == 4) {
			return 8;
		} else {
			return 6;
		}
	}
	
	private int initStudentsFromCloud() {
		if (playerCount == 2 || playerCount == 4) {
			return 3;
		} else {
			return 4;
		}
	}
	
	private int initStudentsFromBag() {
		if (playerCount == 2 || playerCount == 4) {
			return 7;
		} else {
			return 9;
		}
	}
	
	private int initStudentsToRoom() {
		if (playerCount == 2 || playerCount == 4) {
			return 7;
		} else {
			return 9;
		}
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
	
	public int getCloudTileCount() {
		return cloudTileCount;
	}
	
	public int getTowersPerPlayer() {
		return towersPerPlayer;
	}
	
	public int getStudentsDrawnFromCloud() {
		return studentsDrawnFromCloud;
	}
	
	public int getStudentsPickedFromBag() {
		return studentsPickedFromBag;
	}
	
	public int getStudentsMovedToRoom() {
		return studentsMovedToRoom;
	}
}
