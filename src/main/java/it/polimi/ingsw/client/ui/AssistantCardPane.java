package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

public class AssistantCardPane extends RescalableAnchorPane {
	
	public AssistantCardPane(AssistantCard card) {
		super();
		setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/assistants/" + card + ".png") + ");\n-fx-background-size: 100% 100%;\n-fx-background-radius: 15px");
		rescale(1);
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put("clickedAssistant", card);
			NotificationCenter.shared().post(NotificationName.JavaFXDidClickOnAssistantCard, null, userInfo);
			event.consume();
		});
	}
	
	@Override
	public void rescale(double scale) {
		setPrefSize(100 * scale, 147 * scale);
	}
}
