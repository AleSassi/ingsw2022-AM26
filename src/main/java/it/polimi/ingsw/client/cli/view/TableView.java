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
import it.polimi.ingsw.utils.cli.ModelFormatter;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.util.List;
/**
 * This Class represent the {@code TableView}, which displays the object on the Table
 * @author Alessandro Sassi
 */
public class TableView extends TerminalView {
	
	private int numberOfIslands;
	private int numberOfClouds;
	private List<CharacterCardBean> cardBeans;
	
	/** Gets the number of Island tiles on the Table
	 * @return (type int) number of {@link it.polimi.ingsw.server.model.student.Island island tiles} on the Table
	 */
	public int getNumberOfIslands() {
		return numberOfIslands;
	}
	
	/** Gets the number of Cloud tiles on the Table
	 * @return (type int) number of {@link it.polimi.ingsw.server.model.student.Cloud cloud tiles} on the Table
	 */
	public int getNumberOfClouds() {
		return numberOfClouds;
	}
	
	/** Gets the number of Character Cards on the Table
	 * @return (type int) number of {@link it.polimi.ingsw.server.model.characters.CharacterCard character cards} on the Table
	 */
	public int getNumberOfCards() {
		return cardBeans.size();
	}
	
	/** Gets the character on the Table at the specified index
	 * @param characterIndex (type int) the index of the {@link it.polimi.ingsw.server.model.characters.Character character} to get
	 * @return (type Character) The character at the specified index
	 */
	public Character getCharacterAtIndex(int characterIndex) {
		return cardBeans.get(characterIndex).getCharacter();
	}
	
	/**
	 * Gets the set of Students hosted by a Character card
	 * @param characterIndex (type int) The index of the {@link it.polimi.ingsw.server.model.characters.CharacterCard character card}
	 * @return (type StudentCollection) The {@link it.polimi.ingsw.server.model.student.StudentCollection collection} of Students in a Character card
	 */
	public StudentCollection getStudentsInCardAtIndex(int characterIndex) {
		return cardBeans.get(characterIndex).getHostedStudents();
	}
	
	/**
	 * Subscribes to the Table State Message Received notification in order to auto-update
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
	}
	
	/**
	 * Callback for the {@link it.polimi.ingsw.notifications.Notification notification} with name ClientDidReceiveTableStateMessage
	 * It pretty-prints the data of the table (e.g. Islands, Character Cards)
	 * @param notification (@code Notification) with the vent data
	 */
	private void didReceiveTableState(Notification notification) {
		TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		System.out.println(getAvailableProfessorsString(tableStateMessage));
		System.out.println(getCloudsString(tableStateMessage));
		System.out.println(getIslandsString(tableStateMessage));
		System.out.println(getCharacterCardsString(tableStateMessage));
		numberOfIslands = tableStateMessage.getIslands().size();
		numberOfClouds = tableStateMessage.getManagedClouds().size();
		cardBeans = tableStateMessage.getPlayableCharacterCards();
	}
	
	/**
	 * Builds a String with the list of available character cards
	 * @param tableStateMessage The {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage table message} with the table data
	 * @return (type StringBuilder) the String with the list of available character cards
	 */
	private StringBuilder getCharacterCardsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder();
		if (tableStateMessage.getPlayableCharacterCards() != null && !tableStateMessage.getPlayableCharacterCards().isEmpty()) {
			formattedString.append("Character Cards:");
			int cardIndex = 0;
			for (CharacterCardBean card : tableStateMessage.getPlayableCharacterCards()) {
				formattedString.append("\n\t[").append(cardIndex).append("]:\n");
				formattedString.append(ModelFormatter.formatStringForCharacterCardBean(card));
				cardIndex += 1;
			}
		}
		return formattedString;
	}
	
	/**
	 * Builds a String with the list of islands on the table
	 * @param tableStateMessage The {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage table message} with the table data
	 * @return (type StringBuilder) the String with the list of islands
	 */
	private StringBuilder getIslandsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Islands:");
		formattedString.append(buildStringForIslands(tableStateMessage.getIslands()));
		return formattedString;
	}
	
	/**
	 * Builds a String with the list of Clouds on the table
	 * @param tableStateMessage The {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage table message} with the table data
	 * @return (type StringBuilder) the String with the list of Clouds on the table
	 */
	private StringBuilder getCloudsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Clouds:");
		formattedString.append(buildStringForClouds(tableStateMessage.getManagedClouds()));
		return formattedString;
	}
	
	/**
	 * Builds the formatted string for Clouds
	 * @param hosts (type List {@link it.polimi.ingsw.server.model.student.Cloud Cloud}) The list of Clouds on the table to print
	 * @return (type StringBuilder) the String with the list of Clouds on the table
	 */
	private StringBuilder buildStringForClouds(List<Cloud> hosts) {
		StringBuilder formattedString = new StringBuilder();
		int hostIndex = 0;
		for (Cloud host : hosts) {
			formattedString.append(buildStringForStudentHost(host, hostIndex));
			hostIndex += 1;
		}
		return formattedString;
	}
	
	/**
	 * Builds the formatted string for Islands
	 * @param hosts (type List {@link it.polimi.ingsw.server.model.student.Island Island}) The list of Islands on the table to print
	 * @return (type StringBuilder) the String with the list of Islands on the table
	 */
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
	
	/**
	 * Builds the formatted string for a generic Student Host, found at an index when part of a list
	 * @param host (type {@link it.polimi.ingsw.server.model.student.StudentHost StudentHost}) The Student Host to print
	 * @param hostIndex (type int) The index of the host in the parent list
	 * @return (type StringBuilder) the String with the student host data on the table
	 */
	private StringBuilder buildStringForStudentHost(StudentHost host, int hostIndex) {
		StringBuilder formattedString = new StringBuilder();
		formattedString.append("\n");
		formattedString.append("\t[").append(hostIndex).append("]:");
		formattedString.append("\n\t\t");
		formattedString.append(ModelFormatter.formatStringForStudentHost(host));
		return formattedString;
	}
	
	/**
	 * Builds a String with the list of available Professors on the table
	 * @param tableStateMessage The {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage table message} with the table data
	 * @return (type StringBuilder) the String with the list of available Professors on the table
	 */
	private StringBuilder getAvailableProfessorsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Available Professors: ");
		for (Professor professor : tableStateMessage.getAvailableProfessors()) {
			formattedString.append(StringFormatter.formatWithColor(professor.toString(), ModelFormatter.getProfessorColor(professor))).append(tableStateMessage.getAvailableProfessors().indexOf(professor) < tableStateMessage.getAvailableProfessors().size() - 1 ? " | " : "");
		}
		return formattedString;
	}
	
	@Override
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
	}
}
