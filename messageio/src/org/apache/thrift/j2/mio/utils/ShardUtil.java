package org.apache.thrift.j2.mio.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 27.10.15
 */
public class ShardUtil {
    /**
     * From a file pattern like 'name@{shards}' calculates the pattern
     *
     * @param pattern
     * @return List of file prefixes
     */
    public static List<String> prefixes(String pattern) {
        Shard shard = new Shard(pattern);
        List<String> out = new LinkedList<>();
        for (int i = 0; i < shard.num; ++i) {
            out.add(String.format("%s-%04d", shard.name, i));
        }
        return out;
    }
}
