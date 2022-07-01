package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.scene.input.MouseEvent;

/**
 * A rescalable anchor pane that displays assistant cards, with support for clicks
 */
public class AssistantCardPane extends RescalableAnchorPane {
	
	private final AssistantCard card;

	/**
	 * Creates the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} pane
	 * @param card (type AssistantCard) The card to display
	 */
	public AssistantCardPane(AssistantCard card) {
		super();
		this.card = card;
		setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/assistants/" + card + ".png") + ");\n-fx-background-size: 100% 100%;\n-fx-background-radius: 15px");
		rescale(getCurrentScaleValue());
	}
	
	@Override
	public double getUnscaledWidth() {
		return 100;
	}
	
	@Override
	public double getUnscaledHeight() {
		return 147;
	}
	
	/**
	 * Sets up the click event with the specified handler function
	 * @param selectionHandler (type AssistantCardSelectionHandler) {@link it.polimi.ingsw.client.ui.assistants.AssistantCardSelectionHandler} The click handler function, called when the user clicks on the card
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
		setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
	}
}
