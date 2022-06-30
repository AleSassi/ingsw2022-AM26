package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.server.model.assistants.AssistantCard;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class AssistantPickerPane extends GridPane {
	
	private final List<AssistantCardPane> assistantCardPanes;
	
	public AssistantPickerPane(AssistantCard[] availableAssistants) {
		super();
		this.assistantCardPanes = new ArrayList<>();
		int count = 0;
		int i = 0, j = 0;
		for (AssistantCard assistantCard: availableAssistants) {
			AssistantCardPane assistantCardPane = new AssistantCardPane(assistantCard);
			int finalI = i;
			int finalJ = j;
			Platform.runLater(() -> {
				GridPane.setRowIndex(assistantCardPane, finalI);
				GridPane.setColumnIndex(assistantCardPane, finalJ);
				getChildren().add(assistantCardPane);
			});
			assistantCardPanes.add(assistantCardPane);
			count += 1;
			i = count / 5;
			j = count % 5;
		}
	}
	
	public void setSelectionHandler(AssistantCardSelectionHandler selectionHandler) {
		for (AssistantCardPane cardPane: assistantCardPanes) {
			cardPane.setSelectionHandler(selectionHandler);
		}
	}
}