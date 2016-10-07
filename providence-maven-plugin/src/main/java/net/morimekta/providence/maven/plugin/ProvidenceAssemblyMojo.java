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

import net.morimekta.providence.maven.util.ProvidenceInput;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generate providence sources from thrift definitions.
 */
@Mojo(name = ProvidenceAssemblyMojo.GOAL,
      defaultPhase = LifecyclePhase.PACKAGE,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class ProvidenceAssemblyMojo extends AbstractMojo {
    public static final String GOAL = "assemble";
    public static final String TYPE = "zip";
    public static final String CLASSIFIER = "providence";

    /**
     * Skip the providence assembly step for this module.
     */
    @Parameter(defaultValue = "false")
    protected boolean skipAssembly = false;

    /**
     * Files to assemble. By default will select all '.thrift' files in
     * 'src/main/providence/' and subdirectories.
     */
    @Parameter(alias = "inputFiles")
    protected IncludeExcludeFileSelector files = null;

    /**
     * Classifier name to use for the artifact. By default it's 'providence', but it can
     * be replaced depending on needs (and if there are multiple artifacts to be exposed).
     */
    @Parameter(defaultValue = CLASSIFIER)
    protected String classifier = CLASSIFIER;

    // --- After here are internals, components and maven-set params.

    /**
     * Location of the output artifact.
     */
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    protected File buildDir = null;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project = null;

    @Component
    protected MavenProjectHelper projectHelper = null;

    public void execute() throws MojoFailureException {
        if (!skipAssembly) {
            Set<File> inputFiles = ProvidenceInput.getInputFiles(project, files, "src/main/providence/**/*.thrift");
            if (inputFiles.isEmpty()) {
                getLog().info("No providence files, skipping assembly");
                return;
            }

            File target = new File(buildDir, String.format("%s-%s-%s.%s",
                                                           project.getArtifactId(),
                                                           project.getVersion(),
                                                           classifier,
                                                           TYPE));

            Artifact artifact = project.getArtifact();
            String internalPath = Strings.join("/",
                                               artifact.getGroupId().replaceAll("[.]", "/"),
                                               artifact.getArtifactId());
            int numFiles = 0;

            try (FileOutputStream fos = new FileOutputStream(target, false);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ZipOutputStream zos = new ZipOutputStream(bos)) {

                String tmpPath = "";
                for (String dir : internalPath.split("[/]")) {
                    if (!dir.isEmpty()) {
                        tmpPath = tmpPath + dir + File.separator;
                        zos.putNextEntry(new ZipEntry(tmpPath));
                        zos.closeEntry();
                    }
                }

                for (File file : inputFiles) {
                    Path thriftFilePath = Paths.get(file.getAbsolutePath());

                    ZipEntry entry = new ZipEntry(internalPath + File.separator + file.getName());
                    entry.setSize(Files.size(thriftFilePath));
                    entry.setLastModifiedTime(Files.getLastModifiedTime(thriftFilePath));

                    try (FileInputStream fis = new FileInputStream(file);
                         BufferedInputStream bis = new BufferedInputStream(fis)) {
                        zos.putNextEntry(entry);
                        IOUtils.copy(bis, zos);
                    }
                    ++numFiles;
                }

                zos.flush();
            } catch (IOException ie) {
                throw new MojoFailureException("Unable to write providence assembly: " + ie.getMessage(), ie);
            }

            getLog().info("Created assembly: " + target.getName() + " with " + numFiles + " files.");
            projectHelper.attachArtifact(project, TYPE, classifier, target);
        }
    }
}
