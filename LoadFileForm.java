import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class LoadFileForm {
    private Window window;
    private JPanel mainPanel;
    private JButton chooseLocationButton;
    private JRadioButton blockCodeRadioButton;
    private JRadioButton fileNameRadioButton;
    private JTextField identField;
    private JButton loadButton;
    private JLabel filePathLabel;

    public LoadFileForm() {

        final JFileChooser fileChooser = new JFileChooser();

        chooseLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = fileChooser.showOpenDialog(window);

                if (i == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    filePathLabel.setText(file.getAbsolutePath());

                    long fileSize = file.length();

                    loadButton.setEnabled(true);

                    window.pack();
                }
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JAdmin.filePaths.put(identField.getText(), fileChooser.getSelectedFile().getAbsolutePath());

                JAdmin.log("Load file " + (blockCodeRadioButton.isSelected() ? " with block code " : " with name ") + identField.getText());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            synchronized (JAdmin.socketOutputStream) {
                                JAdmin.send("LOAD FILE");
                                if (blockCodeRadioButton.isSelected()) {
                                    JAdmin.send("Block-code: " + identField.getText());
                                } else {
                                    JAdmin.send("File-name: " + identField.getText());
                                }
                                JAdmin.send("");

                                JAdmin.socketOutputStream.flush();
                            }
                        } catch (IOException e1) {
                            JAdmin.log(e1.getMessage(), Color.RED);
                        }
                    }
                }).start();
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
