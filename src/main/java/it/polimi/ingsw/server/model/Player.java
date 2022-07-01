package it.polimi.ingsw.server.model;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.AvailableCardsDeck;
import it.polimi.ingsw.server.model.assistants.PlayedCardDeck;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import javafx.scene.control.Tab;

import java.util.*;
/**
 * This Class represents the {@code Player}
 * @author Leonardo Betti
 */
public class Player {
	
	private final String nickname;
	private CharacterCard playedCard;
	private AvailableCardsDeck availableCardsDeck;
	private PlayedCardDeck playedCardDeck;
	private SchoolBoard board;
	private int availableCoins;
	private final Wizard wizard;
	private int assistantCardOrderModifier;
	
	/**
	 * Constructs and sets up the player
	 * @param nickname (type String) nickname of player
	 * @param towerColor (type Tower) type of player tower
	 * @param initialTowerCount (type List of int) number of tower
	 * @param wiz (type List of Wizard) chosen {@code Wizards}
	 * @param initialCoins (type int) number of initial coins
	 * @throws IncorrectConstructorParametersException whenever the {@code Parameters} of the constructor aren't correct
	 */
	public Player(String nickname, Wizard wiz, Tower towerColor, int initialTowerCount, int initialCoins) throws IncorrectConstructorParametersException {
		if (nickname == null || wiz == null) throw new IncorrectConstructorParametersException();
		
		this.wizard = wiz;
		this.nickname = nickname;
		this.board = new SchoolBoard(towerColor, initialTowerCount);
		this.availableCardsDeck = new AvailableCardsDeck();
		this.playedCardDeck = new PlayedCardDeck();
		this.availableCoins = initialCoins;
		this.assistantCardOrderModifier = 0;
	}
	
	/**
	 * Gets the modifier for turns with the same assistant card value
	 * @return (type int) the modifier of order of assistant card
	 */
	public int getAssistantCardOrderModifier() {
		return assistantCardOrderModifier;
	}
	
	/**
	 * Sets the modifier for turns with the same assistant card value
	 * @param assistantCardOrderModifier (type int) the modifier of order of assistant card
	 */
	public void setAssistantCardOrderModifier(int assistantCardOrderModifier) {
		this.assistantCardOrderModifier = assistantCardOrderModifier;
	}

	/**
	 * Gets the nickname
	 * @return (type string) the player nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Gets the list of available assistant cards
	 * @return (type list of AssistantCard) The list of available {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard}
	 */
	public ArrayList<AssistantCard> getAvailableAssistantCards() {
		ArrayList<AssistantCard> cards = new ArrayList<>();
		for (int i = 0; i < availableCardsDeck.getCount(); i++) {
			cards.add(availableCardsDeck.getCard(i));
		}
		return cards;
	}

	/**
	 * Gets the last played assistant card
	 * @return (type AssistantCard) The last played {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard}
	 */
	public AssistantCard getLastPlayedAssistantCard() {
		return playedCardDeck.topCard();
	}
	
	/**
	 * Gets the list of controlled professors
	 * @return (type list of Professors) Th elist of controlled {@link it.polimi.ingsw.server.model.Professor Professors}
	 */
	public ArrayList<Professor> getControlledProfessors() {
		return board.getControlledProfessors();
	}

	/**
	 * Gets the number of students in the dining room with the same color
	 * @return number of {@link it.polimi.ingsw.server.model.student.Student student} in the dining room with the same color
	 * @param s The student to get the count of
	 */
	public int getCountAtTable(Student s) {
		return board.getCountAtTable(s);
	}

	/**
	 * Gets the active character card, the one purchased by the Player
	 * @return (type CharacterCard) The active character card
	 */
	public CharacterCard getActiveCharacterCard() {
		return playedCard;
	}
	
	/**
	 * Gets a copy of owned school board
	 * @return (type SchoolBoard) The owned {@link it.polimi.ingsw.server.model.SchoolBoard SchoolBoard}
	 */
	public SchoolBoard getBoard() {
		return board.copy();
	}
	
	/**
	 * Gets the number of coins owned by the Player
	 * @return (type int) the number of coins
	 */
	public int getAvailableCoins() {
		return availableCoins;
	}
	
	/**
	 * Gets the Wizard chosen by the player
	 * @return (type Wizard) The {@link it.polimi.ingsw.server.model.assistants.Wizard Wizard} chosen by the Player
	 */
	public Wizard getWizard() {
		return wizard;
	}

	/**
	 * Plays an Assistant card at the specified index in the available assistants list
	 * @param cardIndex (int) index of card to extract
	 * @throws CollectionUnderflowError whenever there aren't enough cards in the available cards list
	 */
	public void playAssistantCardAtIndex(int cardIndex) throws CollectionUnderflowError {
		playedCardDeck.addCardOnTop(availableCardsDeck.removeCard(cardIndex));
		assistantCardOrderModifier = 0;
	}

	/**
	 * Adds a {@link it.polimi.ingsw.server.model.student.Student student} to the entrance space
	 * @param s The student to add to the Entrance space
	 */
	public void addStudentToEntrance(Student s) {
		board.addStudentToEntrance(s);
	}

	/**
	 * Removes a {@link it.polimi.ingsw.server.model.student.Student student} from the entrance space
	 * @param s The student to remove from the Entrance space
	 */
	public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
		board.removeStudentFromEntrance(s);
	}

	/**
	 * Places a {@link it.polimi.ingsw.server.model.student.Student student} into the Dining room, and picks up a Coin if the mode allows it
	 * @param s The student to move to the dining room
	 * @param tableManager The Table manager object, used to check that we can give a coin out to the player
	 * @throws TableFullException whenever the {@link it.polimi.ingsw.server.model.SchoolBoard dining room} is full
	 */
	public void placeStudentAtTableAndGetCoin(Student s, TableManager tableManager) throws TableFullException {
		if (s == null) return;
		if (board.getCountAtTable(s) == 10) throw new TableFullException();
		
		board.addStudentToTable(s);
		if (board.getCountAtTable(s) % 3 == 0 && this.availableCoins > 0) {
			this.availableCoins += tableManager.getCoinFromReserve();
		}
	}
	
	/**
	 * Removes a {@link it.polimi.ingsw.server.model.student.Student student} from the Dining room
	 * @param s The student to remove from the dining room
	 * @throws CollectionUnderflowError whenever the {@link it.polimi.ingsw.server.model.SchoolBoard dining room} is empty
	 */
	public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
		board.removeStudentFromTable(s);
	}
	
	/**
	 * Adds control of a {@link it.polimi.ingsw.server.model.Professor professor}
	 * @param p The controlled professor
	 */
	public void addProfessor(Professor p) {
		board.setControlledProfessor(p);
	}
	
	/**
	 * Removes control of a {@link it.polimi.ingsw.server.model.Professor professor}
	 * @param p The controlled professor
	 */
	public void removeProfessor(Professor p) {
		board.removeProfessorControl(p);
	}
	
	/**
	 * Gets the player tower type (color)
	 * @return The player tower color
	 */
	public Tower getTowerType() {
		return board.getTowerType();
	}
	
	/**
	 * Makes the Player gain a Tower
	 * @throws TooManyTowersException whenever the number of tower in the {@link it.polimi.ingsw.server.model.SchoolBoard board} exceeds max value
	 */
	public void gainTower() throws TooManyTowersException {
		board.gainTower();
	}
	
	/**
	 * Gets the number of available towers
	 * @return The number of available towers that the owner can place
	 */
	public int getAvailableTowerCount() {
		return board.getAvailableTowerCount();
	}
	
	/**
	 * Picks a tower from the Board and returns it
	 * @throws InsufficientTowersException whenever the number of tower of {@link it.polimi.ingsw.server.model.SchoolBoard board} reaches the minimum victory condition
	 * @return The picked Tower
	 */
	public Tower pickAndRemoveTower() throws InsufficientTowersException {
		return board.pickAndRemoveTower();
	}
	
	/**
	 * Adds all {@link it.polimi.ingsw.server.model.student.Student students} in a collection to the entrance
	 * @param sc The collection of students to add to the entrance
	 */
	public void addAllStudentsToEntrance(StudentCollection sc) {
		if (sc == null) return;
		for (Student s : Student.values()) {
			int Count = sc.getCount(s);
			for (int i = 0; i < Count; i++) {
				//System.out.println("Count: " + Count);
				addStudentToEntrance(s);
			}
		}
	}
	
	/**
	 * Plays a character card if the user has enough coins to purchase it
	 * @param card The card to purchase
	 * @return Whether the card has been purchased
	 */
	public boolean playCharacterCard(CharacterCard card) {
		if (card == null) return false;
		
		int price = card.getPrice();
		if (price <= this.availableCoins) {
			card.purchase();
			playedCard = card;
			this.availableCoins -= price;
			return true;
		}
		return false;
	}
	
	/**
	 * Deactivates the character card, unlinking the player from it
	 */
	public void deactivateCard() {
		playedCard = null;
	}
	
	/**
	 * Notifies subscribers that the Player won the game
	 */
	public void notifyVictory() {
		//The Player has built its last Tower. Notify his victory
		HashMap<String, Object> userInfo = new HashMap<>();
		List<Tower> winningTower = new ArrayList<>();
		winningTower.add(getTowerType());
		userInfo.put(NotificationKeys.WinnerTowerType.getRawValue(), winningTower);
		NotificationCenter.shared().post(NotificationName.PlayerVictory, this, userInfo);
	}
	
	/**
	 * Creates a copy of this {@link it.polimi.ingsw.server.model.Player player}
	 * @return The copied Player
	 */
	public Player copy() {
		try {
			Player copy = new Player(nickname, wizard, getTowerType(), 0, 0);
			copy.playedCard = playedCard;
			copy.availableCardsDeck = availableCardsDeck;
			copy.playedCardDeck = playedCardDeck;
			copy.board = board.copy();
			copy.availableCoins = availableCoins;
			return copy;
		} catch (IncorrectConstructorParametersException e) {
			e.printStackTrace();
			return this;
		}
	}
	
	/**
	 * Gets the number of Students in the Entrance space
	 * @return  (type int) the number of {@link it.polimi.ingsw.server.model.student.Student Students} in the entrance space
	 */
	public int getStudentsInEntrance() {
		return board.getNumberOfStudentsInEntrance();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Player player = (Player) o;
		
		if (availableCoins != player.availableCoins) return false;
		if (assistantCardOrderModifier != player.assistantCardOrderModifier) return false;
		if (!nickname.equals(player.nickname)) return false;
		if (!Objects.equals(playedCard, player.playedCard)) return false;
		if (!availableCardsDeck.equals(player.availableCardsDeck)) return false;
		if (!playedCardDeck.equals(player.playedCardDeck)) return false;
		if (!board.equals(player.board)) return false;
		return wizard == player.wizard;
	}
	
	@Override
	public int hashCode() {
		int result = nickname.hashCode();
		result = 31 * result + (playedCard != null ? playedCard.hashCode() : 0);
		result = 31 * result + availableCardsDeck.hashCode();
		result = 31 * result + playedCardDeck.hashCode();
		result = 31 * result + board.hashCode();
		result = 31 * result + availableCoins;
		result = 31 * result + wizard.hashCode();
		result = 31 * result + assistantCardOrderModifier;
		return result;
	}


}
