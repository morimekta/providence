package net.morimekta.providence.tools.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * General utility methods.
 */
public class Utils {
    private transient volatile static String versionString = null;

    public static String getVersionString() {
        if (versionString == null) {
            try {
                Properties properties = new Properties();
                try (InputStream in = Utils.class.getResourceAsStream("/version.properties")) {
                    properties.load(in);
                }
                versionString = "v" + properties.getProperty("build.version");
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
        }
        return versionString;
    }
}
