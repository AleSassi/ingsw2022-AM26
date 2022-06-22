package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;

public class CharactercardContainer extends RescalableAnchorPane{
    Label price1=new Label();
    Label price2=new Label();
    Label price3=new Label();
    GridPane chargrid=new GridPane();
    int coin;
    int activecharacter;
    ArrayList<CharacterCardPane> cards=new ArrayList<>();


    public CharactercardContainer(){
        Platform.runLater(() -> {
            setLayoutX(1300-375);
            setLayoutY(810-220);
            chargrid.setPrefSize(370,218);
        for(int i=0;i<=2;i++){
            CharacterCardPane card=new CharacterCardPane();
            cards.add(i, card);
            card.setPrefSize(120,200);
            chargrid.setConstraints(card, i, 0); }
            });

        Platform.runLater(() -> {

        getChildren().add(chargrid);
        chargrid.setConstraints(price1, 0, 1);
        chargrid.setConstraints(price2, 1, 1);
        chargrid.setConstraints(price3, 2, 1);
        });
    }

    public void setplayer(PlayerStateMessage message){

        this.coin=message.getAvailableCoins();
        if(message.getActiveCharacterCardIdx()!=null){
        activecharacter=message.getActiveCharacterCardIdx();}
        int i=0;
        for(CharacterCardPane cardPane:cards) {
            cardPane.setplayerinfo(coin, activecharacter, i);
        }


    }


    public void settable(TableStateMessage message)  {
        Platform.runLater(() -> {
            price1.setText(String.valueOf(message.getPlayableCharacterCards().get(1).getTotalPrice()));
            price2.setText(String.valueOf(message.getPlayableCharacterCards().get(2).getTotalPrice()));
            price3.setText(String.valueOf(message.getPlayableCharacterCards().get(3).getTotalPrice()));
        });
    List<CharacterCardBean> cardlist=message.getPlayableCharacterCards();
        for(int i=0;i<cardlist.size();i++){
            cards.get(i).setcard(cardlist.get(i));


        }

 }

    public void didReceiveStudentMovementStart(Notification notification) {
        for(int i=0;i<=2;i++){
            cards.get(i).didReceiveStudentMovementStart(notification);}
    }
    @Override
    public void rescale(double scale) {
        Platform.runLater(() -> {
            setLayoutX((1300-375)*scale);
            setLayoutY((810-220)*scale);
            chargrid.setPrefSize(370*scale,218*scale);});
    for(CharacterCardPane cardPane:cards){
        cardPane.rescale(scale);
    }
}
}
