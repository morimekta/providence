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

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.JGenerator;
import net.morimekta.providence.generator.format.java.JOptions;
import net.morimekta.providence.generator.format.java.tiny.TinyGenerator;
import net.morimekta.providence.generator.format.java.tiny.TinyOptions;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.parser.ThriftParser;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * mvn net.morimekta.providence:providence-maven-plugin:0.1.0-SNAPSHOT:help -Ddetail=true -Dgoal=compile
 */
public abstract class BaseGenerateSourcesMojo extends AbstractMojo {
    /**
     * Skip the providence generator step for this module.
     */
    @Parameter(defaultValue = "false")
    protected boolean skip;

    /**
     * Use the "tiny java" generator version instead of the default. It has
     * minimal dependencies, and cannot be serialized using the providence
     * libraries. It will only require dependency on one library:
     * <ul>
     *     <li><code>net.morimekta.utils:io-util:0.2.3</code>
     * </ul>
     */
    @Parameter(defaultValue = "false")
    protected boolean tiny;

    /**
     * Adds android.os.Parcelable support. Not compatible with 'tiny'.
     */
    @Parameter(defaultValue = "false")
    protected boolean android;

    /**
     * If set to true will add jackson 2 annotations to messages and enums.
     * Required additional dependency on jackson 2 core libraries:
     * <ul>
     *     <li><code>com.fasterxml.jackson.core:jackson-annotations:2.x</code>
     *     <li><code>com.fasterxml.jackson.core:jackson-core:2.x</code>
     *     <li><code>com.fasterxml.jackson.core:jackson-databind:2.x</code>
     * </ul>
     */
    @Parameter(defaultValue = "false")
    protected boolean jackson;

    /**
     * If true will add the generated sources to be compiled.
     */
    @Parameter(defaultValue = "true")
    protected boolean compileOutput;

    /**
     * Additional directories to find include files for thrift compilation.
     * The extra files there will <b>not</b> be compiled into source code.
     */
    @Parameter
    protected IncludeExcludeFileSelector includeDirs;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    private Set<File> getInputFiles(IncludeExcludeFileSelector inputFiles,
                                    String defaultInputInclude) {
        TreeSet<File> inputs = new TreeSet<>();

        DirectoryScanner inputScanner = new DirectoryScanner();
        if (inputFiles != null) {
            inputScanner.setIncludes(inputFiles.getIncludes());
            if (inputFiles.getExcludes() != null) {
                inputScanner.setExcludes(inputFiles.getExcludes());
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

    boolean executeInternal(File outputDir,
                            IncludeExcludeFileSelector inputFiles,
                            String defaultInputIncludes) throws MojoExecutionException, MojoFailureException {
        if (skip) {
            return false;
        }

        Set<File> inputs = getInputFiles(inputFiles, defaultInputIncludes);
        if (inputs.isEmpty()) {
            return false;
        }

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new MojoExecutionException("Unable to create target directory " + outputDir);
            }
        }

        TreeSet<File> includes = new TreeSet<>();

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
        } else {
            includes.addAll(inputs.stream()
                                  .map(File::getParentFile)
                                  .collect(Collectors.toList()));
        }

        FileManager fileManager = new FileManager(outputDir);
        Parser parser = new ThriftParser();
        TypeLoader loader = new TypeLoader(includes, parser);

        LinkedList<CDocument> documents = new LinkedList<>();

        for (File in : inputs) {
            try {
                documents.add(loader.load(in));
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to read thrift file: " + in.getName(), e);
            } catch (ParseException e) {
                getLog().warn(e.getMessage());
                getLog().warn(".---------------------.");
                throw new MojoFailureException("Failed to parse thrift file: " + in.getName(), e);
            }
        }

        try {
            Generator generator;
            if (tiny) {
                TinyOptions options = new TinyOptions();
                options.jackson = jackson;
                if (android) {
                    throw new MojoExecutionException("Android option not compatible with pure-jackson.");
                }
                generator = new TinyGenerator(fileManager, loader.getRegistry(), options);
            } else {
                JOptions options = new JOptions();
                options.android = android;
                generator = new JGenerator(fileManager, loader.getRegistry(), options);
            }

            for (CDocument doc : documents) {
                try {
                    generator.generate(doc);
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to write document: " + doc.getPackageName(), e);
                } catch (GeneratorException e) {
                    getLog().warn(e.getMessage());
                    throw new MojoFailureException("Failed to generate document: " + doc.getPackageName(), e);
                }
            }
        } catch (GeneratorException e) {
            getLog().warn(e.getMessage());
            throw new MojoFailureException("Failed to generate file: " + e.getMessage(), e);
        }

        return compileOutput;
    }
}
