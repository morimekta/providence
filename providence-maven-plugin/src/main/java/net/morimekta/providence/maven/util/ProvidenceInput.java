package net.morimekta.providence.maven.util;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

/**
 * Input utility for providence maven plugins.
 */
public class ProvidenceInput {
    /**
     * Get the set of input files.
     * @param project The maven project
     * @param files The input-exclude selector.
     * @param defaultInputInclude The default input include (if not specified).
     * @return The set of input files.
     */
    public static Set<File> getInputFiles(MavenProject project,
                                          IncludeExcludeFileSelector files,
                                          String defaultInputInclude) {
        TreeSet<File> inputs = new TreeSet<>();

        DirectoryScanner inputScanner = new DirectoryScanner();
        if (files != null) {
            inputScanner.setIncludes(files.getIncludes());
            if (files.getExcludes() != null) {
                inputScanner.setExcludes(files.getExcludes());
            }
        } else {
            inputScanner.setIncludes(new String[]{defaultInputInclude});
        }

        inputScanner.setBasedir(project.getBasedir());
        inputScanner.scan();

        for (String file : inputScanner.getIncludedFiles()) {
            inputs.add(new File(project.getBasedir(), file));
        }

        return inputs;
    }
}
