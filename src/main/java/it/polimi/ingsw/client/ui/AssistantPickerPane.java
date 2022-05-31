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

public class AssistantPickerPane extends GridPane implements JavaFXRescalable {
	
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
		NotificationCenter.shared().addObserver(this::didReceiveWindowResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	private void didReceiveWindowResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue);
		}
	}
	
	@Override
	public void rescale(double scale) {
		for (int i = 0; i < 2; i++) {
			RowConstraints row = new RowConstraints(147 * scale);
			getRowConstraints().add(row);
		}
		for (int i = 0; i < 5; i++) {
			ColumnConstraints col = new ColumnConstraints(100 * scale);
			getColumnConstraints().add(col);
		}
		setVgap(20 * scale);
		setHgap(20 * scale);
	}
}