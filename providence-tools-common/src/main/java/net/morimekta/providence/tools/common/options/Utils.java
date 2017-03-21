package net.morimekta.providence.tools.common.options;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * General utility methods.
 */
public class Utils {
    public static void collectIncludes(File dir, Map<String, File> includes) throws IOException {
        if (!dir.exists()) {
            throw new ArgumentException("No such include directory: " + dir.getCanonicalFile().getPath());
        }
        File[] files = dir.listFiles();
        if (!dir.isDirectory() || files == null) {
            throw new ArgumentException("Not a directory: " + dir.getCanonicalFile().getPath());
        }
        for (File file : files) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isFile() && file.canRead() && ReflectionUtils.isThriftFile(file.getName())) {
                includes.put(ReflectionUtils.programNameFromPath(file.getName()), file.getCanonicalFile());
            }
        }
    }

    public static String getVersionString() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = Utils.class.getResourceAsStream("/build.properties")) {
            properties.load(in);
        }
        return "v" + properties.getProperty("build.version");
    }

    public static HttpTransport createTransport() {
        return new NetHttpTransport();
    }

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, OutputStream, Integer> getOutput(Format defaultFormat,
                                                        ConvertStream out,
                                                        boolean strict)
            throws IOException {
        Format fmt = defaultFormat;
        File file = null;
        if (out != null) {
            fmt = out.format != null ? out.format : fmt;
            file = out.file;
        }

        final Serializer serializer = fmt.createSerializer(strict);
        if (file != null) {
            if (file.exists() && !file.isFile()) {
                throw new ArgumentException("%s exists and is not a file.", file.getAbsolutePath());
            }

            return MessageCollectors.toFile(file, serializer);
        } else {
            return MessageCollectors.toStream(System.out, serializer);
        }
    }

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> getInput(PMessageDescriptor<Message, Field> descriptor,
                             ConvertStream in,
                             Format defaultFormat,
                             boolean strict) throws ParseException, IOException {
        Format fmt = defaultFormat;
        File file = null;
        if (in != null) {
            fmt = in.format != null ? in.format : fmt;
            file = in.file;
        }

        Serializer serializer = fmt.createSerializer(strict);
        if (file != null) {
            try {
                return MessageStreams.file(file, serializer, descriptor);
            } catch (IOException e) {
                throw new ArgumentException("Unable to read file %s", file.getName());
            }
        } else {
            BufferedInputStream is = new BufferedInputStream(System.in);
            try {
                return MessageStreams.stream(is, serializer, descriptor);
            } catch (IOException e) {
                throw new ArgumentException("Broken pipe");
            }
        }
    }

}
