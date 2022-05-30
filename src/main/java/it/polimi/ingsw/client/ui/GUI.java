package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class GUI extends Application {
	
	private static Scene scene;
	private static Stage mainStage;
	
	protected static final double referenceWidth = 1300;
	protected static final double referenceHeight = 810;
	
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
		mainStage = stage;
		
		//Setup resize listeners
		stage.widthProperty().addListener((observable, oldValue, newValue) -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put("oldWidth", oldValue);
			userInfo.put("newWidth", newValue);
			NotificationCenter.shared().post(NotificationName.JavaFXWindowDidResize, null, userInfo);
		});
		stage.heightProperty().addListener((observable, oldValue, newValue) -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put("oldHeight", oldValue);
			userInfo.put("newHeight", newValue);
			NotificationCenter.shared().post(NotificationName.JavaFXWindowDidResize, null, userInfo);
		});
	}
	
	public static double getWindowWidth() {
		return mainStage.getWidth();
	}
	
	public static double getWindowHeight() {
		return mainStage.getHeight();
	}
	
	static FXMLLoader setRoot(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
		scene.setRoot(fxmlLoader.load());
		return fxmlLoader;
	}
	
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
