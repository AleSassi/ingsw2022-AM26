package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.student.Cloud;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.ModelFormatter;
import it.polimi.ingsw.utils.cli.StringFormatter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerStateView extends TerminalView {
	
	private int numberOfCards = 0;
	private int maxMNSteps = 0;
	private Integer purchasedCharacterCard;
	private StudentHost entrance, table;
	private final MatchVariant variant;
	
	public PlayerStateView(MatchVariant variant) {
		super();
		this.variant = variant;
	}
	
	public int getNumberOfCards() {
		return numberOfCards;
	}
	
	public int getMaxMNSteps() {
		return maxMNSteps;
	}
	
	public Integer getPurchasedCharacterCard() {
		return purchasedCharacterCard;
	}
	
	public StudentHost getEntrance() {
		return entrance;
	}
	
	public StudentHost getTable() {
		return table;
	}
	
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this::didReceivePlayerState, NotificationName.ClientDidReceivePlayerStateMessage, null);
	}
	
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
	
	private StringBuilder getControlledCardString(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		if (playerStateMessage.getActiveCharacterCardIdx() != null) {
			formattedString.append("Controlled Character Card: ").append(playerStateMessage.getActiveCharacterCardIdx());
		}
		return formattedString;
	}
	
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
					stringBuilder.append(StringFormatter.formatWithColor("PP", rowProfessor.getProfessorColor()));
				} else {
					stringBuilder.append(StringFormatter.formatWithColor("__", rowProfessor.getProfessorColor()));
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
	
	private void extractAndPrintStudent(StringBuilder stringBuilder, StudentHost entrance, @Nullable Student lockedStudent) {
		boolean hasPrintedStudent = false;
		if (lockedStudent != null) {
			try {
				entrance.removeStudents(lockedStudent, 1);
				// If it succeeded we print the student
				stringBuilder.append(StringFormatter.formatWithColor("SS", lockedStudent.getAssociatedProfessor().getProfessorColor()));
				hasPrintedStudent = true;
			} catch (CollectionUnderflowError ignored) {
			}
		} else {
			for (Student student: Student.values()) {
				try {
					entrance.removeStudents(student, 1);
					// If it succeeded we print the student
					stringBuilder.append(StringFormatter.formatWithColor("SS", student.getAssociatedProfessor().getProfessorColor()));
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
