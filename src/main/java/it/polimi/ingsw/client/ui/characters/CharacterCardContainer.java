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

/**
 * A rescalable container for holding a set of 3 {@link it.polimi.ingsw.client.ui.characters.CharacterCardPane CharacterCardPane}s
 */
public class CharacterCardContainer extends RescalableAnchorPane {
 
	private final GridPane characterCardGridPane = new GridPane();
    private final ArrayList<CharacterCardPane> cards = new ArrayList<>();
	private final double defaultVgap = 0;
	private final double defaultHgap = 8;
	/**
	 * Constructor creates the {@code CharacterCardContainer} from the {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage TableStateMessage}
	 * @param initialMessage (type TableStateMessage) The message for the table state, used to get the list of available character cards
	 */
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
		rescale(getCurrentScaleValue());
	}
	
	@Override
	public double getUnscaledWidth() {
		return 390;
	}
	
	@Override
	public double getUnscaledHeight() {
		return 180;
	}
	
	/**
	 * Sets the parent {@code Controller}
	 * @param controller (type MainBoardController) the parent controller
	 */
	public void setParentController(MainBoardController controller) {
		for (CharacterCardPane cardPane: cards) {
			cardPane.setParentController(controller);
		}
	}
	
	@Override
	public void rescale(double scale) {
        setLayoutX(GUI.getWindowWidth() - getUnscaledWidth() * scale);
        setLayoutY(GUI.getWindowHeight() - getUnscaledHeight() * scale);
		setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
		characterCardGridPane.getRowConstraints().removeAll(characterCardGridPane.getRowConstraints());
		characterCardGridPane.getColumnConstraints().removeAll(characterCardGridPane.getColumnConstraints());
        for (int i = 0; i < 1; i++) {
            RowConstraints row = new RowConstraints(cards.get(0).getUnscaledHeight() * scale);
            characterCardGridPane.getRowConstraints().add(row);
        }
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints(cards.get(0).getUnscaledWidth() * scale);
            characterCardGridPane.getColumnConstraints().add(column);
        }
		characterCardGridPane.setVgap(defaultVgap);
		characterCardGridPane.setHgap(defaultHgap * scale);
	}
}
