package it.polimi.ingsw.client.ui.rescale;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.scene.layout.AnchorPane;

public abstract class RescalableAnchorPane extends AnchorPane implements JavaFXRescalable {
	
	public RescalableAnchorPane() {
		super();
		NotificationCenter.shared().addObserver(this, this::didReceiveWindowResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	private void didReceiveWindowResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue);
		}
	}
}
