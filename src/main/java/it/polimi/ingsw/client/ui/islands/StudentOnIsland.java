package it.polimi.ingsw.client.ui.islands;

import it.polimi.ingsw.client.ui.StudentPane;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class StudentOnIsland extends RescalableAnchorPane {
    
    private final StudentPane studentPane;
    private final Student color;
    private final Label studentLabel = new Label("0");
    private final int idx;

    public StudentOnIsland(Student s, int idx) {
        this.idx = idx;
        color = s;
        studentPane = new StudentPane(s);
        Platform.runLater(() -> {
            getChildren().addAll(studentLabel, studentPane);
            studentLabel.toFront();
            studentLabel.setVisible(false);
            studentPane.setVisible(false);
        });
        
        rescale(getCurrentScaleValue());

        NotificationCenter.shared().addObserver(this, this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
    }

    public void didReceiveTableState(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage message) {
            if(message.getIslands().size() - 1 >= idx) {
                int count = message.getIslands().get(idx).getNumberOfSameStudents(color);
                Platform.runLater(() -> {
                    studentLabel.setText("" + count);
                    if (count > 0) {
                        studentLabel.setVisible(true);
                        studentPane.setVisible(true);
                    }
                    else {
                        studentLabel.setVisible(false);
                        studentPane.setVisible(false);
                    }
                });
            }

        }
    }

    public void deleteStudent() {
        new Thread(() -> NotificationCenter.shared().removeObserver(this)).start();
    }

    public void rescale(double scale) {
        studentPane.setLayoutX(0);
        studentLabel.setLayoutX(0);
        studentPane.setPrefSize(30 * scale , 30 * scale);
        studentLabel.setPrefSize(30 * scale , 30 * scale);
        studentLabel.setFont(new Font("Avenir", 20 * scale));
    }
}
