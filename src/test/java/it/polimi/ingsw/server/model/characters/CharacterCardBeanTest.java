package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the CharacterCardBean
 * @see CharacterCardBean
 */
class CharacterCardBeanTest {
	
	/**
	 * Tests all getters
	 */
	@Test
	void testGetters() {
		StudentCollection hostedStudents = new StudentCollection();
		hostedStudents.addStudents(Student.BlueUnicorn, 3);
		hostedStudents.addStudents(Student.RedDragon, 2);
		CharacterCardBean bean = new CharacterCardBean(Character.Abbot, Character.Abbot.getInitialPrice(), null, 0, 0, hostedStudents);
		assertEquals(Character.Abbot, bean.getCharacter());
		assertEquals(Character.Abbot.getInitialPrice(), bean.getTotalPrice());
		assertNull(bean.getExcludedStudent());
		assertEquals(0, bean.getMemorizedModifier());
		assertEquals(0, bean.getAvailableStopCards());
		assertEquals(hostedStudents, bean.getHostedStudents());
	}
	
}