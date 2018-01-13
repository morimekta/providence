package net.morimekta.providence.tools.common.formats;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageSpliterator;
import net.morimekta.providence.tools.common.ProvidenceTools;
import net.morimekta.providence.util.SimpleTypeRegistry;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FormatUtils {
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
                includes.put(ReflectionUtils.programNameFromPath(file.getName()), file.getAbsoluteFile().getCanonicalFile());
            }
        }
    }

    public static void collectConfigIncludes(File rc, Map<String, File> includes) throws IOException {
        if (!rc.exists()) {
            return;
        }

        rc = rc.getCanonicalFile();
        if (!rc.isFile()) {
            throw new ProvidenceConfigException("Rc file is not a file " + rc.getPath());
        }

        try {
            SimpleTypeRegistry registry = new SimpleTypeRegistry();
            registry.registerRecursively(ProvidenceTools.kDescriptor);
            ProvidenceConfig loader = new ProvidenceConfig(registry);
            ProvidenceTools config = loader.getConfig(rc);

            if (config.hasIncludes()) {
                File basePath = rc.getParentFile();
                if (config.hasIncludesBasePath()) {
                    String base = config.getIncludesBasePath();
                    if (base.charAt(0) == '~') {
                        base = System.getenv("HOME") + base.substring(1);
                    }
                    basePath = new File(base);
                    if (!basePath.exists() || !basePath.isDirectory()) {
                        throw new ProvidenceConfigException(
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

    public static Map<String, File> getIncludeMap(File rc, List<File> includes) throws IOException {
        Map<String, File> includeMap = new HashMap<>();
        if (includes.isEmpty()) {
            collectConfigIncludes(rc, includeMap);

            if (includeMap.isEmpty()) {
                throw new ArgumentException("No includes, use --include/-I or update ~/.pvdrc");
            }
        } else {
            for (File file : includes) {
                collectIncludes(file, includeMap);
            }
        }
        return includeMap;
    }

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> getInput(@Nonnull PMessageDescriptor<Message, Field> descriptor,
                             @Nonnull ConvertStream in,
                             boolean strict) throws IOException {
        Serializer serializer = in.format.createSerializer(strict);
        InputStream is = getInputStream(in);
        // Ensures it's closed at the end.
        return StreamSupport.stream(new MessageSpliterator<>(is, serializer, descriptor, is),
                                    false);
    }

    public static MessageReader getServiceInput(ConvertStream in, boolean strict) throws IOException {
        Serializer serializer = in.format.createSerializer(strict);
        return new IOMessageReader(getInputStream(in), serializer);
    }

    private static InputStream getInputStream(ConvertStream in) throws IOException {
        InputStream is;
        if (in.file != null) {
            File file = in.file.getCanonicalFile();
            if (!file.exists()) {
                throw new ArgumentException("%s does not exists", file.getAbsolutePath());
            }
            if (!file.isFile()) {
                throw new ArgumentException("%s is not a file", file.getAbsolutePath());
            }

            is = new FileInputStream(file);
        } else {
            is = new BufferedInputStream(System.in) {
                @Override
                public void close() {
                    // ignore
                }
            };
        }

        if (in.base64mime) {
            is = Base64.getMimeDecoder().wrap(is);
        } else if (in.base64) {
            is = Base64.getDecoder().wrap(is);
        }
        return new BufferedInputStream(is);
    }

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, ?, Integer> getOutput(ConvertStream out,
                                             boolean strict)
            throws IOException {
        Serializer serializer = out.format.createSerializer(strict);
        return MessageCollectors.toStream(getOutputStream(out), serializer, true);
    }

    public static MessageWriter getServiceOutput(ConvertStream out, boolean strict)
            throws IOException {
        Serializer serializer = out.format.createSerializer(strict);
        return new IOMessageWriter(getOutputStream(out), serializer);
    }

    private static OutputStream getOutputStream(ConvertStream out) throws IOException {
        OutputStream os;
        if (out.file != null) {
            File file = out.file.getCanonicalFile();
            if (file.exists() && !file.isFile()) {
                throw new ArgumentException("%s exists and is not a file.", file.getAbsolutePath());
            }
            Files.createDirectories(file.getParentFile().toPath());
            os = new BufferedOutputStream(new FileOutputStream(file));
        } else {
            os = new BufferedOutputStream(System.out) {
                @Override
                public void close() throws IOException {
                    flush();
                }
            };
        }

        if (out.base64mime) {
            os = Base64.getMimeEncoder()
                       .wrap(os);
        } else if (out.base64) {
            os = Base64.getEncoder()
                       .withoutPadding()
                       .wrap(os);
        }
        return os;
    }
}
