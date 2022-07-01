package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.ModelFormatter;
import it.polimi.ingsw.utils.cli.StringFormatter;
import org.jetbrains.annotations.Nullable;
/**
 * This Class represent the {@code PlayerStateView}
 * @author Alessandro Sassi
 */
public class PlayerStateView extends TerminalView {
	
	private int numberOfCards = 0;
	private int maxMNSteps = 0;
	private Integer purchasedCharacterCard;
	private StudentHost entrance, table;
	private final MatchVariant variant;
	/**constructor
	 * set variant of match
	 * @param variant (type {@link it.polimi.ingsw.server.model.match.MatchVariant}) type of match
	 */
	public PlayerStateView(MatchVariant variant) {
		super();
		this.variant = variant;
	}
	/**getter
	 * @return (type int)numer of card{@link it.polimi.ingsw.server.model.assistants.AssistantCard assistantcard}
	 */
	public int getNumberOfCards() {
		return numberOfCards;
	}
	/**getter
	 * @return (type int)numer of max MStep
	 */
	public int getMaxMNSteps() {
		return maxMNSteps;
	}
	/**getter
	 * @return (type int)numer of card{@link it.polimi.ingsw.server.model.characters.Character charactercard} character the player purchased
	 */
	public Integer getPurchasedCharacterCard() {
		return purchasedCharacterCard;
	}
	/**getter
	 * @return (type int)numer of card{@link it.polimi.ingsw.server.model.student.StudentHost StudentHost} the entrance
	 */
	public StudentHost getEntrance() {
		return entrance;
	}
	/**getter
	 * @return (type int)numer of card{@link it.polimi.ingsw.server.model.student.StudentHost StudentHost} the dining room
	 */
	public StudentHost getTable() {
		return table;
	}
	/**
	 * create a thread and add observers(one for each type of {@link it.polimi.ingsw.notifications.Notification Notification} we need) on  {@link it.polimi.ingsw.notifications.NotificationCenter Center} type of match
	 * for every (@Code Nofication) that arrive call a differrent method of class according to the name of (@Code Nofication)
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerState, NotificationName.ClientDidReceivePlayerStateMessage, null);
	}
	/**
	 * method called whan arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name didReceivePlayerState
	 display the information about the player
	 * @param notification (@code Notification) that contain the information of player
	 */
	private void didReceivePlayerState(Notification notification) {
		PlayerStateMessage playerStateMessage = (PlayerStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		boolean isForCLIPlayer = playerStateMessage.getNickname().equals(Client.getNickname());
		if (isForCLIPlayer) {
			System.out.println(StringFormatter.formatWithColor("Your Board (Tower Color: " + playerStateMessage.getBoard().getTowerType() + "):", ANSIColors.Green));
			numberOfCards = playerStateMessage.getAvailableCardsDeck().length;
			if (playerStateMessage.getLastPlayedAssistantCard() != null) {
				maxMNSteps = playerStateMessage.getLastPlayedAssistantCard().getMotherNatureSteps();
			}
			purchasedCharacterCard = playerStateMessage.getActiveCharacterCardIdx();
			entrance = playerStateMessage.getBoard().getEntrance();
			table = playerStateMessage.getBoard().getDiningRoom();
		} else {
			// Show the redux version for tactical purposes
			System.out.println(StringFormatter.formatWithColor(playerStateMessage.getNickname() + "'s Board (Tower Color: " + playerStateMessage.getBoard().getTowerType() + "):", ANSIColors.Yellow));
		}
		System.out.println(buildStringForSchoolBoard(playerStateMessage));
		if (isForCLIPlayer) {
			System.out.println(buildStringForAssistantCards(playerStateMessage));
		}
		System.out.println(getControlledCardString(playerStateMessage));
	}
	/**
	 display the information about the active {@link it.polimi.ingsw.server.model.characters.Character charactercard}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage}contain information of player
	 */
	private StringBuilder getControlledCardString(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		if (playerStateMessage.getActiveCharacterCardIdx() != null) {
			formattedString.append("Controlled Character Card: ").append(playerStateMessage.getActiveCharacterCardIdx());
		}
		return formattedString;
	}
	/**
	 create the string for display the information of schoolboard {@link it.polimi.ingsw.server.model.SchoolBoard SchoolBoard}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage}contain information of player
	 */
	private StringBuilder buildStringForSchoolBoard(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		formattedString.append(getSchoolBoardASCIIArt(playerStateMessage));
		if (variant == MatchVariant.ExpertRuleSet) {
			formattedString.append("\nCoins: ").append(playerStateMessage.getAvailableCoins());
		}
		if (playerStateMessage.getLastPlayedAssistantCard() != null) {
			formattedString.append("\nLast Played Assistant: ").append(ModelFormatter.formatStringForAssistantCard(playerStateMessage.getLastPlayedAssistantCard()));
		}
		return formattedString;
	}
	/**
	 create the structure of the draw(in command line using string)  about{@link it.polimi.ingsw.server.model.SchoolBoard SchoolBoard}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage}contain information of player
	 * @return(type stringbuilder)the builded string
	 */
	private StringBuilder getSchoolBoardASCIIArt(PlayerStateMessage playerStateMessage) {
		StringBuilder stringBuilder = new StringBuilder();
		int studentRow = 0;
		StudentHost entrance = playerStateMessage.getBoard().getEntrance().copy();
		StudentHost diningRoom = playerStateMessage.getBoard().getDiningRoom().copy();
		for (int line = 0; line < 11; line++) {
			if (line < 1) {
				stringBuilder.append(".".repeat(54));
			} else if ((line - 1) % 2 == 0) {
				stringBuilder.append(".".repeat(2));
				if (studentRow == 0) {
					stringBuilder.append(".".repeat(3));
					extractAndPrintStudent(stringBuilder, entrance, null);
				} else {
					extractAndPrintStudent(stringBuilder, entrance, null);
					stringBuilder.append(".");
					extractAndPrintStudent(stringBuilder, entrance, null);
				}
				stringBuilder.append(".|.");
				//Print the Dining Room, with a ** for coin spaces
				Student rowStudent = Student.values()[studentRow];
				for (int i = 0; i < 10; i++) {
					extractAndPrintStudent(stringBuilder, diningRoom, rowStudent);
					stringBuilder.append(".");
				}
				stringBuilder.append("|.");
				//Print the Professors
				Professor rowProfessor = rowStudent.getAssociatedProfessor();
				if (playerStateMessage.getBoard().getControlledProfessors().contains(rowProfessor)) {
					stringBuilder.append(StringFormatter.formatWithColor("PP", ModelFormatter.getProfessorColor(rowProfessor)));
				} else {
					stringBuilder.append(StringFormatter.formatWithColor("__", ModelFormatter.getProfessorColor(rowProfessor)));
				}
				stringBuilder.append(".|.");
				//Print the Tower space
				if (studentRow * 2 < playerStateMessage.getBoard().getAvailableTowerCount()) {
					stringBuilder.append(StringFormatter.formatWithColor("TT", ANSIColors.Unknown));
				} else {
					stringBuilder.append(StringFormatter.formatWithColor("__", ANSIColors.Unknown));
				}
				stringBuilder.append(".");
				if ((studentRow * 2) + 1 < playerStateMessage.getBoard().getAvailableTowerCount()) {
					stringBuilder.append(StringFormatter.formatWithColor("TT", ANSIColors.Unknown));
				} else {
					stringBuilder.append(StringFormatter.formatWithColor("__", ANSIColors.Unknown));
				}
				stringBuilder.append("..");
			} else {
				stringBuilder.append(".".repeat(54));
				studentRow += 1;
			}
			stringBuilder.append("\n");
		}
		return stringBuilder;
	}
	/**
	 print in
	 * @param stringBuilder (type stringBuilder)the string builder actually in construction
	the student of type
	 * @param lockedStudent (type{@link it.polimi.ingsw.server.model.student.StudentHost Student})
	 * if there is it in
	 * @param entrance (type{@link it.polimi.ingsw.server.model.student.StudentHost SHost})
	 */
	private void extractAndPrintStudent(StringBuilder stringBuilder, StudentHost entrance, @Nullable Student lockedStudent) {
		boolean hasPrintedStudent = false;
		if (lockedStudent != null) {
			try {
				entrance.removeStudents(lockedStudent, 1);
				// If it succeeded we print the student
				stringBuilder.append(StringFormatter.formatWithColor("SS", ModelFormatter.getProfessorColor(lockedStudent.getAssociatedProfessor())));
				hasPrintedStudent = true;
			} catch (CollectionUnderflowError ignored) {
			}
		} else {
			for (Student student: Student.values()) {
				try {
					entrance.removeStudents(student, 1);
					// If it succeeded we print the student
					stringBuilder.append(StringFormatter.formatWithColor("SS", ModelFormatter.getProfessorColor(student.getAssociatedProfessor())));
					hasPrintedStudent = true;
					break;
				} catch (CollectionUnderflowError ignored) {
				}
			}
		}
		if (!hasPrintedStudent) {
			stringBuilder.append("__");
		}
	}
	/**
	 create the string for display the information of assistant card {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage}contain information of player
	 */
	private StringBuilder buildStringForAssistantCards(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder("Assistant Cards available: ");
		int index = 0;
		for (AssistantCard assistantCard : playerStateMessage.getAvailableCardsDeck()) {
			formattedString.append("\n\t[").append(index).append("]: ").append(ModelFormatter.formatStringForAssistantCard(assistantCard));
			index += 1;
		}
		
		return formattedString;
	}
	
	@Override
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
	}
}
