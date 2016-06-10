/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.morimekta.providence.maven.plugin;

import net.morimekta.providence.maven.util.ProvidenceAssemble;
import net.morimekta.providence.maven.util.ProvidenceInput;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import java.io.File;
import java.util.Set;

/**
 * Generate a zip file with the thrift source as an assembly step.
 * This assembly (using the 'pvd' or 'providence' classifier) can
 * then be dependent as for inclusing.
 *
 * TODO: Make the simple assembly work.
 * TODO: Add dependencies with 'pvd' or 'providence' assemblies to
 *       include path.
 *
 * mvn net.morimekta.providence:providence-maven-plugin:0.1.2-SNAPSHOT:assemble
 */
@Mojo(name = "assemble",
      defaultPhase = LifecyclePhase.PACKAGE,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class ProvidenceAssemblyMojo extends AbstractMojo {
    public static final String TYPE = "pom";
    public static final String CLASSIFIER = "providence";

    /**
     * Location of the output artifact.
     */
    @Parameter(defaultValue = "${project.build.directory}")
    private File outputDir = null;

    /**
     * Final name of the created assembly.
     */
    @Parameter(defaultValue = "${project.artifactId}-${project.version}-providence.zip")
    private String finalName = null;

    /**
     * Skips the assembly, install & deploy step.
     */
    @Parameter(defaultValue = "false")
    private boolean skipAssembly = false;

    /**
     * Files to include. By default will select all '.thrift' files in
     * 'src/main/providence/' and subdirectories.
     */
    @Parameter
    protected IncludeExcludeFileSelector files;

    // ---

    @Component
    protected MavenProjectHelper projectHelper = null;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project = null;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipAssembly) {
            return;
        }

        File target = new File(outputDir, finalName);
        if (!target.exists() || files != null) {
            Set<File> inputFiles = ProvidenceInput.getInputFiles(project, files, "src/main/providence/**/*.thrift");
            if (inputFiles.isEmpty()) {
                return;
            }

            ProvidenceAssemble.generateProvidencePackageFile(inputFiles, project.getArtifact(), target);
        }

        projectHelper.attachArtifact(project, TYPE, CLASSIFIER, target);
    }
}
