package net.morimekta.providence.maven.util;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import static net.morimekta.providence.reflect.util.ReflectionUtils.isThriftFile;

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
                                          String defaultInputInclude) throws MojoExecutionException {
        try {
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

            // Include all files included specifically.
            for (String file : inputScanner.getIncludedFiles()) {
                inputs.add(new File(project.getBasedir(), file).getCanonicalFile());
            }
            // Include all thrift files in included directories.
            for (String dir : inputScanner.getIncludedDirectories()) {
                File[] ls = new File(dir).listFiles();
                if (ls != null) {
                    for (File file : ls) {
                        if (isThriftFile(file.toString())) {
                            inputs.add(file.getCanonicalFile());
                        }
                    }
                }
            }
            // exclude all files excluded specifically.
            for (String file : inputScanner.getExcludedFiles()) {
                inputs.remove(new File(project.getBasedir(), file).getCanonicalFile());
            }
            // Exclude all files in excluded directories (and subdirectories).
            for (String dir : inputScanner.getExcludedDirectories()) {
                String path = new File(dir).getCanonicalPath();
                inputs.removeIf(f -> f.toString().startsWith(path));
            }

            return inputs;
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
