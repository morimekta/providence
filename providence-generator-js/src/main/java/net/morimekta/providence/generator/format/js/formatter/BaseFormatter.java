package net.morimekta.providence.generator.format.js.formatter;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.generator.format.js.utils.JSUtils;
import net.morimekta.providence.reflect.contained.CMessageDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.util.Strings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseFormatter {
    final JSOptions options;

    BaseFormatter(JSOptions options) {
        this.options = options;
    }

    public String getFileName(CProgram program) {
        return program.getProgramName() + ".js";
    }

    public String getFilePath(CProgram program) {
        if (program.getNamespaceForLanguage("js") != null) {
            String[] parts = program.getNamespaceForLanguage("js").split("[.]");
            if (options.type_script || options.node_js) {
                return String.join("-", parts);
            }
            return String.join(File.separator, parts);
        } else {
            return program.getProgramName();
        }
    }

    String getClassReference(CService service) {
        return "_" + service.getProgramName() + "." + Strings.camelCase("", service.getName());
    }
    String getClassReference(PDeclaredDescriptor declaredDescriptor) {
        return JSUtils.getClassReference(declaredDescriptor);
    }
    String getClassReference(CMessageDescriptor descriptor) {
        return getClassReference((PDeclaredDescriptor) descriptor);
    }

    /**
     * Get the node-like include / require path.
     *
     * @param toBeIncluded The program to include.
     * @param context The current (context) program.
     * @return The package require / include path.
     */
    String getNodePackageInclude(CProgram toBeIncluded, CProgram context) {
        Path relativeTo = Paths.get(File.separator + getFilePath(context));
        Path includedPath = Paths.get(File.separator + getFilePath(toBeIncluded), toBeIncluded.getProgramName());
        String relative = relativeTo.relativize(includedPath).toString();
        if (!"/".equals(File.separator)) {
            // in case file separator is not '/'
            relative = String.join("/", relative.split(File.separator));
        }
        if (!relative.startsWith(".")) {
            relative = "./" + relative;
        }

        return relative;
    }

}
