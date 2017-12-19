package net.morimekta.providence.tools.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * General utility methods.
 */
public class Utils {
    public static String getVersionString() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = Utils.class.getResourceAsStream("/version.properties")) {
            properties.load(in);
        }
        return "v" + properties.getProperty("build.version");
    }
}
