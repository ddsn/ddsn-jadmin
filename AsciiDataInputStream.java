import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Julian <julian@ddsn.org> on 26.04.2014.
 */
public class AsciiDataInputStream {

    private static final int MAX_STRING_LENGTH = 1024;

    private InputStream inputStream;

    public AsciiDataInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    String readLine() throws IOException {
        int character = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((character = inputStream.read()) != (int)'\n') {
            stringBuilder.append((char)character);
            if (stringBuilder.length() > MAX_STRING_LENGTH) {
                throw new IOException("String too long");
            }
        }
        return stringBuilder.toString();
    }

    int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

}
