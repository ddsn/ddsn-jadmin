import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class JAdmin {

    public static Socket socket;
    public static InputStream socketInputStream;
    public static OutputStream socketOutputStream;
    public static final String version = "v0.0.1";
    public static JFrame mainFrame;
    private static JAdminForm jAdminForm;
    public static HashMap<String, String> filePaths = new HashMap<String, String>();

    public static void main(String[] args) {

        jAdminForm = new JAdminForm();

        mainFrame = new JFrame("DDSN JAdmin " + version);
        mainFrame.setContentPane(jAdminForm.getMainPanel());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        log("Welcome to JAdmin " + version);

    }

    public static JAdminForm getJAdminForm() {
        return jAdminForm;
    }

    public static void log(String message) {
        jAdminForm.addMessage(message, Color.BLACK);
    }

    public static void log(final String message, final Color color) {
        jAdminForm.addMessage(message, color);
    }

    public static void send(String message) throws IOException {
        JAdmin.socketOutputStream.write((message + "\n").getBytes(Charset.forName("ASCII")));
    }

    public static void send(byte[] bytes) throws IOException {
        JAdmin.socketOutputStream.write(bytes);
    }

    public static void flush() throws IOException {
        JAdmin.socketOutputStream.flush();
    }

}
