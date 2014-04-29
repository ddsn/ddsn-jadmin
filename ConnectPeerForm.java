import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Julian <julian@ddsn.org> on 28.04.2014.
 */
public class ConnectPeerForm {
    private Window window;
    private JPanel mainPanel;
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;

    public ConnectPeerForm() {

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JAdmin.send("CONNECT PEER");
                    JAdmin.send("Host: " + hostField.getText());
                    JAdmin.send("Port: " + portField.getText());
                    JAdmin.send("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
