package it.polimi.ingsw.client.ui.rescale;

import it.polimi.ingsw.client.ui.AutoCleanableAnchorPane;
import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.scene.layout.AnchorPane;
/**
 * Abstract class representing a rescalable {@code AnchorPane}
 */
public abstract class RescalableAnchorPane extends AutoCleanableAnchorPane implements JavaFXRescalable {
	
	/**
	 * The additional scaling multiplier to be applied to the object
	 */
	private double scalingValue = 1.0;
	
	/**
	 * The cached, unmultiplied scale value
	 */
	private double scaleValue = GUI.getStageScale();
	
	/**
	 * Constructor creates the rescalable {@code AnchorPane}
	 */
	public RescalableAnchorPane() {
		super();
		NotificationCenter.shared().addObserver(this, this::didReceiveWindowResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	/**
	 * Gets the width of the pane as if the scaling factor was 1
	 * @return the unscaled width of the pane
	 */
	public abstract double getUnscaledWidth();
	/**
	 * Gets the height of the pane as if the scaling factor was 1
	 * @return the unscaled height of the pane
	 */
	public abstract double getUnscaledHeight();
	
	/**
	 * Sets a scaling value to be applied as a multiplier to the screen scale
	 * @param scalingValue The multiplier to be applied to the screen scale before issuing a rescale notification
	 */
	public void setScalingValue(double scalingValue) {
		this.scalingValue = scalingValue;
		rescale(getCurrentScaleValue());
	}
	
	/**
	 * Sets the scaling value to be applied as a multiplier to the screen scale
	 * @return The multiplier to be applied to the screen scale before issuing a rescale notification
	 */
	public double getScalingValue() {
		return scalingValue;
	}
	
	/**
	 * Gets the current, premultiplied scaling factor for the pane
	 * @return The current, premultiplied scaling factor for the pane
	 */
	public double getCurrentScaleValue() {
		return scaleValue * scalingValue;
	}
	
	/**
	 * A callback called when a Resize notification is posted by the GUI
	 * @param notification the resize notification
	 */
	private void didReceiveWindowResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue * scalingValue);
			this.scaleValue = scaleValue;
		}
	}
}
