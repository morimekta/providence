package net.morimekta.providence.tools.common.formats;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.tools.common.ProvidenceTools;
import net.morimekta.providence.util.SimpleTypeRegistry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

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
    Collector<Message, ?, Integer> getOutput(Format defaultFormat,
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

            if (out.base64) {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStream os = Base64.getEncoder().wrap(new BufferedOutputStream(fos));
                return MessageCollectors.toStream(os, serializer);
            }
            return MessageCollectors.toFile(file, serializer);
        } else {
            if (out != null && out.base64) {
                return MessageCollectors.toStream(Base64.getEncoder().wrap(System.out), serializer);
            }
            return MessageCollectors.toStream(System.out, serializer);
        }
    }

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> getInput(PMessageDescriptor<Message, Field> descriptor,
                             ConvertStream in,
                             Format defaultFormat,
                             boolean strict) throws IOException {
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
                if (in.base64) {
                    FileInputStream fis = new FileInputStream(file);
                    InputStream is = Base64.getDecoder().wrap(new BufferedInputStream(fis));
                    return MessageStreams.stream(is, serializer, descriptor);
                }
                return MessageStreams.file(file, serializer, descriptor);
            } catch (IOException e) {
                throw new ArgumentException("Unable to read file %s", file.toString());
            }
        } else {
            InputStream is = new BufferedInputStream(System.in);
            if (in != null && in.base64) {
                is = Base64.getDecoder().wrap(is);
            }
            return MessageStreams.stream(is, serializer, descriptor);
        }
    }
}
