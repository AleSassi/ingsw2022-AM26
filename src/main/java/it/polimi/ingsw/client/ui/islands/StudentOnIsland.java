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

/**
 * Class {@code StudentOnIsland} represent the graphic assets of the {@link it.polimi.ingsw.server.model.student.Student Students} on the {@link it.polimi.ingsw.server.model.student.Island Island}
 */
public class StudentOnIsland extends RescalableAnchorPane {
    
    private final StudentPane studentPane;
    private final Student color;
    private final Label studentLabel = new Label("0");
    private final int idx;
    private final double defaultLayoutX = 0;
    private final double defaultLayoutY = 0;
    private final double defaultFontSize = 20;

    /**
     * Constructor creates a new {@link it.polimi.ingsw.server.model.student.Student Student}
     * @param s (type Student) type of {@code Student}
     * @param idx (type int) {@link it.polimi.ingsw.server.model.student.Island Island's} index
     */
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
    
    @Override
    public double getUnscaledWidth() {
        return 30;
    }
    
    @Override
    public double getUnscaledHeight() {
        return 30;
    }
    
    /**
     * Callback for the {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage TableStateMessage} received
     * @param notification (type Notification) The notification for the message received
     */
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

    /**
     * Deletes this {@code StudentOnIsland}
     */
    public void deleteStudent() {
        new Thread(() -> NotificationCenter.shared().removeObserver(this)).start();
    }
    
    @Override
    public void rescale(double scale) {
        studentPane.setLayoutX(defaultLayoutX);
        studentLabel.setLayoutX(defaultLayoutY);
        studentPane.setPrefSize(getUnscaledWidth() * scale , getUnscaledHeight() * scale);
        studentLabel.setPrefSize(getUnscaledWidth() * scale , getUnscaledHeight() * scale);
        studentLabel.setFont(new Font("Avenir", defaultFontSize * scale));
    }
}
