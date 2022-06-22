package it.polimi.ingsw.client.ui;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.HashMap;

public class chooseMNS extends RescalableAnchorPane{
    private int MNS;
    javafx.scene.control.TextField textField=new TextField();
    Button submit=new Button();

    public chooseMNS(){
        Platform.runLater(() -> {
        setPrefSize(400, 400);
        submit.setText("submit");
        getChildren().add(textField);
        getChildren().add(submit);
        setLayoutX(10 );
        setLayoutY(50 );

        });



    }



    public void setsubmit(ActionEvent actionEvent){
        int step=Integer.parseInt(textField.getText());
        if(step<0||step>2){
            Platform.runLater(() -> {
            Alert commanderror=new Alert(Alert.AlertType.ERROR);
            commanderror.setContentText("you must insert number between 0 and 2");
            commanderror.show();});
        }else{
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("MNStep", step);
        NotificationCenter.shared().post(NotificationName.JavaFXMNS, null, userInfo);}
        
    }
    
    @Override
    public void rescale(double scale) {
        setPrefSize(400*scale, 400*scale);
        setLayoutX(10 * scale);
        setLayoutY(50 * scale);
    }
}
