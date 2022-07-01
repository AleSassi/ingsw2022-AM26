package it.polimi.ingsw.client.ui.rescale;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.viewcontrollers.CleanableController;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * An abstract controller which supports the Rescale feature
 */
public abstract class RescalableController extends CleanableController implements JavaFXRescalable {
	
	private double scaleValue = GUI.getStageScale();
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		super.initialize(url, resourceBundle);
		NotificationCenter.shared().addObserver(this, this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	@Override
	public double getCurrentScaleValue() {
		return scaleValue;
	}
	
	/**
	 * A callback called when a Resize notification is posted by the GUI
	 * @param notification the resize notification
	 */
	private void didReceiveWindowDidResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue);
			this.scaleValue = scaleValue;
		}
	}
	
	@Override
	public abstract void rescale(double scale);
}
