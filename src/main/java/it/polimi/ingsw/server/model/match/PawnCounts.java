package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.InvalidPlayerCountException;

/**
 * A class representing a set of possible values for the ,multiplicity of tiles
 */
class PawnCounts {
	
	private final int playerCount;
	private final int cloudTileCount;
	private final int towersPerPlayer;
	private final int studentsDrawnFromCloud;
	private final int studentsPickedFromBag;
	private final int studentsMovedToRoom;

	/**
	 * Initialize al the variables depending on the number of {@link it.polimi.ingsw.server.model.Player Player}
	 * @param playerCount (type int) the number of {@code Players} in the match
	 */
	public PawnCounts(int playerCount) throws InvalidPlayerCountException {
		if (playerCount < 2 || playerCount > 4) throw new InvalidPlayerCountException();
		
		this.playerCount = playerCount;
		this.cloudTileCount = initCloudTileCount();
		this.towersPerPlayer = initTowersPerPlayer();
		this.studentsDrawnFromCloud = initStudentsFromCloud();
		this.studentsPickedFromBag = initStudentsFromBag();
		this.studentsMovedToRoom = initStudentsToRoom();
	}

	/**
	 * Sets the number of {@link it.polimi.ingsw.server.model.student.Cloud Clouds}
	 * @return (type int) the number of {@code Clouds}
	 */
	private int initCloudTileCount() {
		return playerCount;
	}

	/**
	 * Sets the number of {@link it.polimi.ingsw.server.model.Tower Towers} for each {@link it.polimi.ingsw.server.model.Player Player}
	 * @return (type int) the number of {@code Towers} for each {@code Player}
	 */
	private int initTowersPerPlayer() {
		if (playerCount == 2 || playerCount == 4) {
			return 8;
		} else {
			return 6;
		}
	}

	/**
	 * Sets the number of {@link it.polimi.ingsw.server.model.student.Student Students} on each {@link it.polimi.ingsw.server.model.student.Cloud Cloud}
	 * @return (type int) the number of {@code Students} on each {@code Cloud}
	 */
	private int initStudentsFromCloud() {
		if (playerCount == 2 || playerCount == 4) {
			return 3;
		} else {
			return 4;
		}
	}

	/**
	 * Sets the number of {@link it.polimi.ingsw.server.model.student.Student Students} picked from the {@code Bag}
	 * @return (type int) the number of {@code Students} picked from the {@code Bag}
	 */
	private int initStudentsFromBag() {
		if (playerCount == 2 || playerCount == 4) {
			return 7;
		} else {
			return 9;
		}
	}

	/**
	 * Sets the number of {@link it.polimi.ingsw.server.model.student.Student Students} in the {@code Entrance}
	 * @return (type int) the number of {@code Students} in the {@code Entrance}
	 */
	private int initStudentsToRoom() {
		if (playerCount == 2 || playerCount == 4) {
			return 7;
		} else {
			return 9;
		}
	}

	/**
	 * Gets the number of {@link it.polimi.ingsw.server.model.student.Cloud Clouds}
	 * @return (type int) the number of {@code Clouds}
	 */
	public int getCloudTileCount() {
		return cloudTileCount;
	}

	/**
	 * Gets the number of {@link it.polimi.ingsw.server.model.Tower Towers} for each {@link it.polimi.ingsw.server.model.Player Player}
	 * @return (type int) the number of {@code Towers} for each {@code Player}
	 */
	public int getTowersPerPlayer() {
		return towersPerPlayer;
	}

	/**
	 * Gets the number of {@link it.polimi.ingsw.server.model.student.Student Students} on each {@link it.polimi.ingsw.server.model.student.Cloud Cloud}
	 * @return (type int) the number of {@code Students} on each {@code Cloud}
	 */
	public int getStudentsDrawnFromCloud() {
		return studentsDrawnFromCloud;
	}

	/**
	 * Gets the number of {@link it.polimi.ingsw.server.model.student.Student Students} picked from the {@code Bag}
	 * @return (type int) the number of {@code Students} picked from the {@code Bag}
	 */
	public int getStudentsPickedFromBag() {
		return studentsPickedFromBag;
	}

	/**
	 * gets the number of {@link it.polimi.ingsw.server.model.student.Student Students} in the {@code Entrance}
	 * @return (type int) the number of {@code Students} in the {@code Entrance}
	 */
	public int getStudentsMovedToRoom() {
		return studentsMovedToRoom;
	}
}
