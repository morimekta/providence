package net.morimekta.providence.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Created by morimekta on 4/21/16.
 */
@Mojo(name = "compile",
      defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class GenerateSourcesMojo extends BaseGenerateSourcesMojo {
    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/providence")
    private File outputDir = null;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (executeInternal(outputDir, "src/main/providence/**/*.thrift")) {
            project.addCompileSourceRoot(outputDir.getPath());
        }
    }
}
