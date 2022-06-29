package it.polimi.ingsw.client.ui.viewcontrollers;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class CleanableController implements Initializable {
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		NotificationCenter.shared().addObserver(this, this::startCleanupAfterTermination, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
		NotificationCenter.shared().addObserver(this, this::startCleanupAfterTermination, NotificationName.ClientDidTimeoutNetwork, null);
	}
	
	private void startCleanupAfterTermination(Notification notification) {
		new Thread(this::doCleanupAfterTermination).start();
	}
	
	private void doCleanupAfterTermination() {
		NotificationCenter.shared().removeObserver(this);
		cleanupAfterTermination();
	}
	
	protected abstract void cleanupAfterTermination();
}
