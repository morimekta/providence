package net.morimekta.providence.mio.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 27.10.15
 */
public class ShardUtil {
    /**
     * Cheks if a name pattern is for a sharded file-set.
     *
     * @param pattern File name pattern.
     * @return True if the file pattern matches sharding.
     */
    public static boolean shardedName(String pattern) {
        try {
            if (pattern.contains("@")) {
                Shard shard = new Shard(pattern);
                return shard.num > 0;
            }
            return false;
        } catch (RuntimeException rte) {
            return false;
        }
    }

    public static String shardName(String name, int shardNumber) {
        return String.format("%s-%04d", name, shardNumber);
    }

    /**
     * From a file pattern like 'name@{shards}' calculates the pattern
     *
     * @return List of file prefixes
     */
    public static List<String> prefixes(String pattern) {
        Shard shard = new Shard(pattern);
        List<String> out = new LinkedList<>();
        for (int i = 0; i < shard.num; ++i) {
            out.add(shardName(shard.name, i));
        }
        return out;
    }

    public static boolean sequencedName(String file) {
        return file.endsWith("-00000");
    }

    public static String sequencePrefix(String file) {
        if (sequencedName(file)) {
            return file.substring(0, file.length() - 6);
        }
        throw new IllegalArgumentException("File name is not a sequence " + file);
    }
}
