import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class StoreFileForm {
    private Window window;
    private JPanel mainPanel;
    private JButton selectFileButton;
    private JButton sendButton;
    private JLabel filePathLabel;
    private JLabel fileSizeLabel;

    public StoreFileForm() {

        final JFileChooser fileChooser = new JFileChooser();

        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = fileChooser.showOpenDialog(window);

                if (i == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    filePathLabel.setText(file.getAbsolutePath());

                    long fileSize = file.length();

                    if (fileSize < 1024) {
                        fileSizeLabel.setText(fileSize + " bytes");
                        sendButton.setEnabled(true);
                    } else if (fileSize < 1024 * 1024) {
                        fileSizeLabel.setText(String.format("%.2f", fileSize / 1024.0) + " KB");
                        sendButton.setEnabled(true);
                    } else if (fileSize < 8 * 1024 * 1024) {
                        fileSizeLabel.setText(String.format("%.2f", fileSize / 1024.0 / 1024.0) + " MB");
                        sendButton.setEnabled(true);
                    } else {
                        fileSizeLabel.setText(String.format("%.2f", fileSize / 1024.0 / 1024.0) + " MB (" + ((fileSize - 1) / 1024 / 1024 / 8 + 1) + " chunks)");
                        sendButton.setEnabled(true);
                    }

                    window.pack();
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final File file = fileChooser.getSelectedFile();

                filePathLabel.setText(file.getAbsolutePath());

                final long fileSize = file.length();

                if (fileSize < 8 * 1024 * 1024) {
                    RandomAccessFile randomAccessFile = null;
                    try {
                        randomAccessFile = new RandomAccessFile(file, "r");
                    } catch (FileNotFoundException e1) {
                        return;
                    }

                    final byte[] bytes = new byte[(int)fileSize];

                    try {
                        randomAccessFile.readFully(bytes);
                    } catch (IOException e1) {
                        JAdmin.log(e1.getMessage(), Color.RED);
                        return;
                    }

                    JAdmin.log("Read " + bytes.length + " bytes");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                synchronized (JAdmin.socketOutputStream) {
                                    JAdmin.log("Sending " + bytes.length + " bytes");

                                    JAdmin.send("STORE FILE");
                                    JAdmin.send("File-name: " + file.getName());
                                    JAdmin.send("File-size: " + fileSize);
                                    JAdmin.send("Chunks: " + 1);
                                    JAdmin.send("");

                                    JAdmin.send("Chunk-size: " + fileSize);
                                    JAdmin.send("");

                                    JAdmin.send(bytes);

                                    JAdmin.log("File " + file.getName() + " sent", Color.decode("#008000"));
                                }
                            } catch (IOException e1) {
                                JAdmin.log(e1.getMessage(), Color.RED);
                            }
                        }
                    }).start();
                } else {
                    RandomAccessFile randomAccessFile = null;
                    try {
                        randomAccessFile = new RandomAccessFile(file, "r");
                    } catch (FileNotFoundException e1) {
                        return;
                    }

                    long rest = fileSize;
                    int i = 0;

                    while (rest > 0) {
                        final int chunkSize = (int)Math.min(rest, 8 * 1024 * 1024);
                        rest -= chunkSize;

                        final byte[] bytes = new byte[chunkSize];

                        try {
                            randomAccessFile.readFully(bytes);
                        } catch (IOException e1) {
                            JAdmin.log(e1.getMessage(), Color.RED);
                            return;
                        }

                        JAdmin.log("Read " + bytes.length + " bytes");

                        final int j = i++;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    synchronized (JAdmin.socketOutputStream) {
                                        JAdmin.log("Sending " + bytes.length + " bytes");

                                        JAdmin.send("STORE FILE");
                                        JAdmin.send("File-name: " + file.getName() + "-" + j);
                                        JAdmin.send("File-size: " + chunkSize);
                                        JAdmin.send("Chunks: " + 1);
                                        JAdmin.send("");

                                        JAdmin.send("Chunk-size: " + chunkSize);
                                        JAdmin.send("");

                                        JAdmin.send(bytes);

                                        JAdmin.log("File " + file.getName() + "-" + j + " sent", Color.decode("#008000"));
                                    }
                                } catch (IOException e1) {
                                    JAdmin.log(e1.getMessage(), Color.RED);
                                }
                            }
                        }).start();
                    }

                    JAdmin.log("done", Color.GREEN);
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
