package cappuccino.helium.ui.mainview;

import cappuccino.helium.network.Message;
import cappuccino.helium.network.Server;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class MainViewController implements Initializable {

    @FXML
    private TextField serverIP;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField userHandle;
    @FXML
    private TextField serverPassword;
    @FXML
    private TextArea chatWindow;
    @FXML
    private TextField messageField;
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;

    private Server server;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatWindow.textProperty().addListener((ObservableValue<?> observable, Object oldValue, Object newValue) -> {
            chatWindow.setScrollTop(Double.MAX_VALUE);
        });
    }

    @FXML
    public void connectToServer(ActionEvent event) {
        System.out.println("Connect to server");

        String ip = serverIP.getText();
        int port = -1;
        try {
            port = Integer.parseInt(serverPort.getText());
        } catch (NumberFormatException ex) {

        }

        // connect to server...
        String handle = userHandle.getText();
        String password = serverPassword.getText();

        server = new Server(ip, port);
        server.setHandle(handle);
        server.setPassword(password);

        server.getMessages().addListener((ListChangeListener.Change<? extends Message> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Message m : c.getAddedSubList()) {
                        System.out.println("GUI is handling message: " + m);
                        if (m.getSenderHandle().equals(server.getHandle())) {
                            continue; // skip this message because I sent it
                        }
                        chatWindow.appendText(m.getSenderHandle() + ": " + m.getContent() + "\n");
                        chatWindow.setScrollTop(1000);
                    }
                }
            }
        });

        // send handle and password if necessary...
        chatWindow.setDisable(false);
        messageField.setDisable(false);
        sendButton.setDisable(false);
        connectButton.setDisable(true);
        serverIP.setDisable(true);
        serverPort.setDisable(true);
        userHandle.setDisable(true);
        serverPassword.setDisable(true);
    }

    @FXML
    public void sendMessage(ActionEvent event) {
        String message = messageField.getText();
        messageField.setText("");

        System.out.println("Send message: " + message);
        server.sendMessage(new Message(Message.MessageType.NEW_MESSAGE, Message.ContentType.TEXT, server.getHandle(), message));

        chatWindow.appendText("Me: " + message + "\n");
        chatWindow.setScrollTop(1000);
    }
    
    public void closeConnections() {
        server.closeConnection();
    }

}
