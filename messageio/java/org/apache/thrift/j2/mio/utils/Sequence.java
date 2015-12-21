package org.apache.thrift.j2.mio.utils;

import java.io.File;
import java.util.Iterator;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 27.10.15
 */
public class Sequence implements Iterator<File> {
    private final String prefix;
    private int mNextSequence;
    private int mSequence;

    public Sequence(String prefix) {
        this.prefix = prefix;
        this.mSequence = 0;
        this.mNextSequence = 0;
    }

    @Override
    public boolean hasNext() {
        return new File(String.format("%s-%05d", prefix, mNextSequence)).exists();
    }

    @Override
    public File next() {
        mSequence = mNextSequence++;
        return new File(String.format("%s-%05d", prefix, mSequence));
    }
}
