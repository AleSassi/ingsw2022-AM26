package it.polimi.ingsw.client.ui.rescale;

import it.polimi.ingsw.client.ui.AutoCleanableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public abstract class RescalableAnchorPane extends AutoCleanableAnchorPane implements JavaFXRescalable {
	
	private double scalingValue = 1.0;
	private double scaleValue = 1.0;
	
	public RescalableAnchorPane() {
		super();
		NotificationCenter.shared().addObserver(this, this::didReceiveWindowResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	public void setScalingValue(double scalingValue) {
		this.scalingValue = scalingValue;
		rescale(scaleValue * scalingValue);
	}
	
	private void didReceiveWindowResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue * scalingValue);
			this.scaleValue = scaleValue;
		}
	}
}
