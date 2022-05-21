package it.polimi.ingsw.client.ui;


import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;


public class LoginController {
    ObservableList<String> wizardChoices = FXCollections.observableArrayList("wizard1", "wizard2", "wizard3", "wizard4");
    ObservableList<String> gameChoices = FXCollections.observableArrayList("simple", "expert");
    ObservableList<String> numofplayerchoice = FXCollections.observableArrayList("2", "3", "4");

    int i = 0;


    private String username;
    private MatchVariant type;


        public void run () {
            NotificationCenter.shared().addObserver((notification) -> (new Thread(() -> {
                try {
                    otherPlayerLoggedInReceived(notification);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            })).start(), NotificationName.ClientDidReceiveLoginResponse, GameClient.shared());
        }



    @FXML ChoiceBox<String> gamebox;
    @FXML ChoiceBox<String> wizardbox;
    @FXML ChoiceBox<String> numberofplayer;
    @FXML TextField nicknamebox;
    @FXML TextField playerbox;
    @FXML Label statuslabel;
    @FXML Pane pane;




    @FXML
    private void initialize() {
        gamebox.setValue("simple");
        gamebox.setItems(gameChoices);
        wizardbox.setValue("wizard1");
        wizardbox.setItems(wizardChoices);
        numberofplayer.setValue("2");
        numberofplayer.setItems(numofplayerchoice);

    }

    private synchronized void otherPlayerLoggedInReceived(Notification notification) throws IOException, InterruptedException {
        LoginResponse message = (LoginResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        boolean messageIsForPlayer = message.getNickname().equals(Client.getNickname());

        if (message.getNickname().equals(Client.getNickname()) && message.isLoginAccepted()) {
            //if the message is for the player and the login Accepted go to lobby
            LobbyController lobbyController = GUI.setRoot("scenes/lobby").getController();
            lobbyController.run(type, message.getNumberOfPlayersRemainingToFillLobby());

        } else if(message.getNickname().equals(Client.getNickname()) && !message.isLoginAccepted()){
            Platform.runLater(() -> {
                statuslabel.setText(message.getRejectionReason());
            });

        }
    }


    public void SendNickname(ActionEvent actionEvent) {
        username = nicknamebox.getText();
        String number = numberofplayer.getSelectionModel().getSelectedItem();
        int Number = Integer.parseInt(number);
        String selectedgame = gamebox.getSelectionModel().getSelectedItem();
        String selectedwizard = wizardbox.getSelectionModel().getSelectedItem();
        Wizard wiz;


        if (selectedgame.equals("simple")) {
            type = MatchVariant.BasicRuleSet;
        } else {
            type = MatchVariant.ExpertRuleSet;
        }

        if (selectedwizard.equals("wizard1")) {
            wiz = Wizard.Wizard1;
        } else if (selectedwizard.equals("wizard2")) {
            wiz = Wizard.Wizard2;
        } else if (selectedwizard.equals("wizard3")) {
            wiz = Wizard.Wizard3;
        } else {
            wiz = Wizard.Wizard4;
        }
        Client.setNickname(username);
        NetworkMessage loginMessage = new LoginMessage(username, Number, type, wiz);
        //this.run();
        loginMessage.serialize();
        GameClient.shared().sendMessage(loginMessage);
    }
}
