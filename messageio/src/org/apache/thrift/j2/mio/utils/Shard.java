package org.apache.thrift.j2.mio.utils;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 27.10.15
 */
public class Shard {
    public final String name;
    public final int num;

    public Shard(String pattern) {
        String[] parts = pattern.split("[@]");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Illegal shard file pattern :" + pattern);
        }
        name = parts[0];
        num = Integer.parseUnsignedInt(parts[1]);
        if (num < 1) {
            throw new IllegalArgumentException("Illegal shard count " + num);
        }
    }
}
