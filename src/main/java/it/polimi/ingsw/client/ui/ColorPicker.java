package it.polimi.ingsw.client.ui;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.student.Student;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


import java.util.HashMap;

public class ColorPicker extends RescalableAnchorPane {
    private GridPane pickcolopane=new GridPane();
    private AnchorPane redpane=new AnchorPane();
    private AnchorPane yellowpane=new AnchorPane();
    private AnchorPane bluepane=new AnchorPane();
    private AnchorPane greenpane=new AnchorPane();
    private AnchorPane pinkpane=new AnchorPane();
    CharacterCardNetworkParamSet paramSet = null;



    private void ColorPicker(){
        Platform.runLater(() -> {
        setPrefSize(400, 400);
        pickcolopane.setPrefSize(400,400);
        redpane.setStyle("-fx-background-color: red");
        yellowpane.setStyle("-fx-background-color: yellow");
        bluepane.setStyle("-fx-background-color: blue");
        greenpane.setStyle("-fx-background-color: green");
        pinkpane.setStyle("-fx-background-color: pink");
        pickcolopane.setConstraints(redpane, 0, 0);
        pickcolopane.setConstraints(yellowpane, 1, 0);
        pickcolopane.setConstraints(bluepane, 0, 1);
        pickcolopane.setConstraints(greenpane, 1, 1);
        pickcolopane.setConstraints(pinkpane, 1, 2);
        setLayoutX(10 );
        setLayoutY(50 );
        });



    }




    public void choosecolor(){
        redpane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            //Action registered successfully - post notification
            HashMap<String, Object> userInfo = new HashMap<>();
            Student student=Student.RedDragon;
            userInfo.put("student", student);
            NotificationCenter.shared().post(NotificationName.JavaFXcolor, null, userInfo);
            event.consume();
        });

        yellowpane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            //Action registered successfully - post notification
            HashMap<String, Object> userInfo = new HashMap<>();
            Student student=Student.YellowElf;
            userInfo.put("student", student);
            NotificationCenter.shared().post(NotificationName.JavaFXcolor, null, userInfo);
            event.consume();
        });

        bluepane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            //Action registered successfully - post notification
            HashMap<String, Object> userInfo = new HashMap<>();
            Student student=Student.BlueUnicorn;
            userInfo.put("student", student);
            NotificationCenter.shared().post(NotificationName.JavaFXcolor, null, userInfo);
            event.consume();
        });

        greenpane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            //Action registered successfully - post notification
            HashMap<String, Object> userInfo = new HashMap<>();
            Student student=Student.GreenFrog;
            userInfo.put("student", student);
            NotificationCenter.shared().post(NotificationName.JavaFXcolor, null, userInfo);
            event.consume();
        });

        pinkpane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            //Action registered successfully - post notification
            HashMap<String, Object> userInfo = new HashMap<>();
            Student student=Student.PinkFair;
            userInfo.put("student", student);
            NotificationCenter.shared().post(NotificationName.JavaFXcolor, null, userInfo);
            event.consume();
        });





    }




    @Override
    public void rescale(double scale) {
        setPrefSize(400*scale, 400*scale);
        pickcolopane.setPrefSize(400*scale,400*scale);
        setLayoutX(10 * scale);
        setLayoutY(50 * scale);
    }
}
