package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class AssistantPickerPane extends GridPane {
	
	public AssistantPickerPane(AssistantCard[] availableAssistants) {
		super();
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
			count += 1;
			i = count / 5;
			j = count % 5;
		}
	}
}
