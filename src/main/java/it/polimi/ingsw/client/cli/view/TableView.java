package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Cloud;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.student.StudentHost;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.util.List;

public class TableView extends TerminalView {
	
	private int numberOfIslands;
	private int numberOfClouds;
	private List<CharacterCardBean> cardBeans;
	
	public int getNumberOfIslands() {
		return numberOfIslands;
	}
	
	public int getNumberOfClouds() {
		return numberOfClouds;
	}
	
	public int getNumberOfCards() {
		return cardBeans.size();
	}
	
	public Character getCharacterAtIndex(int characterIndex) {
		return cardBeans.get(characterIndex).getCharacter();
	}
	
	public StudentCollection getStudentsInCardAtIndex(int characterIndex) {
		return cardBeans.get(characterIndex).getHostedStudents();
	}
	
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
	}
	
	private void didReceiveTableState(Notification notification) {
		TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		//TODO: Complete with ASCII Art Table Representation
		System.out.println(getAvailableProfessorsString(tableStateMessage));
		System.out.println(getCloudsString(tableStateMessage));
		System.out.println(getIslandsString(tableStateMessage));
		System.out.println(getCharacterCardsString(tableStateMessage));
		numberOfIslands = tableStateMessage.getIslands().size();
		numberOfClouds = tableStateMessage.getManagedClouds().size();
		cardBeans = tableStateMessage.getPlayableCharacterCards();
	}
	
	private StringBuilder getCharacterCardsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		if (tableStateMessage.getPlayableCharacterCards() != null && !tableStateMessage.getPlayableCharacterCards().isEmpty()) {
			formattedString.append("Character Cards:");
			int cardIndex = 0;
			for (CharacterCardBean card : tableStateMessage.getPlayableCharacterCards()) {
				formattedString.append("\n\t[").append(cardIndex).append("]:\n");
				formattedString.append(card.toFormattedString());
				cardIndex += 1;
			}
		}
		return formattedString;
	}
	
	private StringBuilder getIslandsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Islands:");
		formattedString.append(buildStringForIslands(tableStateMessage.getIslands()));
		return formattedString;
	}
	
	private StringBuilder getCloudsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Clouds:");
		formattedString.append(buildStringForClouds(tableStateMessage.getManagedClouds()));
		return formattedString;
	}
	
	private StringBuilder buildStringForClouds(List<Cloud> hosts) {
		StringBuilder formattedString = new StringBuilder();
		int hostIndex = 0;
		for (Cloud host : hosts) {
			formattedString.append(buildStringForStudentHost(host, hostIndex));
			hostIndex += 1;
		}
		return formattedString;
	}
	
	private StringBuilder buildStringForIslands(List<Island> hosts) {
		StringBuilder formattedString = new StringBuilder();
		int hostIndex = 0;
		for (Island host : hosts) {
			formattedString.append(buildStringForStudentHost(host, hostIndex));
			if (host.isMotherNaturePresent()) {
				formattedString.append("\n\t\t").append("Mother Nature");
			}
			if (host.getActiveTowerType() != null) {
				formattedString.append("\n\t\t").append("Towers: ").append(host.getActiveTowerType()).append(" (").append(host.getTowerCount()).append(")");
			}
			if (host.itHasStopCard()) {
				formattedString.append("\n\t\t STOPPED");
			}
			hostIndex += 1;
		}
		return formattedString;
	}
	
	private StringBuilder buildStringForStudentHost(StudentHost host, int hostIndex) {
		StringBuilder formattedString = new StringBuilder();
		formattedString.append("\n");
		formattedString.append("\t[").append(hostIndex).append("]:");
		formattedString.append("\n\t\t");
		formattedString.append(host.toFormattedString());
		return formattedString;
	}
	
	private StringBuilder getAvailableProfessorsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Available Professors: ");
		for (Professor professor : tableStateMessage.getAvailableProfessors()) {
			formattedString.append(StringFormatter.formatWithColor(professor.toString(), professor.getProfessorColor())).append(tableStateMessage.getAvailableProfessors().indexOf(professor) < tableStateMessage.getAvailableProfessors().size() - 1 ? " | " : "");
		}
		return formattedString;
	}
	
	@Override
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
	}
}
