package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;

import java.util.HashMap;

/**
 * Class {@code CharacterCardGenericPane} represent the generic {@link it.polimi.ingsw.client.ui.characters.CharacterCardPane CharacterCardPane}
 */
public class CharacterCardGenericPane extends CharacterCardPane {
	/**
	 * Constructor creates the {@code CharacterCardGenericPane}
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} index
	 * @param cardBean (type CharacterCardBean)
	 */
	public CharacterCardGenericPane(int cardIndex, CharacterCardBean cardBean) {
		super(cardIndex, cardBean);
	}
	
	@Override
	protected void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean) {
	
	}
	
	@Override
	protected void executeActionOnClick() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.JavaFXPlayedCharacter.getRawValue(), getCharacter());
		NotificationCenter.shared().post(NotificationName.JavaFXDidPlayCharacterCard, null, userInfo);
	}
	
	@Override
	protected void adaptStudentsForControlLoopClosed(Student[] clickedStudents) {
	}
}
