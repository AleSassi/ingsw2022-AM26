package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.utils.cli.ModelFormatter;

import java.util.Objects;
/**
 * This Class represents a data object that snapshots a Character card, so that it can be sent over the network to Clients
 * @author Alessandro Sassi
 * @see CharacterCard
 */
public class CharacterCardBean {
	
	private final Character character;
	private final int totalPrice;
	private final Student excludedStudent;
	private final int memorizedModifier;
	private final int availableStopCards;
	private final StudentCollection hostedStudents;

	/**
	 * Constructor, initializes the bean with the card data
	 * @param character (type {@link it.polimi.ingsw.server.model.characters.Character Character}) The Character of the card
	 * @param totalPrice      (type int) The price of the card
	 * @param excludedStudent    (type Student) The {@link it.polimi.ingsw.server.model.student.Student Student} not to consider when computing the influence
	 * @param memorizedModifier (type int) The modifier that the card memorized
	 * @param availableStopCards    (type int) The number of stop cards that are available
	 * @param hostedStudents  (type StudentCollection) A {@link it.polimi.ingsw.server.model.student.StudentCollection collection} of students that the card hosts for its effect
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
	 * Gets the character of the card
	 * @return (type Character) the character of the card
	 */
	public Character getCharacter() {
		return character;
	}
	
	/**
	 * Get the total price of the card
	 * @return (type int) the total price of the card
	 */
	public int getTotalPrice() {
		return totalPrice;
	}
	
	/**
	 * Get the students excluded from the Influence count
	 * @return (type int) The student not to consider when computing the influence
	 */
	public Student getExcludedStudent() {
		return excludedStudent;
	}
	
	/**
	 * Gets the memorized modifier
	 * @return (type int) The memorized modifier
	 */
	public int getMemorizedModifier() {
		return memorizedModifier;
	}
	
	/**
	 * Gets the number of Stop Cards available on the card
	 * @return (type int) number of stop card
	 */
	public int getAvailableStopCards() {
		return availableStopCards;
	}
	
	/**
	 * Gets the collection of Students hosted in the card
	 * @return (type StudentCollection) The collection of Students hosted by the card
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
