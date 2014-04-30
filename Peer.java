/**
 * Created by Julian <julian@ddsn.org> on 30.04.2014.
 */
public class Peer {
    public int inLayer;
    public int outLayer;
    public boolean connected;
    public String id;

    @Override
    public String toString() {
        return id + " " + (connected ? "(connected)" : "(disconnected)");
    }
}
