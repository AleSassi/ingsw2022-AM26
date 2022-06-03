package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
		NotificationCenter.shared().addObserver(this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
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
				victoryPane = GUIUtils.createImageViewWithImageNamed("images/King-Crown-PNG-Image.png");
				root.getChildren().add(victoryPane);
				victoryPane.setPrefSize(200, 200);
				victoryPane.setLayoutY(37);
				double windowWidth = GUI.getWindowWidth();
				victoryLabel.setLayoutX(windowWidth);
				victoryPane.setLayoutX(windowWidth);
			});
		} else {
			Platform.runLater(() -> {
				victoryLabel.setText("    LOSE");
				victoryPane = GUIUtils.createImageViewWithImageNamed("images/Fail-Stamp.png");
				root.getChildren().add(victoryPane);
				victoryPane.setPrefSize(297, 218);
				victoryPane.setLayoutX(534);
				victoryPane.setLayoutY(37);
				StringBuilder winnames = new StringBuilder();
				for (String s : winnerNicknames) {
					winnames.append(" ").append(s);
				}
				winnames.append(" win the games");
				winnerLabel.setText(winnames.toString());
			});
		}
	}
	
	@Override
	public void rescale(double scale) {
		victoryLabel.setFont(new Font("Avenir", 120 * scale));
		victoryLabel.relocate(364 * scale, 248 * scale);
		victoryPane.setPrefSize(297 * scale, 218 * scale);
		victoryPane.relocate(489 * scale, 37 * scale);
		winnerLabel.relocate(610 * scale, 427 * scale);
		winnerLabel.setFont(new Font("Avenir", 18 * scale));
		newGameButton.setLayoutX(364 * scale);
		newGameButton.setLayoutY(469 * scale);
		newGameButton.setPrefSize(542 * scale, 128 * scale);
		quitButton.setPrefSize(542 * scale, 128 * scale);
		quitButton.setLayoutX(364 * scale);
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
		LoginController loginController = GUI.setRoot("scenes/LoginPage").getController();
	}
}
