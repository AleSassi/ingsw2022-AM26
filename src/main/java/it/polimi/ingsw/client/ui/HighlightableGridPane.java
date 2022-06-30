package it.polimi.ingsw.client.ui;

import javafx.scene.layout.GridPane;

public class HighlightableGridPane extends GridPane {
	
	public void highlight(boolean highlighted) {
		setStyle(highlighted ? getStyle() + ";\n-fx-background-color: rgba(75,167,255,0.75)" : "");
	}
	
}
