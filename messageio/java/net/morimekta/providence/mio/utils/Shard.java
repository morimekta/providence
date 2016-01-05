package net.morimekta.providence.mio.utils;

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

    /**
     * Generate a sequence for given shard number.
     * @param shard Shard number to get file sequence for.
     * @return The sequence iterator.
     */
    public Sequence sequence(int shard) {
        if (shard < 0 || shard >= num) {
            throw new IllegalArgumentException("Shard ID outside range [0.." + (num - 1));
        }
        return new Sequence(String.format("%s-%04d", name, shard));
    }
}
