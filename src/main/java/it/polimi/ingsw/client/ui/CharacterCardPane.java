package it.polimi.ingsw.client.ui;
import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.utils.ui.GUIUtils;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.HashMap;

public class CharacterCardPane extends RescalableAnchorPane{
    int coin;
   int activecharacter;
    CharacterCardBean char1;
    int cardidx;
    AnchorPane charpane1;
    StudentCollection hostedstuddent;
    GridPane StudentGrid1;
    int effectactive=0;
    Student stud1=null, stud2=null;
    int movement=0;


    public void setplayerinfo(int playercoin, int isactive, int cardidx){
        this.coin=playercoin;
        activecharacter=isactive;
        this.cardidx=cardidx;

    }


    public void setcard(CharacterCardBean cha){
        this.char1=cha;
        int i;
        int x=0,y=0;
        hostedstuddent=char1.getHostedStudents();
        this.cardidx=cardidx;
        switch(char1.getCharacter()) {
            case Abbot:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front.jpg");
                for(Student student: Student.values()){
                    int count=hostedstuddent.getCount(student);
                    for(i=0;i>count;i++){
                        AnchorPane studentpane=GUIUtils.createStudentButton(student, new StudentDropTarget[]{StudentDropTarget.ToDiningRoom, StudentDropTarget.ToIsland, StudentDropTarget.ToCharacterCard});
                        StudentGrid1.setConstraints(studentpane, x, y);
                        if(x==1){
                            y++;
                            x=0;

                        }else{
                            x++;
                        }
                    }
                }
                charpane1.getChildren().add(StudentGrid1);
                StudentGrid1.setDisable(true);
                break;
            case CheeseMan:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front12.jpg");
                break;
            case Ambassador:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front2.jpg");
                break;
            case Magician:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front3.jpg");
                break;
            case Herbalist:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front4.jpg");
                break;
            case Centaurus:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front5.jpg");
                break;
            case Swordsman:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front7.jpg");
                break;
            case Mushroom:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front8.jpg");
                break;
            case Circus:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front6.jpg");
                for(Student student: Student.values()){
                    int count=hostedstuddent.getCount(student);
                    for(i=0;i>count;i++){
                        AnchorPane studentpane=GUIUtils.createStudentButton(student, new StudentDropTarget[]{StudentDropTarget.ToEntrance});
                        StudentGrid1.setConstraints(studentpane, x, y);
                        if(x==1){
                            y++;
                            x=0;

                        }else{
                            x++;
                        }
                    }
                }
                charpane1.getChildren().add(StudentGrid1);
                StudentGrid1.setDisable(true);
                break;
            case Musician:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front9.jpg");
                break;
            case Queen:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front10.jpg");
                for(Student student: Student.values()){
                    int count=hostedstuddent.getCount(student);
                    for(i=0;i>count;i++){
                        AnchorPane studentpane=GUIUtils.createStudentButton(student, new StudentDropTarget[]{StudentDropTarget.ToDiningRoom});
                        StudentGrid1.setConstraints(studentpane, x, y);
                        if(x==1){
                            y++;
                            x=0;

                        }else{
                            x++;
                        }
                    }
                }
                charpane1.getChildren().add(StudentGrid1);
                StudentGrid1.setDisable(true);
                break;
            case Thief:
                charpane1= GUIUtils.createImageViewWithImageNamed("images/Character/CarteTOT_front11.jpg");
                break;
        }

    }


    private void playcharachter(CharacterCardBean character){
        Alert commanderror=new Alert(Alert.AlertType.ERROR);
        CharacterCardNetworkParamSet paramSet = null;
        if(!(activecharacter>=0&&activecharacter<=2)){
            if(character.getTotalPrice()>coin){
                Platform.runLater(() -> {
                Alert error=new Alert(Alert.AlertType.ERROR);
                error.setContentText("no sufficient coin");
                error.show();
                });
            }else{
                Platform.runLater(() -> {
                Alert error=new Alert(Alert.AlertType.ERROR);
                error.setContentText("you played a character card");
                error.show();
                });
                PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPurchaseCharacterCard, -1, null, false, -1, -1, -1, this.cardidx, null);
                GameClient.shared().sendMessage(actionMessage);
            }
        }else{
            if(activecharacter!=cardidx){
                Platform.runLater(() -> {
                    Alert error=new Alert(Alert.AlertType.ERROR);
                    error.setContentText("another card is active");
                    error.show();
                });
            }else{
            HashMap<String, Object> UserInfo = new HashMap<>();
            switch (character.getCharacter()) {
                case Abbot:
                    UserInfo.put("card", Character.Abbot);
                    NotificationCenter.shared().post(NotificationName.JavaFXCickOnIsland, null, UserInfo);
                    StudentGrid1.setDisable(false);
                    break;
                case CheeseMan:
                    commanderror.setContentText("The CheeseMan card cannot be played, since its effect is passive");
                    commanderror.show();
                    break;
                case Ambassador:
                    UserInfo.put("card", Character.Ambassador);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    break;
                case Magician:
                    UserInfo.put("card", Character.Magician);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    break;
                case Herbalist:
                    UserInfo.put("card", Character.Herbalist);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    break;
                case Centaurus:
                    commanderror.setContentText("The Centaurus card cannot be played, since its effect is passive");
                    commanderror.show();
                    break;
                case Swordsman:
                    commanderror.setContentText("The Swordman card cannot be played, since its effect is passive");
                    commanderror.show();
                    break;
                case Mushroom:
                    UserInfo.put("card", Character.Mushroom);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    break;
                case Circus:
                    UserInfo.put("card", Character.Circus);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    StudentGrid1.setDisable(false);
                    effectactive = 2;
                    break;
                case Musician:
                    UserInfo.put("card", Character.Musician);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    StudentGrid1.setDisable(false);
                    effectactive = 2;
                    break;
                case Queen:
                    UserInfo.put("card", Character.Queen);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    break;
                case Thief:
                    UserInfo.put("card", Character.Thief);
                    NotificationCenter.shared().post(NotificationName.JavaFXPlayedAssistantCard, null, UserInfo);
                    break;


            }
        }
        }




    }

    public void didReceiveStudentMovementStart(Notification notification){
        CharacterCardNetworkParamSet paramSet = null;
        if(stud1==null&&effectactive!=0){
        stud1 = (Student) notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue());}
        else if((stud1!=null&&effectactive==1)){
            stud2 = (Student) notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue());
            paramSet = new CharacterCardNetworkParamSet(stud1, stud2, false, -1, -1, -1, null);
            PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayCharacterCard, -1, null, false, -1, -1, -1, -1, paramSet);
            GameClient.shared().sendMessage(actionMessage);
            movement++;
            if(movement==3){
                movement=0;
                effectactive=0;
            }
        }else if((stud1!=null&&effectactive==2)){
            stud2 = (Student) notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue());
            paramSet = new CharacterCardNetworkParamSet(stud1, stud2, false, -1, -1, -1, null);
            PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayCharacterCard, -1, null, false, -1, -1, -1, -1, paramSet);
            GameClient.shared().sendMessage(actionMessage);
            if(movement==3){
                movement=0;
                effectactive=0;
            }
        }


    }






    @Override
    public void rescale(double scale) {
       setPrefSize(120*scale,200*scale);
       StudentGrid1.setPrefSize(120*scale,200*scale);

    }
}
