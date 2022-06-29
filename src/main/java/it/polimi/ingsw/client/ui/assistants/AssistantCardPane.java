package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.client.ui.RescalableAnchorPane;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.scene.input.MouseEvent;

public class AssistantCardPane extends RescalableAnchorPane {
	
	private final AssistantCard card;
	
	public AssistantCardPane(AssistantCard card) {
		super();
		this.card = card;
		setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/assistants/" + card + ".png") + ");\n-fx-background-size: 100% 100%;\n-fx-background-radius: 15px");
		rescale(1);
	}
	
	public void setSelectionHandler(AssistantCardSelectionHandler selectionHandler) {
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			event.consume();
			selectionHandler.handle(card);
		});
	}
	
	@Override
	public void rescale(double scale) {
		setPrefSize(100 * scale, 147 * scale);
	}
}
