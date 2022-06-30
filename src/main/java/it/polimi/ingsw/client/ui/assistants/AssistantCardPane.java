package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.scene.input.MouseEvent;

/**
 * Class {@code AssistantCardPane} represent the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard's} pane
 */
public class AssistantCardPane extends RescalableAnchorPane {
	
	private final AssistantCard card;

	/**
	 * Creates the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard's} pane
	 * @param card (type AssistantCard)
	 */
	public AssistantCardPane(AssistantCard card) {
		super();
		this.card = card;
		setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/assistants/" + card + ".png") + ");\n-fx-background-size: 100% 100%;\n-fx-background-radius: 15px");
		rescale(getCurrentScaleValue());
	}

	/**
	 * Click event handler
	 * @param selectionHandler (type AssistantCardSelectionHandler) {@link it.polimi.ingsw.client.ui.assistants.AssistantCardSelectionHandler}
	 */
	public void setSelectionHandler(AssistantCardSelectionHandler selectionHandler) {
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			event.consume();
			selectionHandler.handle(card);
		});
	}
	
	/**
	 * Gets the {@link it.polimi.ingsw.server.model.assistants.AssistantCard} displayed by the pane
	 * @return the {@link it.polimi.ingsw.server.model.assistants.AssistantCard} displayed by the pane
	 */
	public AssistantCard getCard() {
		return card;
	}
	
	@Override
	public void rescale(double scale) {
		setPrefSize(100 * scale, 147 * scale);
	}
}
