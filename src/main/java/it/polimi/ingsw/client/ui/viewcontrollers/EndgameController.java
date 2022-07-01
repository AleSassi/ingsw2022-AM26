package it.polimi.ingsw.client.ui.viewcontrollers;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.rescale.JavaFXRescalable;
import it.polimi.ingsw.client.ui.rescale.RescalableController;
import it.polimi.ingsw.client.ui.rescale.RescaleUtils;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.control.Label;

import java.io.IOException;
/**
 * This Class display the last window of the game with the winners
 * @author Leonardo Betti
 */
public class EndgameController extends RescalableController {
 
	@FXML
	private Label victoryLabel;
	@FXML
	private AnchorPane victoryPane;
	@FXML
	private ImageView resultimage;
	@FXML
	private Label winnerLabel;
	@FXML
	private AnchorPane root;
	@FXML
	private Button newGameButton;
	@FXML
	private Button quitButton;

	/**
	 * this method displays a different page according to the result of the game for the actual player
	 * @param winnerNicknames (type list of String) nickname of winner players, if this parameter contains the nickname of actual player, winner page will be displayed otherwise the lose page will be displayed with the winner nicknames
	 * @throws IOException whenever there are problem with getting the fxml
	 */
	public void endGame(String[] winnerNicknames) throws IOException {
		boolean won = false;
		for (String winnerNickname: winnerNicknames) {
			if (winnerNickname.equals(Client.getNickname())) {
				won = true;
				break;
			}
		}
		if (won) {
			Platform.runLater(() -> {
				victoryLabel.setText("VICTORY!");
				root.getChildren().remove(victoryPane);
				victoryPane = GUIUtils.createImageViewWithImageNamed("images/King-Crown-PNG-Image.png");
				root.getChildren().add(victoryPane);
				victoryPane.setPrefSize(297, 218);
				victoryPane.setLayoutY(37);
				double windowWidth = GUI.getWindowWidth();
				victoryLabel.setLayoutX((windowWidth - victoryLabel.getWidth()) * 0.5);
				victoryPane.setLayoutX((windowWidth - victoryPane.getPrefWidth()) * 0.5);
			});
		} else {
			Platform.runLater(() -> {
				victoryLabel.setText("You Lost");
				victoryPane = GUIUtils.createImageViewWithImageNamed("images/Fail-Stamp.png");
				root.getChildren().add(victoryPane);
				victoryPane.setPrefSize(297, 218);
				victoryPane.setLayoutY(37);
				victoryLabel.setLayoutX((GUI.getWindowWidth() - victoryLabel.getWidth()) * 0.5);
				victoryPane.setLayoutX((GUI.getWindowWidth() - victoryPane.getPrefWidth()) * 0.5);
				StringBuilder winnames = new StringBuilder();
				for (String s : winnerNicknames) {
					if (s.equals(winnerNicknames[0])) {
						winnames.append(s);
					} else {
						winnames.append(", ").append(s);
					}
				}
				winnames.append(" won the game");
				winnerLabel.setText(winnames.toString());
				winnerLabel.setAlignment(Pos.CENTER);
				winnerLabel.setLayoutX((GUI.getWindowWidth() - winnerLabel.getPrefWidth()) * 0.5);
			});
		}
	}
	
	/**
	 * this method change the size of object inside the GUI interface according to the scale factor
	 * @param scale (type double) that represents the ratio from the old size og GUI window and the new dimension after resize
	 */
	@Override
	public void rescale(double scale) {
		victoryLabel.setFont(new Font("Avenir", 120 * scale));
		victoryPane.setPrefSize(297 * scale, 218 * scale);
		double windowWidth = GUI.getWindowWidth();
		victoryLabel.setPrefWidth(533 * scale);
		victoryLabel.setPrefHeight(147 * scale);
		victoryLabel.setLayoutX((windowWidth - victoryLabel.getPrefWidth()) * 0.5);
		victoryLabel.setLayoutY(248 * scale);
		victoryPane.setLayoutX((windowWidth - victoryPane.getPrefWidth()) * 0.5);
		victoryPane.setLayoutY(37 * scale);
		winnerLabel.setPrefSize(200 * scale, 20 * scale);
		winnerLabel.setFont(new Font("Avenir", 18 * scale));
		winnerLabel.setLayoutX((windowWidth - winnerLabel.getPrefWidth()) * 0.5);
		winnerLabel.setLayoutY(427 * scale);
		newGameButton.setPrefSize(542 * scale, 128 * scale);
		newGameButton.setLayoutX((windowWidth - newGameButton.getPrefWidth()) * 0.5);
		newGameButton.setLayoutY(469 * scale);
		quitButton.setPrefSize(542 * scale, 128 * scale);
		quitButton.setLayoutX((windowWidth - quitButton.getPrefWidth()) * 0.5);
		quitButton.setLayoutY(651 * scale);
	}
	
	/**
	 * this method mangae the click on the "quit button", closing the interface
	 * @param actionEvent(type action event) click of mouse that start the action
	 */
	public void quit(ActionEvent actionEvent) {
		GameClient.shared().terminate();
	}
	
	/**
	 * this method manages the click on the "new game button", display login page
	 * @param actionEvent(type action event) click of mouse that start the action
	 */
	public void newGame(ActionEvent actionEvent) throws IOException {
		GUI.setRoot("scenes/LoginPage");
		NotificationCenter.shared().removeObserver(this);
	}
	
	@Override
	protected void cleanupAfterTermination() {
	}
}
