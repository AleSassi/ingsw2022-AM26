package it.polimi.ingsw.client.ui.viewcontrollers;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.rescale.JavaFXRescalable;
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

public class EndgameController implements JavaFXRescalable {
 
	@FXML
	Label victoryLabel;
	@FXML
	AnchorPane victoryPane;
	@FXML
	ImageView resultimage;
	@FXML
	Label winnerLabel;
	@FXML
	AnchorPane root;
	@FXML
	Button newGameButton;
	@FXML
	Button quitButton;
	
	
	public void endGame(String[] winnerNicknames) throws IOException {
		NotificationCenter.shared().addObserver(this, this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
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
	
	private void didReceiveWindowDidResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue);
		}
	}
	
	public void quit(ActionEvent actionEvent) {
		GameClient.shared().terminate();
	}
	
	public void newGame(ActionEvent actionEvent) throws IOException {
		GUI.setRoot("scenes/LoginPage");
	}
}
