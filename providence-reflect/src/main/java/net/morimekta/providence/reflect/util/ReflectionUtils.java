package net.morimekta.providence.reflect.util;

/**
 * Simple utility for type checking and matching.
 */
public class ReflectionUtils {
    public static boolean isThriftFile(String filePath) {
        // This is in case windows has default upper-cased the file name.
        filePath = filePath.toLowerCase();

        return filePath.endsWith(".providence") ||
               filePath.endsWith(".thrift") ||
               filePath.endsWith(".thr") ||
               filePath.endsWith(".pvd");
    }

    public static String programNameFromPath(String filePath) {
        String lowerCased = filePath.toLowerCase();
        if (lowerCased.endsWith(".providence")) {
            filePath = filePath.substring(0, filePath.length() - 11);
        } else if (lowerCased.endsWith(".thrift")) {
            filePath = filePath.substring(0, filePath.length() - 7);
        } else if (lowerCased.endsWith(".thr") || lowerCased.endsWith(".pvd")) {
            filePath = filePath.substring(0, filePath.length() - 4);
        }
        if (filePath.contains("/")) {
            filePath = filePath.replaceAll(".*[/]", "");
        }

        return filePath.replaceAll("[-.]", "_");
    }
}
