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
 * This Class rapresent the {@code Player}
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
	 getter
	 @retun (type int)the modifier of order of assistant card
	 */
	public int getAssistantCardOrderModifier() {
		return assistantCardOrderModifier;
	}
	/**
	 setter
	 @param assistantCardOrderModifier (type int)the modifier of order of assistant card
	 */

	public void setAssistantCardOrderModifier(int assistantCardOrderModifier) {
		this.assistantCardOrderModifier = assistantCardOrderModifier;
	}

	/**
	 getter
	 @retun (type string)the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 getter
	 @retun (type list of AssistantCard) {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} the avaible (@code AssistantCard)
	 */
	public ArrayList<AssistantCard> getAvailableAssistantCards() {
		ArrayList<AssistantCard> cards = new ArrayList<>();
		for (int i = 0; i < availableCardsDeck.getCount(); i++) {
			cards.add(availableCardsDeck.getCard(i));
		}
		return cards;
	}

	/**
	 getter
	 @retun (type AssistantCard) {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard}last played (@code AssistantCard)
	 */
	public AssistantCard getLastPlayedAssistantCard() {
		return playedCardDeck.topCard();
	}
	/**
	 getter
	 @retun (type list of Professors) {@link it.polimi.ingsw.server.model.Professor Professor}all controlled (@code Professor)
	 */
	public ArrayList<Professor> getControlledProfessors() {
		return board.getControlledProfessors();
	}

	/**
	 * @return number of  {@link it.polimi.ingsw.server.model.student.Student student}, that has type
	 * @param s (type of student) chosen {@code Student}
	 */
	public int getCountAtTable(Student s) {
		return board.getCountAtTable(s);
	}

	/**
	 getter
	 @retun (type CharacterCard) {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard}active(@code CharacterCard)
	 */
	public CharacterCard getActiveCharacterCard() {
		return playedCard;
	}
	/**
	 getter
	 @retun (type SchoolBoard) {@link it.polimi.ingsw.server.model.SchoolBoard SchoolBoard}the board (@code SchoolBoard)
	 */
	public SchoolBoard getBoard() {
		return board.copy();
	}
	/**
	 getter
	 @retun (type int) the number of coin
	 */
	public int getAvailableCoins() {
		return availableCoins;
	}
	/**
	 getter
	 @retun (type Wizard) {@link it.polimi.ingsw.server.model.assistants.Wizard Wizard}the Wizard (@code Wizard)
	 */
	public Wizard getWizard() {
		return wizard;
	}

	/**
	 * remove the  {@link it.polimi.ingsw.server.model.characters.Character}, that has index
	 * @param cardIndex (int) index of card from {@code PlayerCardDeck}
	 * @throws CollectionUnderflowError whenever {@code PlayerCardDeck} hasn't sufficent element
	 */
	
	public void playAssistantCardAtIndex(int cardIndex) throws CollectionUnderflowError {
		playedCardDeck.addCardOnTop(availableCardsDeck.removeCard(cardIndex));
		assistantCardOrderModifier = 0;
	}

	/**
	 * add a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
	 * @param s (type of student) chosen {@code Student} to {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 */
	public void addStudentToEntrance(Student s) {
		board.addStudentToEntrance(s);
	}

	/**
	 * remove a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
	 * @param s (type of student) chosen {@code Student} to {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 */
	public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
		board.removeStudentFromEntrance(s);
	}

	/**
	 * add a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
	 * @param s (type of student) chosen {@code Student}, to the dining room of {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 * if is the third card added increment the coin of player
	 * @param tableManager link to the {@link it.polimi.ingsw.server.model.TableManager},
	 * @throws TableFullException whenever {@link it.polimi.ingsw.server.model.SchoolBoard board} is full
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
	 * remove a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
	 * @param s (type of student) chosen {@code Student}, to the dining room of {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 * @throws CollectionUnderflowError whenever {@link it.polimi.ingsw.server.model.SchoolBoard board} doesn't have sufficent student of type s
	 */
	public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
		board.removeStudentFromTable(s);
	}
	/**
	 * add a {@link it.polimi.ingsw.server.model.Professor professor}, that has type
	 * @param p (type of professor) chosen {@code Professor}, to the dining room of {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 */
	public void addProfessor(Professor p) {
		board.setControlledProfessor(p);
	}
	/**
	 * remove a {@link it.polimi.ingsw.server.model.Professor professor}, that has type
	 * @param p (type of professor) chosen {@code Professor}, to the dining room of {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 */
	public void removeProfessor(Professor p) {
		board.removeProfessorControl(p);
	}
	/**
	 getter
	 @retun (type tower) {@link it.polimi.ingsw.server.model.Tower Tower}the type of (@code Tower)
	 */
	public Tower getTowerType() {
		return board.getTowerType();
	}
	/**
	 * increment the counter of tower of {@link it.polimi.ingsw.server.model.SchoolBoard board}
	 @throws TooManyTowersException whenever the number of tower of {@link it.polimi.ingsw.server.model.SchoolBoard board} has max value
	 */
	public void gainTower() throws TooManyTowersException {
		board.gainTower();
	}
	/**
	 getter
	 @retun (type int) number of avaible {@link it.polimi.ingsw.server.model.Tower Tower}
	 */
	public int getAvailableTowerCount() {
		return board.getAvailableTowerCount();
	}
	/**
	 * decrment the counter of tower of {@link it.polimi.ingsw.server.model.SchoolBoard board} calling is method
	 @throws InsufficientTowersException whenever the number of tower of {@link it.polimi.ingsw.server.model.SchoolBoard board} doen't have sufficent tower
	 */
	public Tower pickAndRemoveTower() throws InsufficientTowersException {
		return board.pickAndRemoveTower();
	}
	/**
	 * add all {@link it.polimi.ingsw.server.model.student.Student student}, that is inside
	 * @param sc (list of student) to {@link it.polimi.ingsw.server.model.SchoolBoard board}
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
	 * if the player have sufficent coin set the
	 * @param card (@Code CharacterCard) as variable playedCard
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
	 * deactivate the card setting variable playedCard to null
	 */
	public void deactivateCard() {
		playedCard = null;
	}
	/**
	 *add {@link it.polimi.ingsw.notifications.NotificationName notification} to {@link it.polimi.ingsw.notifications.NotificationCenter notcenter} to notify the win
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
	 * create a copy of {@link it.polimi.ingsw.server.model.Player player}
	 * @return (@code Player) the copy
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
	 getter
	 @retun (type int)the number of  {@link it.polimi.ingsw.server.model.student.Student Student}
	 */
	public int getStudentsInEntrance() {
		return board.getNumberOfStudentsInEntrance();
	}
	
	@Override
	/**verify if this class is equal to
	 * @param(type Object)
	 * @return (type bool) true if class are equal, false otherwise
	 */
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
