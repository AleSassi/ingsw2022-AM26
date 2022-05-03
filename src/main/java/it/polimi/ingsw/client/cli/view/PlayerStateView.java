package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Cloud;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.StudentHost;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.util.List;

public class PlayerStateView extends TerminalView {
	
	private int numberOfCards = 0;
	private int maxMNSteps = 0;
	
	public int getNumberOfCards() {
		return numberOfCards;
	}
	
	public int getMaxMNSteps() {
		return maxMNSteps;
	}
	
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this::didReceivePlayerState, NotificationName.ClientDidReceivePlayerStateMessage, null);
	}
	
	private void didReceivePlayerState(Notification notification) {
		PlayerStateMessage playerStateMessage = (PlayerStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		//TODO: Complete with ASCII Art Table Representation
		System.out.println(buildStringForSchoolBoard(playerStateMessage));
		System.out.println(buildStringForAssistantCards(playerStateMessage));
		System.out.println(getControlledCardString(playerStateMessage));
		numberOfCards = playerStateMessage.getAvailableCardsDeck().length;
		if (playerStateMessage.getLastPlayedAssistantCard() != null) {
			maxMNSteps = playerStateMessage.getLastPlayedAssistantCard().getMotherNatureSteps();
		}
	}
	
	private StringBuilder getControlledCardString(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		if (playerStateMessage.getActiveCharacterCardIdx() != null) {
			formattedString.append("Controlled Character Card: ").append(playerStateMessage.getActiveCharacterCardIdx());
		}
		return formattedString;
	}
	
	private StringBuilder buildStringForSchoolBoard(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder("School Board: ");
		formattedString.append("\n");
		formattedString.append("\tEntrance: ").append(playerStateMessage.getBoard().getEntrance().toFormattedString());
		formattedString.append("\n\tDining Room: ").append(playerStateMessage.getBoard().getDiningRoom().toFormattedString());
		formattedString.append("\t\tTowers: ").append(playerStateMessage.getBoard().getTowerType().toString()).append(" [").append(playerStateMessage.getBoard().getAvailableTowerCount()).append("]");
		formattedString.append("\n\t").append(getAvailableProfessorsString(playerStateMessage));
		formattedString.append("\n\tCoins: ").append(playerStateMessage.getAvailableCoins());
		if (playerStateMessage.getLastPlayedAssistantCard() != null) {
			formattedString.append("\n\tLast Played Assistant: ").append(playerStateMessage.getLastPlayedAssistantCard().toString());
		}
		return formattedString;
	}
	
	private StringBuilder getAvailableProfessorsString(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder("Controlled Professors: ");
		for (Professor professor : playerStateMessage.getBoard().getControlledProfessors()) {
			formattedString.append(StringFormatter.formatWithColor(professor.toString(), professor.getProfessorColor())).append(playerStateMessage.getBoard().getControlledProfessors().indexOf(professor) < playerStateMessage.getBoard().getControlledProfessors().size() - 1 ? " | " : "");
		}
		return formattedString;
	}
	
	private StringBuilder buildStringForAssistantCards(PlayerStateMessage playerStateMessage) {
		StringBuilder formattedString = new StringBuilder("Assistant Cards available: ");
		int index = 0;
		for (AssistantCard assistantCard : playerStateMessage.getAvailableCardsDeck()) {
			formattedString.append("\n\t[").append(index).append("]: ").append(assistantCard);
			index += 1;
		}
		
		return formattedString;
	}
}
