import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class JAdminForm {
    private JPanel mainPanel;
    private JButton connectButton;
    private JList logList;
    private JButton disconnectButton;
    private JButton sendPingButton;
    private JButton storeFileButton;
    private JScrollPane logScrollPane;
    private JButton loadFileButton;
    private JButton connectPeerButton;
    private JTabbedPane tabbedPane1;
    private JButton refreshButton;
    private JLabel integratedLabel;
    private JLabel codeLabel;
    private JLabel blocksStoredLabel;
    private JLabel peersConnectedLabel;
    private JList outPeers;
    private JList inPeers;
    private JList queuedPeers;
    private JLabel capacityLabel;
    private JLabel idLabel;
    private SimpleDateFormat simpleDateFormat;

    public JAdminForm() {
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        inPeers.setModel(new DefaultListModel<Peer>());
        outPeers.setModel(new DefaultListModel<Peer>());
        queuedPeers.setModel(new DefaultListModel<Peer>());

        logList.setModel(new DefaultListModel<LogMessage>());
        logList.setCellRenderer(new ListCellRenderer<LogMessage>() {
            @Override
            public Component getListCellRendererComponent(JList list, LogMessage value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel(value.toString());
                label.setForeground(value.getColor());
                return label;
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectionForm connectionForm = new ConnectionForm();

                JDialog loginDialog = new JDialog(JAdmin.mainFrame, "Connect", JDialog.ModalityType.APPLICATION_MODAL);
                connectionForm.setWindow(loginDialog);
                loginDialog.add(connectionForm.getMainPanel());
                loginDialog.pack();
                loginDialog.setLocationRelativeTo(null);
                loginDialog.setResizable(false);
                loginDialog.setVisible(true);
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JAdmin.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                setConnected(false);
                JAdmin.log("connection closed on my behalf");
            }
        });
        sendPingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    synchronized (JAdmin.socketOutputStream) {
                        JAdmin.send("PING");
                        JAdmin.log("Send ping");
                    }
                } catch (IOException e1) {
                    JAdmin.log(e1.getMessage(), Color.RED);
                }
            }
        });
        storeFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StoreFileForm storeFileForm = new StoreFileForm();

                JDialog storeFileDialog = new JDialog(JAdmin.mainFrame, "Store File", JDialog.ModalityType.APPLICATION_MODAL);
                storeFileForm.setWindow(storeFileDialog);
                storeFileDialog.add(storeFileForm.getMainPanel());
                storeFileDialog.pack();
                storeFileDialog.setLocationRelativeTo(null);
                storeFileDialog.setResizable(false);
                storeFileDialog.setVisible(true);
            }
        });
        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadFileForm loadFileForm = new LoadFileForm();

                JDialog loadFileDialog = new JDialog(JAdmin.mainFrame, "Load File", JDialog.ModalityType.APPLICATION_MODAL);
                loadFileForm.setWindow(loadFileDialog);
                loadFileDialog.add(loadFileForm.getMainPanel());
                loadFileDialog.pack();
                loadFileDialog.setLocationRelativeTo(null);
                loadFileDialog.setResizable(false);
                loadFileDialog.setVisible(true);
            }
        });
        connectPeerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectPeerForm connectPeerForm = new ConnectPeerForm();

                JDialog connectPeerDialog = new JDialog(JAdmin.mainFrame, "Connect Peer", JDialog.ModalityType.APPLICATION_MODAL);
                connectPeerForm.setWindow(connectPeerDialog);
                connectPeerDialog.add(connectPeerForm.getMainPanel());
                connectPeerDialog.pack();
                connectPeerDialog.setLocationRelativeTo(null);
                connectPeerDialog.setResizable(false);
                connectPeerDialog.setVisible(true);
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JAdmin.send("PEER INFO");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addMessage(final String message, final Color color) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((DefaultListModel<LogMessage>) logList.getModel()).add(0, new LogMessage(simpleDateFormat.format(new Date()) + ": " + message, color));
            }
        });
    }

    public void setConnected(final boolean connected) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(!connected);
                disconnectButton.setEnabled(connected);
                sendPingButton.setEnabled(connected);
                storeFileButton.setEnabled(connected);
                loadFileButton.setEnabled(connected);
                connectPeerButton.setEnabled(connected);
                refreshButton.setEnabled(connected);
            }
        });
    }

    public void setConnecting(final boolean connecting) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!connecting) {
                    connectButton.setText("Connect");
                } else {
                    connectButton.setText("Connecting...");
                    connectButton.setEnabled(false);
                }
            }
        });
    }

    public void setPeerInfoId(final String id) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                idLabel.setText(id);
            }
        });
    }

    public void setPeerInfoIntegrated(final boolean integrated) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                integratedLabel.setText(integrated ? "yes" : "no");
            }
        });
    }

    public void setPeerInfoCode(final String code) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                codeLabel.setText(code);
            }
        });
    }

    public void setPeerInfoBlocks(final int blocks) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                blocksStoredLabel.setText(Integer.toString(blocks));
            }
        });
    }

    public void setPeerInfoCapacity(final int capacity) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                capacityLabel.setText(Integer.toString(capacity));
            }
        });
    }

    public void setPeerInfoPeers(final int peers) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                peersConnectedLabel.setText(Integer.toString(peers));
            }
        });
    }

    public void redrawPeers() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((DefaultListModel<Peer>)inPeers.getModel()).clear();
                ((DefaultListModel<Peer>)outPeers.getModel()).clear();
                ((DefaultListModel<Peer>)queuedPeers.getModel()).clear();
                for (Peer peer : JAdmin.peers.values()) {
                    if (peer.inLayer != -1 || peer.outLayer != -1) {
                        if (peer.inLayer != -1) {
                            ((DefaultListModel<Peer>)inPeers.getModel()).addElement(peer);
                        }
                        if (peer.outLayer != -1) {
                            ((DefaultListModel<Peer>)outPeers.getModel()).addElement(peer);
                        }
                    } else {
                        ((DefaultListModel<Peer>)queuedPeers.getModel()).addElement(peer);
                    }
                }
            }
        });
    }
}
