package net.morimekta.providence.maven.util;

import net.morimekta.providence.reflect.util.ReflectionUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.DirectoryScanner;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static net.morimekta.providence.reflect.util.ReflectionUtils.isThriftFile;

/**
 * Input utility for providence maven plugins.
 */
public class ProvidenceInput {
    /**
     * Get the set of input files.
     * @param project The maven project
     * @param inputSelector The input-exclude selector.
     * @param defaultInputInclude The default input include (if not specified).
     * @param print_debug Print debug info to maven log.
     * @param log Maven logger instance.
     * @throws MojoExecutionException If parsing or checking input files failed.
     * @return The set of input files.
     */
    public static Set<File> getInputFiles(@Nonnull MavenProject project,
                                          IncludeExcludeFileSelector inputSelector,
                                          @Nonnull String defaultInputInclude,
                                          boolean print_debug,
                                          @Nonnull Log log) throws MojoExecutionException {
        try {
            TreeSet<File> inputs = new TreeSet<>();

            DirectoryScanner inputScanner = new DirectoryScanner();
            if (inputSelector != null) {
                if (inputSelector.getIncludes() != null &&
                        inputSelector.getIncludes().length > 0) {
                    if (print_debug) {
                        log.info("Specified includes:");
                        for (String include : inputSelector.getIncludes()) {
                            log.info("    -I " + include);
                        }
                    }
                    inputScanner.setIncludes(inputSelector.getIncludes());
                } else {
                    if (print_debug) {
                        log.info("Default includes: " + defaultInputInclude);
                    }
                    inputScanner.setIncludes(new String[]{defaultInputInclude});
                }

                if (inputSelector.getExcludes() != null &&
                        inputSelector.getExcludes().length > 0) {
                    log.info("Specified excludes:");
                    for (String exclude : inputSelector.getExcludes()) {
                        log.info("    -E " + exclude);
                    }
                    inputScanner.setExcludes(inputSelector.getExcludes());
                }
            } else {
                if (print_debug) {
                    log.info("Default input: " + defaultInputInclude);
                }
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
                File[] ls = new File(project.getBasedir(), dir).listFiles();
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
                String path = new File(project.getBasedir(), dir).getCanonicalPath();
                inputs.removeIf(f -> f.toString().startsWith(path + File.separator));
            }

            return inputs.stream()
                         .filter(ReflectionUtils::isThriftFile)
                         .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public static String format(Duration duration) {
        long h = duration.toHours();
        long m = duration.minusHours(h).toMinutes();
        if (h > 0) {
            return String.format("%d:%02d H", h, m);
        }
        long s = duration.minusHours(h).minusMinutes(m).getSeconds();
        if (m > 0) {
            return String.format("%d:%02d min", m, s);
        }
        long ms = duration.minusHours(h).minusMinutes(m).minusSeconds(s).toMillis();
        return String.format("%d.%02d s", s, ms / 10);
    }
}
