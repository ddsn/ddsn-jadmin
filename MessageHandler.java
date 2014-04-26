import java.awt.*;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class MessageHandler implements Runnable {

    @Override
    public void run() {
        AsciiDataInputStream asciiDataInputStream = new AsciiDataInputStream(JAdmin.socketInputStream);
        while (!JAdmin.socket.isClosed()) {
            try {
                String message = asciiDataInputStream.readLine();

                if (message.equals("FILE LOADED")) {
                    int fileSize = 0;
                    String blockCode = null;
                    while (!(message = asciiDataInputStream.readLine()).equals("")) {
                        JAdmin.log(message);
                        String[] fieldValue = message.split(": ");

                        if (fieldValue[0].equals("File-size")) {
                            fileSize = Integer.parseInt(fieldValue[1]);
                        } else if (fieldValue[0].equals("Block-code")) {
                            blockCode = fieldValue[1];
                        }
                    }
                    byte[] data = new byte[fileSize];
                    int off = 0;
                    while ((off += asciiDataInputStream.read(data, off, fileSize - off)) < fileSize) {
                    }
                    JAdmin.log("Read " + fileSize + " bytes");

                    DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(JAdmin.filePaths.get(blockCode)));
                    dataOutputStream.write(data);
                    dataOutputStream.close();

                    JAdmin.log("Wrote " + fileSize + " bytes to file " + JAdmin.filePaths.get(blockCode), Color.decode("#008000"));
                } else {
                    JAdmin.log("Receive: " + message);
                }
            } catch (IOException e1) {
                JAdmin.log(e1.getMessage(), Color.RED);
                try {
                    JAdmin.socket.close();
                } catch (IOException e2) {
                }
                JAdmin.getJAdminForm().setConnected(false);
                break;
            }
        }
    }
}
