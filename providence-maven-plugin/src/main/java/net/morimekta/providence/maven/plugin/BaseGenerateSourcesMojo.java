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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.JavaGenerator;
import net.morimekta.providence.generator.format.java.JavaGeneratorFactory;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.maven.util.ProvidenceDependency;
import net.morimekta.providence.maven.util.ProvidenceInput;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;
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
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * mvn net.morimekta.providence:providence-maven-plugin:0.1.0-SNAPSHOT:help -Ddetail=true -Dgoal=compile
 */
public abstract class BaseGenerateSourcesMojo extends AbstractMojo {
    private static final String TEST = "test";

    // -----------    PARSER OPTIONS    ----------- //

    /**
     * If the thrift program parser should fail if enum value is missing for
     * any enum value definition parsed.
     */
    @Parameter(defaultValue = "false",
               property = "providence.gen.require_enum_value",
               alias = "requireEnumValue")
    protected boolean require_enum_value;

    /**
     * If the thrift program parser should fail if field ID is missing for any
     * field definitions parsed.
     */
    @Parameter(defaultValue = "false",
               property = "providence.gen.require_field_id",
               alias = "requireFieldId")
    protected boolean require_field_id;

    // -----------    GENERATE OPTIONS    ----------- //
    /**
     * Adds android.os.Parcelable support. Requires android dependencies, or
     * can use <code>net.morimekta.utils:android-util</code> for testing.
     */
    @Parameter(defaultValue = "false",
               property = "providence.gen.android")
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
    @Parameter(defaultValue = "false",
               property = "providence.gen.jackson")
    protected boolean jackson;

    /**
     * Generate model methods to read and write same as the binary protocol.
     * Can be turned off to reduce the amount of generated code. Serialization
     * will work regardless, but keeping this enabled speeds up the binary
     * serialization significantly.
     */
    @Parameter(defaultValue = "true",
               property = "providence.gen.rw_binary")
    protected boolean rw_binary = true;

    /**
     * If set to true will add hazelcast annotations to messages and enums.
     */
    @Parameter(defaultValue = "false",
               property = "providence.gen.hazelcast_portable")
    protected boolean hazelcast_portable;

    /**
     * If true add version to generated annotations.
     */
    @Parameter(defaultValue = "true",
               property = "providence.gen.generated_annotation_version")
    protected boolean generated_annotation_version;

    /**
     * Generate public constructors with all fields as arguments for structs and
     * exceptions.
     */
    @Parameter(defaultValue = "false",
               property = "providence.gen.public_constructors")
    protected boolean public_constructors;

    /**
     * Print extra debug info to the maven log.
     */
    @Parameter(defaultValue = "false",
               property = "providence.print_debug")
    protected boolean print_debug;

    /**
     * Dependencies to providence artifacts. 'providence' classifier and 'zip'
     * type is implied here, but can be overridden. The thrift files from these
     * artifacts will be available for inclusion by compiled thrift files, but
     * will not be compiled themselves.
     */
    @Parameter
    protected ProvidenceDependency[] dependencies = new ProvidenceDependency[0];

    /**
     * If true will add the generated sources to be compiled.
     */
    @Parameter(defaultValue = "true")
    protected boolean compileOutput;

    /**
     * Skip thrift files if they are missing the java namespace.
     */
    @Parameter(defaultValue = "false", property = "providence.skip_if_missing_namespace")
    protected boolean skipIfMissingNamespace;

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

    boolean executeInternal(IncludeExcludeFileSelector includeDirs,
                            File outputDir,
                            IncludeExcludeFileSelector inputSelector,
                            String defaultInputIncludes,
                            boolean testCompile) throws MojoExecutionException, MojoFailureException {
        Set<File> inputFiles = ProvidenceInput.getInputFiles(project, inputSelector, defaultInputIncludes, print_debug, getLog());
        if (inputFiles.isEmpty()) {
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
        for (Dependency dep : dependencies) {
            if (testCompile || !TEST.equalsIgnoreCase(dep.getScope())) {
                resolveDependency(dep, includes, workingDir, resolvedArtifacts);
            }
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

        FileManager fileManager = new FileManager(outputDir);
        ProgramParser parser = new ThriftProgramParser(require_field_id, require_enum_value);
        TypeLoader loader = new TypeLoader(includes, parser);

        if (print_debug) {
            inputFiles.stream()
                      .filter(Objects::nonNull)
                      .map(file -> {
                          try {
                              return file.getAbsoluteFile().getCanonicalPath();
                          } catch (IOException e) {
                              e.printStackTrace();
                              return null;
                          }
                      })
                      .sorted()
                      .forEach(f -> getLog().info("Compiling: " + f));
        }

        JavaOptions javaOptions = new JavaOptions();
        javaOptions.android = android;
        javaOptions.jackson = jackson;
        javaOptions.rw_binary = rw_binary;
        javaOptions.hazelcast_portable = hazelcast_portable;
        javaOptions.generated_annotation_version = generated_annotation_version;
        javaOptions.public_constructors = public_constructors;
        GeneratorOptions generatorOptions = new GeneratorOptions();
        generatorOptions.generator_program_name = "providence-maven-plugin";
        generatorOptions.program_version = getVersionString();

        GeneratorFactory factory = new JavaGeneratorFactory();
        Generator generator = new JavaGenerator(fileManager,
                                                generatorOptions,
                                                javaOptions);

        Path base = project.getBasedir().toPath().toAbsolutePath();
        if (project.getParent() != null && project.getParent().getBasedir() != null) {
            // Only replace with parent if parent is a parent directory of this.
            Path parentBase = project.getParent().getBasedir().toPath().toAbsolutePath();
            if (base.toString().startsWith(parentBase.toString())) {
                base = parentBase;
            }
        }

        for (File in : inputFiles) {
            ProgramTypeRegistry registry;
            try {
                registry = loader.load(in);
            } catch (SerializerException e) {
                // ParseException is a SerializerException. And serialize exceptions can come from
                // failing to make sense of constant definitions.
                getLog().error("    ============ >> PROVIDENCE << ============");
                getLog().error("");
                Arrays.stream(e.asString().split("\r?\n"))
                      .forEach(l -> getLog().error(l));
                getLog().error("");
                getLog().error("    ============ << PROVIDENCE >> ============");
                throw new MojoFailureException("Failed to parse thrift file: " + in.getName(), e);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to read thrift file: " + in.getName(), e);
            }

            try {
                if (skipIfMissingNamespace && registry.getProgram().getNamespaceForLanguage(factory.generatorName()) == null) {
                    getLog().warn("Skipping (no " +  factory.generatorName() + " namespace) " +
                                  base.relativize(in.toPath()));
                    continue;
                }
                generator.generate(registry);
            } catch (GeneratorException e) {
                throw new MojoFailureException("Failed to generate program: " + registry.getProgram().getProgramName(), e);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to write program file: " + registry.getProgram().getProgramName(), e);
            }
        }

        try {
            generator.generateGlobal(loader.getProgramRegistry(), inputFiles);
        } catch (GeneratorException e) {
            throw new MojoFailureException("Failed to generate global", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write global file", e);
        }

        return compileOutput;
    }

    private static String getVersionString() {
        Properties properties = new Properties();
        try (InputStream in = BaseGenerateSourcesMojo.class.getResourceAsStream(
                "/net/morimekta/providence/maven/providence_version.properties")) {
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
        return properties.getProperty("providence.version");
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
        request.setManagedVersionMap(project.getManagedVersionMap());
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
            // Skip basic java files. These cannot be used as includes anyway, and
            // is the most common content of the default artifacts.
            includeScanner.setExcludes(new String[]{
                    "**/*.class",
                    "**/*.java",
                    "**/*.properties",
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
