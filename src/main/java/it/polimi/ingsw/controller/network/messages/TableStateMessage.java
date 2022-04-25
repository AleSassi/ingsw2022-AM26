package it.polimi.ingsw.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.student.Cloud;
import it.polimi.ingsw.model.student.Island;
import it.polimi.ingsw.model.student.StudentHost;

import java.util.List;
import java.util.Objects;

public class TableStateMessage extends NetworkMessage {
	
	private List<Professor> availableProfessors;
	private List<Island> islands;
	private StudentHost studentBag;
	private List<Cloud> managedClouds;
	private List<CharacterCard> playableCharacterCards;
	
	public TableStateMessage(List<Professor> availableProfessors, List<Island> islands, StudentHost studentBag, List<Cloud> managedClouds, List<CharacterCard> playableCharacterCards) {
		this.availableProfessors = availableProfessors;
		this.islands = islands;
		this.studentBag = studentBag;
		this.managedClouds = managedClouds;
		this.playableCharacterCards = playableCharacterCards;
	}
	
	public TableStateMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public List<Professor> getAvailableProfessors() {
		return availableProfessors;
	}
	
	public List<Island> getIslands() {
		return islands;
	}
	
	public StudentHost getStudentBag() {
		return studentBag;
	}
	
	public List<Cloud> getManagedClouds() {
		return managedClouds;
	}
	
	public List<CharacterCard> getPlayableCharacterCards() {
		return playableCharacterCards;
	}
	
	@Override
	String serialize() {
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
