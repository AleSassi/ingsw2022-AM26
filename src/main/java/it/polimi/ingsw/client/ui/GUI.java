package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.viewcontrollers.LoginController;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class GUI extends Application {
	
	private static Scene scene;
	private static double stageWidth, stageHeight;
	
	public static final double referenceWidth = 1300;
	public static final double referenceHeight = 810;
	
	@Override
	public void start(Stage stage) throws IOException {
		scene = new Scene(loadFXML("scenes/LoginPage"));
		
		stage.setTitle("Eriantys");
		stage.setScene(scene);
		stage.setResizable(true);
		stage.setWidth(referenceWidth);
		stage.setHeight(referenceHeight);
		LoginController loginController = GUI.setRoot("scenes/LoginPage").getController();
		stage.show();
		stageWidth = stage.getWidth();
		stageHeight = stage.getHeight();
		
		//Setup resize listeners
		stage.widthProperty().addListener((observable, oldValue, newValue) -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put("oldWidth", oldValue);
			userInfo.put("newWidth", newValue);
			stageWidth = newValue.doubleValue();
			NotificationCenter.shared().post(NotificationName.JavaFXWindowDidResize, null, userInfo);
		});
		stage.heightProperty().addListener((observable, oldValue, newValue) -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put("oldHeight", oldValue);
			userInfo.put("newHeight", newValue);
			stageHeight = newValue.doubleValue();
			NotificationCenter.shared().post(NotificationName.JavaFXWindowDidResize, null, userInfo);
		});
	}
	
	public static double getWindowWidth() {
		return stageWidth;
	}
	
	public static double getWindowHeight() {
		return stageHeight;
	}
	
	public static void registerForDisconnectionEvents() {
		NotificationCenter.shared().addObserver(GUI.class, GUI::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidTimeoutNetwork, null);
		NotificationCenter.shared().addObserver(GUI.class, GUI::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
	}
	
	public static FXMLLoader setRoot(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
		scene.setRoot(fxmlLoader.load());
		return fxmlLoader;
	}
	
	public static Parent getRoot() {
		return scene.getRoot();
	}
	
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}
	
	private static void didReceiveNetworkTimeoutNotification(Notification notification) {
		// Present an alert
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			// Go back to the login screen
			if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof MatchTerminationMessage message) {
				alert.setContentText("The Match was terminated by the server. Reason: " + message.getTerminationReason());
			} else {
				alert.setContentText("The Client encountered an error. Reason: Timeout. The network connection with the Server might have been interrupted, or the Server might be too busy to respond");
			}
			alert.showAndWait().ifPresentOrElse(button -> {
				try {
					GUI.setRoot("scenes/LoginPage");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, () -> {
				try {
					GUI.setRoot("scenes/LoginPage");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
		//Terminate the network session
		GameClient.shared().teardown();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
