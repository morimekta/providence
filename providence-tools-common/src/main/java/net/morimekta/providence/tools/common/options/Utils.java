package net.morimekta.providence.tools.common.options;

import net.morimekta.config.ConfigException;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.tools.common.ProvidenceTools;
import net.morimekta.providence.util.TypeRegistry;

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

    public static void collectConfigIncludes(File rc, Map<String, File> includes) throws IOException {
        if (!rc.exists()) {
            return;
        }

        rc = rc.getCanonicalFile();
        if (!rc.isFile()) {
            throw new ConfigException("Rc file is not a file " + rc.getPath());
        }

        try {
            TypeRegistry registry = new TypeRegistry();
            registry.registerRecursively(ProvidenceTools.kDescriptor);
            ProvidenceConfig loader = new ProvidenceConfig(registry);
            ProvidenceTools config = loader.getConfig(rc, ProvidenceTools.kDescriptor);

            if (config.hasIncludes()) {
                File basePath = rc.getParentFile();
                if (config.hasIncludesBasePath()) {
                    String base = config.getIncludesBasePath();
                    if (base.charAt(0) == '~') {
                        base = System.getenv("HOME") + base.substring(1);
                    }
                    basePath = new File(base);
                    if (!basePath.exists() || !basePath.isDirectory()) {
                        throw new ConfigException(
                                "Includes Base path in " + rc.getPath() + " is not a directory: " + basePath);
                    }
                }
                for (String path : config.getIncludes()) {
                    File include = new File(basePath, path);
                    collectIncludes(include, includes);
                }
            }
        } catch (SerializerException e) {
            System.err.println("Config error: " + e.getMessage());
            System.err.println(e.asString());
            System.err.println();
            throw new ArgumentException(e, "Exception when parsing " + rc.getCanonicalFile());
        }
    }

    public static String getVersionString() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = Utils.class.getResourceAsStream("/version.properties")) {
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
            file = file.getCanonicalFile();

            if (file.exists() && !file.isFile()) {
                throw new ArgumentException("%s exists and is not a file.", file.toString());
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
            file = file.getCanonicalFile();
            if (!file.exists() || !file.isFile()) {
                throw new ArgumentException("%s does not exists or is not a file.", file.toString());
            }

            try {
                return MessageStreams.file(file, serializer, descriptor);
            } catch (IOException e) {
                throw new ArgumentException("Unable to read file %s", file.toString());
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
