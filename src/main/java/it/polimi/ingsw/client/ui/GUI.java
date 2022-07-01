package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.rescale.RescaleUtils;
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

/**
 * Class {@code GUI} represent the main class for the JavaFX window
 */
public class GUI extends Application {
	
	public static final double referenceWidth = 1300;
	public static final double referenceHeight = 810;
	
	private static Scene scene;
	private static double stageWidth = referenceWidth, stageHeight = referenceHeight;
	private static double stageScale = 1.0;

	/**
	 * JavaFX method - Loads the {@code Stage} and sets the parameters
	 * @param stage (type Stage) {@code Stage} to load
	 * @throws IOException whenever the {@code Scene} fails to initialize from the main FXML file
	 */
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
			stageScale = RescaleUtils.getScaleValue(stageWidth, true);
			NotificationCenter.shared().post(NotificationName.JavaFXWindowDidResize, null, userInfo);
		});
		stage.heightProperty().addListener((observable, oldValue, newValue) -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put("oldHeight", oldValue);
			userInfo.put("newHeight", newValue);
			stageHeight = newValue.doubleValue();
			stageScale = RescaleUtils.getScaleValue(stageHeight, true);
			NotificationCenter.shared().post(NotificationName.JavaFXWindowDidResize, null, userInfo);
		});
	}

	/**
	 * Gets the window width
	 * @return (type double) returns the window width
	 */
	public static double getWindowWidth() {
		return stageWidth;
	}
	/**
	 * Gets the window height
	 * @return (type double) returns the window height
	 */
	public static double getWindowHeight() {
		return stageHeight;
	}

	/**
	 * Gets the {@code Stage's} scale
	 * @return (type double) returns the {@code Stage's} scale
	 */
	public static double getStageScale() {
		return stageScale;
	}

	/**
	 * Creates the observer fot the disconnection events
	 */
	public static void registerForDisconnectionEvents() {
		NotificationCenter.shared().addObserver(GUI.class, GUI::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidTimeoutNetwork, null);
		NotificationCenter.shared().addObserver(GUI.class, GUI::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
	}

	/**
	 * Loads the main FXML file adn sets the {@code Root}
	 * @param fxml (type String) {@code FXML's} file name
	 * @return (type FXMLLoader) returns the loader
	 * @throws IOException whenever the {@code Scene} fails to initialize from the main FXML file
	 */
	public static FXMLLoader setRoot(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
		scene.setRoot(fxmlLoader.load());
		return fxmlLoader;
	}

	/**
	 * Loads the {@code FXMLLoader}
	 * @param fxml (type String) {@code FXML's} file name
	 * @return (type Parent) returns the {@code Parent}
	 * @throws IOException whenever the {@code Scene} fails to initialize from the main FXML file
	 */
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	/**
	 * {@code NetworkTimeout} callback
	 * @param notification (type Notification) The network timeout notification
	 */
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

	/**
	 * Starts the GUI
	 * @param args (type String[])
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
