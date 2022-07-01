package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Cloud;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.StudentHost;

import java.util.List;
import java.util.Objects;
/**
 * Class {@code TableStateMessage} is a message which represents the state of the Table
 */
public class TableStateMessage extends NetworkMessage {
	
	private List<Professor> availableProfessors;
	private List<Island> islands;
	private StudentHost studentBag;
	private List<Cloud> managedClouds;
	private List<CharacterCardBean> playableCharacterCards;
	
	/**
	 * Constructs the message with its raw data
	 * @param availableProfessors The list of available professors
	 * @param islands The list of Islands on the table
	 * @param studentBag The Bag from which students are extracted
	 * @param managedClouds The list of Clouds
	 * @param playableCharacterCards The list of playable characters
	 */
	public TableStateMessage(List<Professor> availableProfessors, List<Island> islands, StudentHost studentBag, List<Cloud> managedClouds, List<CharacterCardBean> playableCharacterCards) {
		this.availableProfessors = availableProfessors;
		this.islands = islands;
		this.studentBag = studentBag;
		this.managedClouds = managedClouds;
		this.playableCharacterCards = playableCharacterCards;
	}
	
	/**
	 * Decodes a JSON serialized string into a message
	 * @param serializedString The serialized string
	 * @throws MessageDecodeException If the decode fails
	 */
	public TableStateMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Extracts the list of available professors
	 * @return The list of available professors
	 */
	public List<Professor> getAvailableProfessors() {
		return availableProfessors;
	}
	
	/**
	 * Extracts the list of islands
	 * @return The list of islands
	 */
	public List<Island> getIslands() {
		return islands;
	}
	
	/**
	 * Extracts the bag
	 * @return The bag
	 */
	public StudentHost getStudentBag() {
		return studentBag;
	}
	
	/**
	 * Extracts the list of clouds
	 * @return The list of clouds
	 */
	public List<Cloud> getManagedClouds() {
		return managedClouds;
	}
	
	/**
	 * Extracts the list of character cards
	 * @return The list of character cards
	 */
	public List<CharacterCardBean> getPlayableCharacterCards() {
		return playableCharacterCards;
	}
	
	@Override
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@Override
	protected void deserialize(String serializedString) throws MessageDecodeException {
		Gson gson = new Gson();
		try {
			TableStateMessage decoded = gson.fromJson(serializedString, TableStateMessage.class);
			availableProfessors = decoded.availableProfessors;
			islands = decoded.islands;
			studentBag = decoded.studentBag;
			managedClouds = decoded.managedClouds;
			playableCharacterCards = decoded.playableCharacterCards;
			
			if (availableProfessors == null || islands == null || studentBag == null || managedClouds == null || playableCharacterCards == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceiveTableStateMessage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		TableStateMessage that = (TableStateMessage) o;
		
		if (!Objects.equals(availableProfessors, that.availableProfessors))
			return false;
		if (!Objects.equals(islands, that.islands)) return false;
		if (!Objects.equals(studentBag, that.studentBag)) return false;
		if (!Objects.equals(managedClouds, that.managedClouds))
			return false;
		return Objects.equals(playableCharacterCards, that.playableCharacterCards);
	}
}
