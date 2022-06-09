package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Objects;

public class CloudPane extends RescalableAnchorPane{
    private  int idx;
    private String address = null;
    private boolean selectable = false;
    private GridPane gridPane = new GridPane();

    public CloudPane(int idx, Notification notification) {
        this.idx = idx;
        switch (idx) {
            case 0 -> address = Objects.requireNonNull(getClass().getResource("images/clouds/cloud1.png")).toExternalForm();
            case 1 -> address = Objects.requireNonNull(getClass().getResource("images/clouds/cloud2.png")).toExternalForm();
            case 2 -> address = Objects.requireNonNull(getClass().getResource("images/clouds/cloud3.png")).toExternalForm();
            case 3 -> address = Objects.requireNonNull(getClass().getResource("images/clouds/cloud4.png")).toExternalForm();
        }

        setStyle("-fx-background-image: url(" + address + ");\n-fx-background-size: 100% 100%");

        clickOnCloud();
        Platform.runLater(() -> getChildren().add(gridPane));
        NotificationCenter.shared().addObserver(this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);

    }



    private void didReceiveTableState(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage message) {
            setStudentOnCloud(message);
        }
    }

    public void showSelection(boolean selectable) {
        this.selectable = selectable;
        if(selectable && gridPane.getChildren().size() == 0) {
            setStyle(getStyle() + ";\n-fx-background-color: rgba(80,255,80,0.4)");
        } else {
            setStyle("-fx-background-image: url(" + address + ");\n-fx-background-size: 100% 100%");
        }
    }

    private void setStudentOnCloud(TableStateMessage message) {
        int c = 0;
        int r = 0;
       Platform.runLater(() -> gridPane.getChildren().removeAll());
        for(Student s : Student.values()) {
            for (int i = 0; i < message.getManagedClouds().get(idx).getCount(s); i++) {
                AnchorPane student = GUIUtils.createImageViewWithImageNamed("images/students/" + s.getColor() +".png");
                int finalR = r;
                int finalC = c;
                Platform.runLater(() -> gridPane.add(student, finalC, finalR));
                c++;
                if (c > 1) {
                    c = 0;
                    r++;
                }
            }
        }
    }

    private void clickOnCloud() {
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (selectable) {
                gridPane.getChildren().clear();
                PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidChooseCloudIsland, -1, null, true, -1, -1, idx, -1, null);
                GameClient.shared().sendMessage(actionMessage);
            }
            event.consume();
        });
    }

    public void rescale(double scale) {
        setPrefSize(50 * scale, 50 * scale);
        gridPane.getRowConstraints().removeAll(gridPane.getRowConstraints());
        gridPane.getColumnConstraints().removeAll(gridPane.getColumnConstraints());
        gridPane.setPrefSize(33 * scale, 33 * scale);


        for (int i = 0; i < 2; i++) {
            ColumnConstraints col = new ColumnConstraints(33 * scale);
            gridPane.getColumnConstraints().add(col);
        }
        for (int i = 0; i < 2; i++) {
            RowConstraints row = new RowConstraints(33 * scale);
            gridPane.getRowConstraints().add(row);
        }
    }
}
