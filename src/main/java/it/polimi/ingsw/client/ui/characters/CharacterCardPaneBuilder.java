package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;

/**
 * A factory for {@link it.polimi.ingsw.client.ui.characters.CharacterCardPane CharacterCardPanes}
 */
class CharacterCardPaneBuilder {
	
	/**
	 * Builds a {@link it.polimi.ingsw.client.ui.characters.CharacterCardPane CharacterCardPane}
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} index in the table list
	 * @param cardBean (type CharacterCardBean) {@code CharacterCard's} data
	 * @return (type CharacterCardPane) returns a {@code CharacterCardPane} concrete subclass
	 */
	protected static CharacterCardPane buildPane(int cardIndex, CharacterCardBean cardBean) {
		if (cardBean.getHostedStudents() != null && cardBean.getHostedStudents().getTotalCount() > 0) {
			return new CharacterCardStudentHostPane(cardIndex, cardBean);
		} else if (cardBean.getCharacter().getChangesMNSteps()) {
			return new CharacterCardMNStepsPane(cardIndex, cardBean);
		} else if (cardBean.getCharacter() == Character.Thief || cardBean.getCharacter() == Character.Mushroom) {
			return new CharacterCardStudentPickerPane(cardIndex, cardBean);
		} else if (cardBean.getCharacter().getHostsStopCards()) {
			return new CharacterCardStopCardHostPane(cardIndex, cardBean);
		} else {
			return new CharacterCardGenericPane(cardIndex, cardBean);
		}
	}
}
