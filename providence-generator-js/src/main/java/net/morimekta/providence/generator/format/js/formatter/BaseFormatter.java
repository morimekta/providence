package net.morimekta.providence.generator.format.js.formatter;

import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.reflect.contained.CProgram;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class BaseFormatter {
    final JSOptions options;

    BaseFormatter(JSOptions options) {
        this.options = options;
    }

    public String getFileName(CProgram program) {
        if (options.type_script) {
            return program.getProgramName() + ".ts";
        }
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
