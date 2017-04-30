package net.morimekta.providence.streams;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Spliterator that reads messages from a stream.
 */
public class MessageSpliterator<Message extends PMessage<Message, Field>, Field extends PField>
        implements Spliterator<Message> {
    /**
     * Make a self-closing message spliterator.
     *
     * @param in Input stream to read from.
     * @param serializer Serializer to use.
     * @param descriptor The message descriptor of contained messages.
     */
    public MessageSpliterator(@Nonnull InputStream in,
                              @Nonnull Serializer serializer,
                              @Nonnull PMessageDescriptor<Message, Field> descriptor) {
        this(in, serializer, descriptor, in);
    }

    /**
     * Make a spliterator with specific closing function.
     *
     * @param in Input stream to read from.
     * @param serializer Serializer to use.
     * @param descriptor The message descriptor of contained messages.
     * @param closer The stream closer function. If null nothing is
     *               done when closing the spliterator.
     */
    public MessageSpliterator(@Nonnull InputStream in,
                              @Nonnull Serializer serializer,
                              @Nonnull PMessageDescriptor<Message, Field> descriptor,
                              @Nullable Closeable closer) {
        this.in = in;
        this.serializer = serializer;
        this.descriptor = descriptor;

        this.closer = closer;
        this.num = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Message> action) {
        Message message = read();
        if (message != null) {
            action.accept(message);
            return true;
        }
        return false;
    }

    /**
     * Normally we cannot split the stream.
     *
     * @return null (no split).
     */
    @Override
    public Spliterator<Message> trySplit() {
        return null;
    }

    /**
     * We mostly never know the number of messages in a message stream
     * until the last message has been read.
     *
     * @return Long.MAX_VALUE (not known).
     */
    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    /**
     * We mostly never know the number of messages in a message stream
     * until the last message has been read.
     *
     * @return -1 (not known).
     */
    @Override
    public long getExactSizeIfKnown() {
        return -1;
    }

    /**
     * Ordered, non-null and immutable.
     *
     * @return The characteristics.
     */
    @Override
    public int characteristics() {
        return ORDERED | NONNULL | IMMUTABLE;
    }

    /**
     * Messages are comparable.
     *
     * @return Comparable compareTo method.
     */
    @Override
    public Comparator<? super Message> getComparator() {
        return Comparable::compareTo;
    }

    // --- PRIVATE ---
    private final InputStream                        in;
    private final PMessageDescriptor<Message, Field> descriptor;
    private final Serializer                         serializer;

    private int       num;
    private Closeable closer;

    private Message read() {
        try {
            if (num > 0) {
                if (!serializer.binaryProtocol()) {
                    if (!IOUtils.skipUntil(in, MessageStreams.READABLE_ENTRY_SEP)) {
                        // no next entry found.
                        return close(null);
                    }
                }
            }
            // Try to check if there is a byte available. Since the
            // available() method ony checks for available non-blocking
            // reads, we need to actually try to read a byte.
            //
            // Sadly this means it's only available when marks are
            // supported.
            if (in.markSupported()) {
                in.mark(2);
                if (in.read() < 0) {
                    return close(null);
                }
                in.reset();
            }
            return serializer.deserialize(in, descriptor);
        } catch (IOException e) {
            close(e);
            throw new UncheckedIOException(e);
        } finally {
            ++num;
        }
    }

    private Message close(Exception cause) {
        if (closer != null) {
            try {
                closer.close();
            } catch (IOException e) {
                if (cause == null) {
                    throw new UncheckedIOException(e);
                } else {
                    cause.addSuppressed(e);
                }
            } finally {
                closer = null;
            }
        }
        return null;
    }
}
