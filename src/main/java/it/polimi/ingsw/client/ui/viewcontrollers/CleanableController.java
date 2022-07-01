package it.polimi.ingsw.client.ui.viewcontrollers;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A JavFX Controller which auto-cleans itself as subscriber for notifications
 */
public abstract class CleanableController implements Initializable {
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		NotificationCenter.shared().addObserver(this, this::startCleanupAfterTermination, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
		NotificationCenter.shared().addObserver(this, this::startCleanupAfterTermination, NotificationName.ClientDidTimeoutNetwork, null);
	}
	
	/**
	 * After receiving a termination message, it starts the cleanup process
	 * @param notification The termination notification
	 */
	private void startCleanupAfterTermination(Notification notification) {
		new Thread(this::doCleanupAfterTermination).start();
	}
	
	/**
	 * Removes the object from the observer list and calls the custom cleanup code
	 */
	private void doCleanupAfterTermination() {
		NotificationCenter.shared().removeObserver(this);
		cleanupAfterTermination();
	}
	
	/**
	 * An abstract method used by subclasses to provide additional cleanup behavior
	 */
	protected abstract void cleanupAfterTermination();
}
