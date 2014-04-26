import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class ConnectionForm {
    private Window window;
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;
    private JPanel mainPanel;

    public ConnectionForm() {
        final ConnectionForm that = this;

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JAdmin.getJAdminForm().setConnecting(true);

                connectButton.setEnabled(false);
                connectButton.setText("Connecting...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JAdmin.socket = new Socket(hostField.getText(), Integer.parseInt(portField.getText()));
                            JAdmin.socketOutputStream = JAdmin.socket.getOutputStream();
                            JAdmin.socketInputStream = JAdmin.socket.getInputStream();
                            JAdmin.log("Connected to " + hostField.getText() + ":" + portField.getText(), Color.decode("#008000"));

                            new Thread(new MessageHandler()).start();

                            that.window.dispose();
                            JAdmin.getJAdminForm().setConnecting(false);
                            JAdmin.getJAdminForm().setConnected(true);
                        } catch (NumberFormatException e1) {
                            connectButton.setText("Connect");
                            connectButton.setEnabled(true);
                            JAdmin.getJAdminForm().setConnecting(false);
                        } catch (IOException e1) {
                            JAdmin.log("connecting to " + hostField.getText() + ":" + portField.getText() + " failed", Color.RED);
                            connectButton.setText("Connect");
                            connectButton.setEnabled(true);
                            JAdmin.getJAdminForm().setConnecting(false);
                        }
                    }
                }).start();
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
