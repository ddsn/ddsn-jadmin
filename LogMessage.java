import java.awt.*;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class LogMessage {

    private String message;
    private Color color;

    public LogMessage(String message, Color color) {
        this.message = message;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return message;
    }

}
