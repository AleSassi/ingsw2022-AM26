package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.notifications.NotificationCenter;
import it.polimi.ingsw.controller.notifications.NotificationKeys;
import it.polimi.ingsw.controller.notifications.NotificationName;
import it.polimi.ingsw.exceptions.model.*;
import it.polimi.ingsw.model.assistants.*;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.student.*;

import java.util.*;

public class Player {
	
	private final String nickname;
	private CharacterCard playedCard;
	private AvailableCardsDeck availableCardsDeck;
	private PlayedCardDeck playedCardDeck;
	private SchoolBoard board;
	private int availableCoins;
	private final Wizard wizard;
	private int assistantCardOrderModifier;
	
	public Player(String nickname, Wizard wiz, Tower towerColor, int initialTowerCount) throws IncorrectConstructorParametersException {
		if (nickname == null || wiz == null) throw new IncorrectConstructorParametersException();
		
		this.wizard = wiz;
		this.nickname = nickname;
		this.board = new SchoolBoard(towerColor, initialTowerCount);
		this.availableCardsDeck = new AvailableCardsDeck();
		this.playedCardDeck = new PlayedCardDeck();
		this.availableCoins = 0;
		this.assistantCardOrderModifier = 0;
	}
	
	public int getAssistantCardOrderModifier() {
		return assistantCardOrderModifier;
	}
	
	public void setAssistantCardOrderModifier(int assistantCardOrderModifier) {
		this.assistantCardOrderModifier = assistantCardOrderModifier;
	}
	
	/**
	 * return nickname of player
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * return list of card that player can use
	 */
	public ArrayList<AssistantCard> getAvailableAssistantCards() {
		ArrayList<AssistantCard> cards = new ArrayList<>();
		for (int i = 0; i < availableCardsDeck.getCount(); i++) {
			cards.add(availableCardsDeck.getCard(i));
		}
		return cards;
	}
	
	/**
	 * return played assistant card
	 */
	public AssistantCard getLastPlayedAssistantCard() {
		return playedCardDeck.topCard();
	}
	
	public ArrayList<Professor> getControlledProfessors() {
		return board.getControlledProfessors();
	}
	
	/**
	 * return number of student(with type S) in the dining room
	 */
	public int getCountAtTable(Student s) {
		return board.getCountAtTable(s);
	}
	
	/**
	 * return the active characther card
	 */
	
	public CharacterCard getActiveCharacterCard() {
		return playedCard;
	}
	
	public SchoolBoard getBoard() {
		return board.copy();
	}
	
	public int getAvailableCoins() {
		return availableCoins;
	}
	
	public Wizard getWizard() {
		return wizard;
	}
	
	/**
	 * allow player to use card from avaible deck of assistant
	 */
	
	public void playAssistantCardAtIndex(int cardIndex) throws CollectionUnderflowError {
		playedCardDeck.addCardOnTop(availableCardsDeck.removeCard(cardIndex));
		assistantCardOrderModifier = 0;
	}
	
	/**
	 * add a student of type s to entance
	 */
	public void addStudentToEntrance(Student s) {
		board.addStudentToEntrance(s);
	}
	
	/**
	 * remove a student of type s from entance
	 */
	public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
		board.removeStudentFromEntrance(s);
	}
	
	/**
	 * add a student to dining room e give a coin if it is the third student added
	 */
	public void placeStudentAtTableAndGetCoin(Student s) throws TableFullException {
		if (s == null) return;
		if (board.getCountAtTable(s) == 10) throw new TableFullException();
		
		board.addStudentToTable(s);
		if (board.getCountAtTable(s) % 3 == 0) {
			this.availableCoins += 1;
		}
	}
	
	/**
	 * remove a student of type s from diningroom
	 */
	public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
		board.removeStudentFromTable(s);
	}

	public void addProfessor(Professor p) {
		board.setControlledProfessor(p);
	}
	/**
	 * remove a professor
	 */
	public void removeProfessor(Professor p) {
		board.removeProfessorControl(p);
	}
	
	public Tower getTowerType() {
		return board.getTowerType();
	}
	/**
	 * remove a professor from table
	 */
	public void gainTower() throws TooManyTowersException {
		board.gainTower();
	}
	
	public int getAvailableTowerCount() {
		return board.getAvailableTowerCount();
	}
	/**
	 * return the color of tower and decrement the towercounter on schoolboard
	 */
	public Tower pickAndRemoveTower() throws InsufficientTowersException {
		return board.pickAndRemoveTower();
	}
	/**
	 * the method have a collection like parameter, it take all student
	 * belonging to the collection, and add it all to the entrance
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
	 * allow play to active character card (set the active card and decrease the player's coin
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
	 * deactivate the character card
	 */
	public void deactivateCard() {
		playedCard = null;
	}
	
	public void notifyVictory() {
		//The Player has built its last Tower. Notify his victory
		HashMap<String, Object> userInfo = new HashMap<>();
		List<Tower> winningTower = new ArrayList<>();
		winningTower.add(getTowerType());
		userInfo.put(NotificationKeys.WinnerTowerType.getRawValue(), winningTower);
		NotificationCenter.shared().post(NotificationName.PlayerVictory, this, userInfo);
	}
	/**
	 * create a player copy
	 */
	public Player copy() {
		try {
			Player copy = new Player(nickname, wizard, getTowerType(), 0);
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
