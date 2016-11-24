package net.morimekta.providence.reflect.util;

/**
 * Simple utility for type checking and matching.
 */
public class ReflectionUtils {
    public static boolean isThriftFile(String name) {
        return name.endsWith(".providence") ||
               name.endsWith(".thrift") ||
               name.endsWith(".thr") ||
               name.endsWith(".pvd");
    }

    public static String packageFromName(String name) {
        if (name.endsWith(".providence")) {
            name = name.substring(0, name.length() - 11);
        } else if (name.endsWith(".thrift")) {
            name = name.substring(0, name.length() - 7);
        } else if (name.endsWith(".thr") || name.endsWith(".pvd")) {
            name = name.substring(0, name.length() - 4);
        }

        return name.replaceAll("[-.]", "_");
    }
}
