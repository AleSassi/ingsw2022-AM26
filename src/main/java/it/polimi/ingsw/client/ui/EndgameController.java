package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.VictoryMessage;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import javafx.scene.text.Font;

import java.util.Arrays;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
/*

        VictoryMessage victoryMessage = (VictoryMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        String[] winners=victoryMessage.getWinners();
        if(Arrays.asList(winners).contains(Client.getNickname())){
            EndgameController endgameController=GUI.setRoot("scenes/win").getController();
            endgameController.endgames(true, winners);}
        else{EndgameController endgameController=GUI.setRoot("scenes/win").getController();
            endgameController.endgames(true, winners);

 */

public class EndgameController implements JavaFXRescalable {
    @FXML
    javafx.scene.control.Label winlabel;
    @FXML
    AnchorPane winpane;
    @FXML
    ImageView resultimage;
    @FXML
    javafx.scene.control.Label winnerlabel;
    @FXML
    AnchorPane root;
    @FXML
    Button newgamebutton;
    @FXML
    Button quitbutton;


    public void endgames(Boolean Win, String[] Winner)throws IOException{
        NotificationCenter.shared().addObserver(this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);


        if(Win){
            Platform.runLater(() -> {
                winlabel.setText("VICTORY");
                winpane=GUIUtils.createImageViewWithImageNamed("images/King-Crown-PNG-Image.png");
                root.getChildren().add(winpane);
                winpane.setPrefSize(200,200);
                winpane.setLayoutY(37);
                double windowwidth=GUI.getWindowWidth();
                winlabel.setLayoutX(windowwidth);
                winpane.setLayoutX(windowwidth);

            });



        }else{
            Platform.runLater(() -> {
                winlabel.setText("    LOSE");
                winpane=GUIUtils.createImageViewWithImageNamed("images/Fail-Stamp.png");
                root.getChildren().add(winpane);
                winpane.setPrefSize(297,218);
                winpane.setLayoutX(534);
                winpane.setLayoutY(37);
                String winnames="";
                for(String s: Arrays.asList(Winner)) {
                    winnames= winnames +" "+ s;
                }
                winnames=winnames+" win the games";
                winnerlabel.setText(winnames);
            });


        }


    }

    @Override
    public void rescale(double scale) {
        winlabel.setFont(new Font("Avenir", 120 * scale));
        winlabel.relocate(364*scale,248*scale );
        winpane.setPrefSize(297*scale,218*scale );
        winpane.relocate(489*scale, 37*scale);
        winnerlabel.relocate(610*scale,427*scale );
        winnerlabel.setFont(new Font("Avenir", 18 * scale));
        newgamebutton.setLayoutX(364*scale);
        newgamebutton.setLayoutY(469*scale);
        newgamebutton.setPrefSize(542*scale, 128*scale);
        quitbutton.setPrefSize(542*scale, 128*scale);
        quitbutton.setLayoutX(364*scale);
        quitbutton.setLayoutY(651*scale);


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

    public void newgame(ActionEvent actionEvent)throws IOException {
        LoginController loginController = GUI.setRoot("scenes/LoginPage").getController();

    }
}
