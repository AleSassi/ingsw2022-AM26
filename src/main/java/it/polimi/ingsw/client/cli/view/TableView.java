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
 * This Class represent the {@code TableView}
 * @author Alessandro Sassi
 */
public class TableView extends TerminalView {
	
	private int numberOfIslands;
	private int numberOfClouds;
	private List<CharacterCardBean> cardBeans;
	/**getter
	 * @return (type int)numer of Island{@link it.polimi.ingsw.server.model.student.Island Island}
	 */
	public int getNumberOfIslands() {
		return numberOfIslands;
	}
	/**getter
	 * @return (type int)numer of Cloud{@link it.polimi.ingsw.server.model.student.Cloud Cloud}
	 */
	public int getNumberOfClouds() {
		return numberOfClouds;
	}
	/**getter
	 * @return (type int)number of Cards{@link it.polimi.ingsw.server.model.characters.Character Character}
	 */
	public int getNumberOfCards() {
		return cardBeans.size();
	}
	/**getter
	 * @param characterIndex (type int)number of Cards{@link it.polimi.ingsw.server.model.characters.Character} index of card
	 * @return (type Character) card choosen
	 */
	public Character getCharacterAtIndex(int characterIndex) {
		return cardBeans.get(characterIndex).getCharacter();
	}
	/**
	 * return the student hosted by the card at index
	 * @param characterIndex (type int) number of Cards{@link it.polimi.ingsw.server.model.characters.Character} index of card
	 * @return (type StudentCollection) {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection}

	 */
	public StudentCollection getStudentsInCardAtIndex(int characterIndex) {
		return cardBeans.get(characterIndex).getHostedStudents();
	}
	/**
	 * create a thread and add observers(one for each type of {@link it.polimi.ingsw.notifications.Notification Notification} we need) on  {@link it.polimi.ingsw.notifications.NotificationCenter Center} type of match
	 * for every (@Code Nofication) that arrive call a differrent method of class according to the name of (@Code Nofication)
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
	}
	/**
	 * method called when arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name ActivePlayer
	 *the method print all yhe information about the table
	 * @param notification (@code Notification) that contain the information of event
	 */
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
	/**
	 use the information in
	 * @param tableStateMessage {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage tableStateMessage}contain information of player
	 to know the playable character{@link it.polimi.ingsw.server.model.characters.Character Character} and insert them in a
	 * @return(type stringbuilder)the string builded with all the  cards
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
	 use the information in
	 * @param tableStateMessage {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage tableStateMessage}contain information of player
	to know the island remaining{@link it.polimi.ingsw.server.model.student.Island Island} and insert them in a
	 * @return(type stringbuilder)the string builded with all the islands
	 */
	private StringBuilder getIslandsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Islands:");
		formattedString.append(buildStringForIslands(tableStateMessage.getIslands()));
		return formattedString;
	}
	/**
	 use the information in
	 * @param tableStateMessage {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage tableStateMessage}contain information of player
	to know the Cloud{@link it.polimi.ingsw.server.model.student.Cloud Cloud} and insert them in a
	 * @return(type stringbuilder)the string builded with all the Cloud
	 */

	private StringBuilder getCloudsString(TableStateMessage tableStateMessage) {
		StringBuilder formattedString = new StringBuilder("Clouds:");
		formattedString.append(buildStringForClouds(tableStateMessage.getManagedClouds()));
		return formattedString;
	}
	/**
	 take the
	 * @param hosts (type list of {@link it.polimi.ingsw.server.model.student.Cloud Cloud} and insert them in a
	 * @return(type stringbuilder)the string builded with all the Cloud
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
	 take the
	 * @param hosts (type list of {@link it.polimi.ingsw.server.model.student.Island Island} and their information, and insert them in a
	 * @return(type stringbuilder)the string builded with all the Island
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
	 take the
	 * @param hostIndex (type {@link it.polimi.ingsw.server.model.student.StudentHost hostIndex}) index of studenthost
	 *and start to build the(@code stringbuilder) with index inside brackets, then complete the (@code stringbuilder) calling method(@code formatStringForStudentHost()) passing him the
	 * @param host (type {@link it.polimi.ingsw.server.model.student.StudentHost host} and take the result
	 * @return(type stringbuilder)the string builded with all the student host and the index
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
	 use the information in
	 * @param tableStateMessage {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage tableStateMessage}contain information of player
	 *to know all the avaible{@link it.polimi.ingsw.server.model.Professor professor} and insert them in a
	 * @return(type stringbuilder)the string builded with all avaible professors
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
