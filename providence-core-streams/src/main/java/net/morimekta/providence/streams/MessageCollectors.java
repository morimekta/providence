package net.morimekta.providence.streams;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;

/**
 * @author Stein Eldar Johnsen
 * @since 28.01.16.
 */
public class MessageCollectors {
    public static <T extends PMessage<T>> Collector<T, OutputStream, Integer> toFile(File file,
                                                                                     PSerializer serializer) {
        final AtomicInteger result = new AtomicInteger(0);
        return Collector.of(() -> {
            try {
                return new BufferedOutputStream(new FileOutputStream(file));
            } catch(IOException e) {
                throw new UncheckedIOException("Unable to open " + file.getName(), e);
            }
        }, (outputStream, t) -> {
            try {
                if(result.get() > 0) {
                    result.addAndGet(maybeWriteBytes(outputStream, serializer.entrySeparator()));
                } else if (!serializer.streamInitiatorPartOfData()) {
                    result.addAndGet(maybeWriteBytes(outputStream, serializer.streamInitiator()));
                }
                result.addAndGet(serializer.serialize(outputStream, t));
            } catch(PSerializeException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Bad data", new IOException(e));
            } catch(IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Unable to write to " + file.getName(), e);
            }
        }, (a, b) -> null, (outputStream) -> {
            try {
                result.addAndGet(maybeWriteBytes(outputStream, serializer.streamTerminator()));
                outputStream.close();
            } catch(IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Unable to close " + file.getName(), e);
            }
            return result.get();
        });
    }

    public static <T extends PMessage<T>> Collector<T, OutputStream, Integer> toStream(OutputStream out,
                                                                                       PSerializer serializer) {
        final AtomicInteger result = new AtomicInteger(0);
        return Collector.of(() -> new BufferedOutputStream(out), (outputStream, t) -> {
            try {
                synchronized(outputStream) {
                    if(result.get() > 0) {
                        result.addAndGet(maybeWriteBytes(outputStream, serializer.entrySeparator()));
                    } else if (!serializer.streamInitiatorPartOfData()) {
                        result.addAndGet(maybeWriteBytes(outputStream, serializer.streamInitiator()));
                    }
                    result.addAndGet(serializer.serialize(outputStream, t));
                }
            } catch(PSerializeException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Bad data", new IOException(e));
            } catch(IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Broken pipe", e);
            }
        }, (a, b) -> null, (outputStream) -> {
            try {
                result.addAndGet(maybeWriteBytes(outputStream, serializer.streamTerminator()));
                outputStream.flush();
            } catch(IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Broken pipe", e);
            }
            return result.get();
        });
    }

    private static int maybeWriteBytes(OutputStream out, byte[] bytes) {
        if(bytes.length > 0) {
            try {
                out.write(bytes);
            } catch(IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return bytes.length;
    }
}
