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
 * This Class represents the {@code PlayerStateView} that shows a textual representation of the Player's School Board and assistant cards
 * @author Alessandro Sassi
 */
public class PlayerStateView extends TerminalView {
	
	private int numberOfCards = 0;
	private int maxMNSteps = 0;
	private Integer purchasedCharacterCard;
	private StudentHost entrance, table;
	private final MatchVariant variant;
	
	/** Initializes the view with the match variant
	 * @param variant (type {@link it.polimi.ingsw.server.model.match.MatchVariant}) The match variant
	 */
	public PlayerStateView(MatchVariant variant) {
		super();
		this.variant = variant;
	}
	
	/** Gets the number of assistant cards that the Player can still play
	 * @return (type int) number of {@link it.polimi.ingsw.server.model.assistants.AssistantCard assistant cards} that the Player can still play
	 */
	public int getNumberOfCards() {
		return numberOfCards;
	}
	
	/** Gets the max number of steps that Mother Nature can move by (as per the played Assistant Card)
	 * @return (type int) max number of steps that Mother Nature can move by
	 */
	public int getMaxMNSteps() {
		return maxMNSteps;
	}
	
	/** Gets the index of the purchased Character Card, or <code>null</code> if no card was purchased
	 * @return (type Integer) index of the {@link it.polimi.ingsw.server.model.characters.CharacterCard character card} purchased by the Player
	 */
	public Integer getPurchasedCharacterCard() {
		return purchasedCharacterCard;
	}
	
	/** Gets the entrance space data
	 * @return (type StudentHost) the {@link it.polimi.ingsw.server.model.student.StudentHost entrance data}
	 */
	public StudentHost getEntrance() {
		return entrance;
	}
	
	/** Gets the dining room space data
	 * @return (type StudentHost) the {@link it.polimi.ingsw.server.model.student.StudentHost dining room data}
	 */
	public StudentHost getTable() {
		return table;
	}
	
	/**
	 * Subscribes to the Notification called when the Client receives a Player State message, so that the vie wcan update itself
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerState, NotificationName.ClientDidReceivePlayerStateMessage, null);
	}
	
	/**
	 * Callback for the {@link it.polimi.ingsw.notifications.Notification notification} with name ClientDidReceivePlayerStateMessage
	 * It pretty-prints an ASCII art-based description of the School Board owned by the Player
	 * @param notification (type Notification) with the event data
	 */
	private void didReceivePlayerState(Notification notification) {
		PlayerStateMessage playerStateMessage = (PlayerStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		boolean isForCLIPlayer = playerStateMessage.getNickname().equals(Client.getNickname());
		if (isForCLIPlayer) {
			String teamString = playerStateMessage.getTeamName() == null ? "" : " - " + playerStateMessage.getTeamName();
			System.out.println(StringFormatter.formatWithColor("Your Board (Tower Color: " + playerStateMessage.getBoard().getTowerType() + ")" + teamString + ":", ANSIColors.Green));
			numberOfCards = playerStateMessage.getAvailableCardsDeck().length;
			if (playerStateMessage.getLastPlayedAssistantCard() != null) {
				maxMNSteps = playerStateMessage.getLastPlayedAssistantCard().getMotherNatureSteps();
			}
			purchasedCharacterCard = playerStateMessage.getActiveCharacterCardIdx();
			entrance = playerStateMessage.getBoard().getEntrance();
			table = playerStateMessage.getBoard().getDiningRoom();
		} else {
			// Show the redux version for tactical purposes
			String teamString = playerStateMessage.getTeamName() == null ? "" : " - " + playerStateMessage.getTeamName();
			System.out.println(StringFormatter.formatWithColor(playerStateMessage.getNickname() + "'s Board (Tower Color: " + playerStateMessage.getBoard().getTowerType() + ")" + teamString + ":", ANSIColors.Yellow));
		}
		System.out.println(buildStringForSchoolBoard(playerStateMessage));
		if (isForCLIPlayer) {
			System.out.println(buildStringForAssistantCards(playerStateMessage));
		}
		System.out.println(getControlledCardString(playerStateMessage));
	}
	
	/**
	 * Displays the data of the active {@link it.polimi.ingsw.server.model.characters.CharacterCard character card}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage} the message with the Player data
	 */
	private StringBuilder getControlledCardString(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		if (playerStateMessage.getActiveCharacterCardIdx() != null) {
			formattedString.append("Controlled Character Card: ").append(playerStateMessage.getActiveCharacterCardIdx());
		}
		return formattedString;
	}
	/**
	 * Displays the data of the Player's {@link it.polimi.ingsw.server.model.SchoolBoard school board} using ASCII art
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage} the message with the Player data
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
	 * Creates the the ASCII art representation of the {@link it.polimi.ingsw.server.model.SchoolBoard school board}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage} the message with the Player data
	 * @return (type StringBuilder) the String with the complete ASCII art
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
	 * Prints a Student using an ASCII art representation, with color-matched SS characters, or "__" if the space is empty
	 * @param stringBuilder (type StringBuilder) the builder where the method should write the strings
	 * @param entrance (type {@link it.polimi.ingsw.server.model.student.StudentHost StudentHost}) the student host object which we need to print the students of
	 * @param lockedStudent (type{@link it.polimi.ingsw.server.model.student.StudentHost Student}) if specified, we only print this student. Otherwise, we print the entire contents of the host
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
	 * Builds the String for displaying the list of available {@link it.polimi.ingsw.server.model.assistants.AssistantCard assistant cards}
	 * @param playerStateMessage {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage} the message with the Player data
	 * @return (type StringBuilder) the formatted String with the list of available assistants
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
