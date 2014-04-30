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

                if (message.equals("HELLO")) {
                    JAdmin.getJAdminForm().setConnected(true);
                } else if (message.equals("LOAD FILE")) {
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
                    if (fileSize == 0) {
                        JAdmin.log("Could not load " + blockCode, Color.RED);
                        continue;
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
                } else if (message.equals("PEER INFO")) {
                    while (!(message = asciiDataInputStream.readLine()).equals("")) {
                        String[] fieldValue = message.split(": ");

                        if (fieldValue[0].equals("Peer-id")) {
                            JAdmin.getJAdminForm().setPeerInfoId(fieldValue[1].substring(0, 6));
                        } else if (fieldValue[0].equals("Peer-code")) {
                            if (fieldValue.length == 1) {
                                JAdmin.getJAdminForm().setPeerInfoCode("empty");
                            } else {
                                JAdmin.getJAdminForm().setPeerInfoCode(fieldValue[1]);
                            }
                        } else if (fieldValue[0].equals("Integrated")) {
                            JAdmin.getJAdminForm().setPeerInfoIntegrated(fieldValue[1].equals("yes"));
                        } else if (fieldValue[0].equals("Blocks")) {
                            JAdmin.getJAdminForm().setPeerInfoBlocks(Integer.parseInt(fieldValue[1]));
                        } else if (fieldValue[0].equals("Capacity")) {
                            JAdmin.getJAdminForm().setPeerInfoCapacity(Integer.parseInt(fieldValue[1]));
                        } else if (fieldValue[0].equals("Peers")) {
                            JAdmin.getJAdminForm().setPeerInfoPeers(Integer.parseInt(fieldValue[1]));
                        }
                    }
                    JAdmin.peers.clear();
                    Peer peer = null;
                    while (true) {
                        if ((message = asciiDataInputStream.readLine()).equals("")) {
                            if (peer == null) {
                                break;
                            }
                            if ((message = asciiDataInputStream.readLine()).equals("")) {
                                break;
                            } else {
                                peer = new Peer();
                            }
                        } else if (peer == null) {
                            peer = new Peer();
                        }
                        String[] fieldValue = message.split(": ");
                        if (fieldValue[0].equals("Peer-id")) {
                            peer.id = fieldValue[1];
                            JAdmin.peers.put(fieldValue[1], peer);
                        } else if (fieldValue[0].equals("Connected")) {
                            peer.connected = fieldValue[1].equals("yes");
                        } else if (fieldValue[0].equals("In-layer")) {
                            peer.inLayer = Integer.parseInt(fieldValue[1]);
                        } else if (fieldValue[0].equals("Out-layer")) {
                            peer.outLayer = Integer.parseInt(fieldValue[1]);
                        }
                    }
                    JAdmin.getJAdminForm().redrawPeers();
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
