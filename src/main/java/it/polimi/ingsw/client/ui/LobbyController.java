package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;


public class LobbyController {
    private int numberOfPlayersToFill = 4;
    private boolean shouldQuit = false;

    @FXML Label statusLabel;
    @FXML Label matchVariantLabel;



    public void run(MatchVariant matchVariant, int number) throws IOException {
        numberOfPlayersToFill  = number;
        Platform.runLater(() -> {
            matchVariantLabel.setText("Match Variant: " + matchVariant);
            statusLabel.setText("Waiting for " + numberOfPlayersToFill + " more players");
        });
        NotificationCenter.shared().addObserver(this::didReceiveNetworkTimeoutNotification, NotificationName.ClientDidTimeoutNetwork, null);
        NotificationCenter.shared().addObserver((notification) -> (new Thread(() -> otherPlayerLoggedInReceived(notification))).start(), NotificationName.ClientDidReceiveLoginResponse, null);
        NotificationCenter.shared().addObserver((notification) -> {
            shouldQuit = true;
            if (numberOfPlayersToFill > 0) {
                MatchTerminationMessage message = (MatchTerminationMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
                System.out.println(StringFormatter.formatWithColor("The server ended the match. Reason: \"" + message.getTerminationReason() + "\"", ANSIColors.Red));
                GameClient.shared().terminate();
            }
            synchronized (this) {
                notify();
            }
        }, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
        waitForPlayers();
    }

    private void waitForPlayers() throws IOException {
        synchronized (this) {
            while (!shouldQuit && numberOfPlayersToFill > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!shouldQuit) {
            Platform.runLater(() -> {
                statusLabel.setText("The lobby is full. You are now ready to start the game");
            });
            Thread.currentThread().interrupt();
            System.out.println("ci sono");


            MainBoardController mainBoardController = GUI.setRoot("scenes/mainBoard").getController();
            mainBoardController.run();
        }
    }

    private synchronized void otherPlayerLoggedInReceived(Notification notification) {
        LoginResponse message = (LoginResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());

        if (!message.getNickname().equals(Client.getNickname()) && message.isLoginAccepted()) {
            Platform.runLater(() -> {
                statusLabel.setText("AWaiting for " + numberOfPlayersToFill + " more players");
            });
            numberOfPlayersToFill = message.getNumberOfPlayersRemainingToFillLobby();
            if (numberOfPlayersToFill == 0) {
                Platform.runLater(() -> {
                    statusLabel.setText("The lobby is full. You are now ready to start the game");
                });
                notify();
            }
        }
    }

    protected void didReceiveNetworkTimeoutNotification(Notification notification) {
        shouldQuit = true;
        System.out.println(StringFormatter.formatWithColor("The Client encountered an error. Reason: Timeout. The network connection with the Server might have been interrupted, or the Server might be too busy to respond", ANSIColors.Red));
        GameClient.shared().terminate();
        synchronized (this) {
            notify();
        }
    }
}
