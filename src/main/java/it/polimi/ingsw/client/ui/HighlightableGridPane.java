package it.polimi.ingsw.client.ui;

import javafx.scene.layout.GridPane;

/**
 * A grid pane that can be highlighted to tell the user it can be clicked on
 */
public class HighlightableGridPane extends GridPane {
	
	/**
	 * Highlights or de-highlights the pane
	 * @param highlighted Whether it must be highlighted or not
	 */
	public void highlight(boolean highlighted) {
		setStyle(highlighted ? getStyle() + ";\n-fx-background-color: rgba(75,167,255,0.75)" : "");
	}
	
}
