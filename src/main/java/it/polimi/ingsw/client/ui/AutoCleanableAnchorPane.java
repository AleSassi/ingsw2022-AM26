package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.NotificationCenter;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * An Anchor Pane that removes itself as subscriber for notifications
 */
public class AutoCleanableAnchorPane extends AnchorPane {
	
	public void cleanupBeforeDisappear() {
		NotificationCenter.shared().removeObserver(this);
		for (Node child: getChildren()) {
			if (child instanceof AutoCleanableAnchorPane pane) {
				pane.cleanupBeforeDisappear();
			}
		}
	}

}
