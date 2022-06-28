package it.polimi.ingsw.client.ui;

import javafx.scene.layout.AnchorPane;

public class FaderPane extends AnchorPane {
	
	public FaderPane() {
		setStyle("-fx-background-color: rgba(0,0,0,0.6)");
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
	}
}
