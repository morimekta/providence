package net.morimekta.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Stein Eldar Johnsen
 * @since 28.01.16.
 */
public class IOUtils {

    public static boolean skipUntil(InputStream in, byte[] separator) throws IOException {
        if(separator.length > 0) {
            if(separator.length == 1) { return skipUntil(in, separator[0]); }
            if(separator.length > 4) { return skipUntil(in, separator, new byte[separator.length]); }

            int mask = separator.length == 2 ? 0xffff : separator.length == 3 ? 0xffffff : 0xffffffff;
            int sep = (separator[0] % 0x100) << 8 | separator[1];
            if(separator.length > 2) { sep = sep << 8 | separator[2]; }
            if(separator.length > 3) { sep = sep << 8 | separator[3]; }
            int r;
            int tmp = 0;
            while((r = in.read()) >= 0) {
                tmp = tmp << 8 | r;
                if((tmp & mask) == sep) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean skipUntil(InputStream in, byte[] separator, byte[] buffer) throws IOException {
        int r;
        while((r = in.read()) >= 0) {
            System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
            buffer[buffer.length - 1] = (byte) r;
            if(Arrays.equals(separator, buffer)) { return true; }
        }
        return false;
    }

    public static boolean skipUntil(InputStream in, byte separator) throws IOException {
        int r;
        while((r = in.read()) >= 0) {
            if(((byte) r) == separator) { return true; }
        }
        return false;
    }
}
