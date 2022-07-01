package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.utils.cli.ModelFormatter;

import java.util.Objects;
/**
 * This Class represent the {@code CharacterCardBean}
 * @author Alessandro Sassi
 */
public class CharacterCardBean {
	/**
	 * initialize {@code CharacterCardBean}
	 */
	private final Character character;
	private final int totalPrice;
	private final Student excludedStudent;
	private final int memorizedModifier;
	private final int availableStopCards;
	private final StudentCollection hostedStudents;

	/**
	 * constructor
	 * @param character (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard}) character of the card
	 * @param totalPrice      (type int) price of the card
	 * @param excludedStudent    (type Student) {@link it.polimi.ingsw.server.model.student.Student Student} student not to consider in count of influence
	 * @param memorizedModifier (type int) modifier that card apply
	 * @param availableStopCards    (type int) number of stop card that are avaible
	 * @param hostedStudents  (type student collection){@link it.polimi.ingsw.server.model.student.StudentCollection collection} student that the card gost for its effect
	 */
	public CharacterCardBean(Character character, int totalPrice, Student excludedStudent, int memorizedModifier, int availableStopCards, StudentCollection hostedStudents) {
		this.character = character;
		this.totalPrice = totalPrice;
		this.excludedStudent = excludedStudent;
		this.memorizedModifier = memorizedModifier;
		this.availableStopCards = availableStopCards;
		this.hostedStudents = hostedStudents;
	}
	/**
	 * getter
	 * @return (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard}) that card rapresent
	 */
	public Character getCharacter() {
		return character;
	}
	/**
	 * getter
	 * @return (type int) the price of card
	 */
	public int getTotalPrice() {
		return totalPrice;
	}
	/**
	 * getter
	 * @return (type int){@link it.polimi.ingsw.server.model.student.Student Student} student not to consider in count of influence
	 */
	public Student getExcludedStudent() {
		return excludedStudent;
	}
	/**
	 * getter
	 * @return (type int) value of modifier memorized
	 */
	public int getMemorizedModifier() {
		return memorizedModifier;
	}
	/**
	 * getter
	 * @return (type int) number of stop card
	 */
	public int getAvailableStopCards() {
		return availableStopCards;
	}
	/**
	 * getter
	 * @return (type collection){@link it.polimi.ingsw.server.model.student.StudentCollection collection} of student hosted by the card
	 */
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
