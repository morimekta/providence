/*
 * Copyright 2016 Providence Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.morimekta.providence.maven.plugin;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.JavaGenerator;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.maven.util.ProvidenceInput;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * mvn net.morimekta.providence:providence-maven-plugin:0.1.0-SNAPSHOT:help -Ddetail=true -Dgoal=compile
 */
public abstract class BaseGenerateSourcesMojo extends AbstractMojo {
    /**
     * Adds android.os.Parcelable support. Requires android dependencies, or
     * can use <code>net.morimekta.utils:android-util</code> for testing.
     */
    @Parameter(defaultValue = "false")
    protected boolean android;

    /**
     * If set to true will add jackson 2 annotations to messages and enums.
     * Required additional dependency on jackson 2 extra libraries:
     * <ul>
     *     <li><code>com.fasterxml.jackson.extra:jackson-annotations:2.x</code>
     *     <li><code>com.fasterxml.jackson.extra:jackson-extra:2.x</code>
     *     <li><code>com.fasterxml.jackson.extra:jackson-databind:2.x</code>
     * </ul>
     */
    @Parameter(defaultValue = "false")
    protected boolean jackson;

    /**
     * Generate model methods to read and write same as the binary protocol.
     * Can be turned off to reduce the amount of generated code. Serialization
     * will work regardless, but keeping this enabled speeds up the binary
     * serialization significantly.
     */
    @Parameter(defaultValue = "true")
    protected boolean rw_binary = true;

    /**
     * If set to true will add hazelcast annotations to messages and enums.
     */
    @Parameter(defaultValue = "false")
    protected boolean hazelcast_portable;

    /**
     * If true add version to generated annotations.
     */
    @Parameter(defaultValue = "false")
    protected boolean generated_annotation_version;

    /**
     * Dependencies to providence artifacts. 'providence' classifier and 'zip'
     * type is implied here.
     */
    @Parameter
    protected Dependency[] dependencies = new Dependency[0];

    /**
     * If true will add the generated sources to be compiled.
     */
    @Parameter(defaultValue = "true")
    protected boolean compileOutput;

    /**
     * Additional directories to find include files for thrift compilation.
     * The extra files there will not be compiled into source code.
     */
    @Parameter
    protected IncludeExcludeFileSelector includeDirs;

    /**
     * If the thrift program parser should fail if field ID is missing for any
     * field definitions parsed.
     */
    @Parameter
    protected boolean requireFieldId = false;

    /**
     * If the thrift program parser should fail if enum value is missing for
     * any enum value definition parsed.
     */
    @Parameter
    protected boolean requireEnumValue = false;

    // --- After here are internals, components and maven-set params.

    /**
     * Location of the output artifact.
     */
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    protected File buildDir = null;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project = null;

    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    protected ArtifactRepository localRepository = null;

    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Parameter(defaultValue = "${project.remoteRepositories}", readonly = true, required = true)
    protected List<ArtifactRepository> remoteRepositories = null;

    @Component
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private ArtifactResolver artifactResolver = null;

    @Component
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private RepositorySystem repositorySystem = null;

    boolean executeInternal(File outputDir,
                            IncludeExcludeFileSelector files,
                            String defaultInputIncludes,
                            boolean testCompile) throws MojoExecutionException, MojoFailureException {

        Set<File> inputs = ProvidenceInput.getInputFiles(project, files, defaultInputIncludes);
        if (inputs.isEmpty()) {
            return false;
        }

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new MojoExecutionException("Unable to create target directory " + outputDir);
            }
        }

        TreeSet<File> includes = new TreeSet<>();

        File workingDir = new File(buildDir, testCompile ? "providence-test" : "providence");
        File[] deleteFiles = workingDir.listFiles();
        if (!workingDir.exists()) {
            if (!workingDir.mkdirs()) {
                throw new MojoExecutionException("Unable to create working directory " + workingDir);
            }
        } else if (deleteFiles != null) {
            StreamSupport.<File>stream(Spliterators.spliterator(deleteFiles, Spliterator.DISTINCT | Spliterator.IMMUTABLE),
                                       false).forEach(File::delete);
        }

        Set<Artifact> resolvedArtifacts = new HashSet<>();
        for (Dependency dep : project.getDependencies()) {
            if ("provided".equalsIgnoreCase(dep.getScope())) {
                resolveDependency(dep, includes, workingDir, resolvedArtifacts);
            }
        }
        for (Dependency dep : dependencies) {
            dep.setType(ProvidenceAssemblyMojo.TYPE);
            if (isNullOrEmpty(dep.getClassifier())) {
                dep.setClassifier(ProvidenceAssemblyMojo.CLASSIFIER);
            }
            resolveDependency(dep, includes, workingDir, resolvedArtifacts);
        }

        if (includeDirs != null) {
            DirectoryScanner includeScanner = new DirectoryScanner();
            includeScanner.setIncludes(includeDirs.getIncludes());
            if (includeDirs.getExcludes() != null) {
                includeScanner.setExcludes(includeDirs.getExcludes());
            }
            includeScanner.setBasedir(project.getBasedir());
            includeScanner.scan();
            for (String dir : includeScanner.getIncludedDirectories()) {
                includes.add(new File(project.getBasedir(), dir));
            }
            for (String dir : includeScanner.getExcludedDirectories()) {
                includes.remove(new File(project.getBasedir(), dir));
            }
        }
        inputs.stream().map(File::getParentFile).forEach(includes::add);

        FileManager fileManager = new FileManager(outputDir);
        ProgramParser parser = new ThriftProgramParser(requireFieldId, requireEnumValue);
        TypeLoader loader = new TypeLoader(includes, parser);

        LinkedList<CProgram> documents = new LinkedList<>();

        for (File in : inputs) {
            try {
                documents.add(loader.load(in));
            } catch (ParseException e) {
                getLog().warn(e.asString());
                getLog().warn(".---------------------.");
                throw new MojoFailureException("Failed to parse thrift file: " + in.getName(), e);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to read thrift file: " + in.getName(), e);
            }
        }

        try {
            JavaOptions options = new JavaOptions();
            options.android = android;
            options.jackson = jackson;
            options.rw_binary = rw_binary;
            options.hazelcast_portable = hazelcast_portable;
            options.generated_annotation_version = generated_annotation_version;

            Generator generator = new JavaGenerator(fileManager, loader.getRegistry(), options);

            for (CProgram doc : documents) {
                try {
                    generator.generate(doc);
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to write document: " + doc.getProgramName(), e);
                } catch (GeneratorException e) {
                    getLog().warn(e.getMessage());
                    throw new MojoFailureException("Failed to generate document: " + doc.getProgramName(), e);
                }
            }
        } catch (GeneratorException e) {
            getLog().warn(e.getMessage());
            throw new MojoFailureException("Failed to generate file: " + e.getMessage(), e);
        }

        return compileOutput;
    }

    private void resolveDependency(Dependency dep,
                                   TreeSet<File> includes,
                                   File workingDir,
                                   Set<Artifact> resolvedArtifacts) throws MojoFailureException, MojoExecutionException {
        Artifact artifact = repositorySystem.createDependencyArtifact(dep);
        // Avoid resolving stuff we already have resolved.
        if (resolvedArtifacts.contains(artifact)) {
            return;
        }

        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setLocalRepository(localRepository);
        request.setRemoteRepositories(remoteRepositories);
        request.setResolveTransitively(false);
        request.setArtifact(artifact);

        ArtifactResolutionResult result = artifactResolver.resolve(request);

        boolean found = false;
        for (Artifact resolved : result.getArtifacts()) {
            if (artifact.equals(resolved)) {
                resolvedArtifacts.add(resolved);
                addDependencyInclude(workingDir, includes, resolved);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new MojoFailureException("Unable to resolve providence dependency: " +
                                           artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" +
                                           artifact.getVersion() + ":" + artifact.getClassifier());
        }
    }

    private void addDependencyInclude(File workingDir, Set<File> includes, Artifact artifact)
            throws MojoExecutionException {
        // TODO: Figure out if this is the right way to name the output directories.
        File outputDir = new File(workingDir, artifact.getGroupId().replaceAll("[.]", Strings.escape(File.separator)) + File.separator + artifact.getArtifactId());
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new MojoExecutionException("Unable to create output dir " + outputDir);
            }
        }

        if (artifact.getFile().isDirectory()) {
            // Otherwise the dependency is a local module, and not packaged.
            // In this case the we need to try to find thrift files in the
            // file tree under that directly.
            DirectoryScanner includeScanner = new DirectoryScanner();
            includeScanner.setBasedir(artifact.getFile());

            // Include basically everything.
            includeScanner.setIncludes(new String[]{
                    "**/*.*",
            });
            // Skip java class files. These cannot be used as includes anyway, and
            // is hte most common content of the default artifacts.
            includeScanner.setExcludes(new String[]{
                    "**/*.class",
            });
            includeScanner.scan();

            try {
                for (String filePath : includeScanner.getIncludedFiles()) {
                    if (ReflectionUtils.isThriftFile(filePath)) {
                        Path file = Paths.get(project.getBasedir().getAbsolutePath(), filePath);
                        File of = new File(outputDir, new File(filePath).getName());
                        try (FileOutputStream fos = new FileOutputStream(of, false);
                             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                            Files.copy(file, bos);
                        }
                    }
                }
                includes.add(outputDir);
            } catch (IOException e) {
                throw new MojoExecutionException("" + e.getMessage(), e);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(artifact.getFile());
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 ZipInputStream zis = new ZipInputStream(bis)) {

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.isDirectory() || !ReflectionUtils.isThriftFile(entry.getName())) {
                        zis.closeEntry();
                        continue;
                    }
                    File of = new File(outputDir, new File(entry.getName()).getName());

                    try (FileOutputStream fos = new FileOutputStream(of, false);
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        IOUtils.copy(zis, bos);
                    }

                    zis.closeEntry();
                }

                includes.add(outputDir);
            } catch (IOException e) {
                throw new MojoExecutionException("" + e.getMessage(), e);
            }
        }
    }

}
