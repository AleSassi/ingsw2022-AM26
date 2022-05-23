package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.utils.cli.ModelFormatter;

import java.util.Objects;

public class CharacterCardBean {
	
	private final Character character;
	private final int totalPrice;
	private final Student excludedStudent;
	private final int memorizedModifier;
	private final int availableStopCards;
	private final StudentCollection hostedStudents;
	
	public CharacterCardBean(Character character, int totalPrice, Student excludedStudent, int memorizedModifier, int availableStopCards, StudentCollection hostedStudents) {
		this.character = character;
		this.totalPrice = totalPrice;
		this.excludedStudent = excludedStudent;
		this.memorizedModifier = memorizedModifier;
		this.availableStopCards = availableStopCards;
		this.hostedStudents = hostedStudents;
	}
	
	public Character getCharacter() {
		return character;
	}
	
	public int getTotalPrice() {
		return totalPrice;
	}
	
	public Student getExcludedStudent() {
		return excludedStudent;
	}
	
	public int getMemorizedModifier() {
		return memorizedModifier;
	}
	
	public int getAvailableStopCards() {
		return availableStopCards;
	}
	
	public StudentCollection getHostedStudents() {
		return hostedStudents;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		CharacterCardBean that = (CharacterCardBean) o;
		
		if (totalPrice != that.totalPrice) return false;
		if (memorizedModifier != that.memorizedModifier) return false;
		if (availableStopCards != that.availableStopCards) return false;
		if (character != that.character) return false;
		if (excludedStudent != that.excludedStudent) return false;
		return Objects.equals(hostedStudents, that.hostedStudents);
	}
}
