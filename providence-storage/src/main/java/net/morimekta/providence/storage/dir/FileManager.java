package net.morimekta.providence.storage.dir;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;

public interface FileManager<K> {
    File getFileFor(@Nonnull K key);

    File tmpFileFor(@Nonnull K key);

    Collection<K> initialKeySet();
}
