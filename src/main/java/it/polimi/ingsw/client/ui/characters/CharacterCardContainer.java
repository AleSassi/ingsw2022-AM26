package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.viewcontrollers.MainBoardController;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;

public class CharacterCardContainer extends RescalableAnchorPane {
 
	private final GridPane characterCardGridPane = new GridPane();
    private final ArrayList<CharacterCardPane> cards = new ArrayList<>();
	
	public CharacterCardContainer(TableStateMessage initialMessage) {
		for (int i = 0; i < 3; i++) {
			CharacterCardPane card = CharacterCardPaneBuilder.buildPane(i, initialMessage.getPlayableCharacterCards().get(i));
			cards.add(card);
		}
		AnchorPane.setBottomAnchor(characterCardGridPane, 0.0);
		AnchorPane.setTopAnchor(characterCardGridPane, 0.0);
		AnchorPane.setRightAnchor(characterCardGridPane, 0.0);
		AnchorPane.setLeftAnchor(characterCardGridPane, 0.0);
        Platform.runLater(() -> {
			int i = 0;
			for (CharacterCardPane card: cards) {
                GridPane.setRowIndex(card, 0);
                GridPane.setColumnIndex(card, i);
				i += 1;
			}
	        characterCardGridPane.getChildren().addAll(cards);
	        getChildren().add(characterCardGridPane);
		});
		rescale(1);
	}
	
	public void setParentController(MainBoardController controller) {
		for (CharacterCardPane cardPane: cards) {
			cardPane.setParentController(controller);
		}
	}
	
	@Override
	public void rescale(double scale) {
        setLayoutX(GUI.getWindowWidth() - 390 * scale);
        setLayoutY(GUI.getWindowHeight() - 180 * scale);
		setPrefSize(390 * scale, 180 * scale);
		characterCardGridPane.getRowConstraints().removeAll(characterCardGridPane.getRowConstraints());
		characterCardGridPane.getColumnConstraints().removeAll(characterCardGridPane.getColumnConstraints());
        for (int i = 0; i < 1; i++) {
            RowConstraints row = new RowConstraints(136 * scale);
            characterCardGridPane.getRowConstraints().add(row);
        }
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints(90 * scale);
            characterCardGridPane.getColumnConstraints().add(column);
        }
		characterCardGridPane.setVgap(0);
		characterCardGridPane.setHgap(8 * scale);
	}
}
