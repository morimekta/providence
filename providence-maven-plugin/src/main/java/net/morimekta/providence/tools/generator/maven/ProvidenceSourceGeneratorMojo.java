package net.morimekta.providence.tools.generator.maven;

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

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.JGenerator;
import net.morimekta.providence.generator.format.java.JOptions;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.util.Strings;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * mvn net.morimekta.providence:providence-maven-plugin:0.0.1-SNAPSHOT:generate-providence-sources -X
 */
@Mojo(name = "generate-providence-sources",
      defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class ProvidenceSourceGeneratorMojo extends AbstractMojo {
    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "DEFAULT")
    private JOptions.Containers containers = JOptions.Containers.DEFAULT;

    @Parameter(defaultValue = "compile")
    private String scope = "compile";

    @Parameter
    private boolean android = false;

    @Parameter
    private boolean jackson = false;

    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/providence")
    private File outputDir = null;

    @Parameter(required = true)
    private IncludeExcludeFileSelector inputFiles = null;

    @Parameter
    private IncludeExcludeFileSelector includeDirs = null;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        switch (scope.toLowerCase()) {
            case "compile":
                project.addCompileSourceRoot(outputDir.getPath());
                break;
            case "test":
                project.addTestCompileSourceRoot(outputDir.getPath());
                break;
            default:
                getLog().info("Invalid compile scope " + scope + ": Must be test or compile.");
                throw new MojoExecutionException("Invalid compile scope " + scope + ": Must be test or compile.");
        }

        JOptions options = new JOptions();
        if (containers != null) {
            options.containers = containers;
        }
        options.android = android;
        options.jackson = jackson;

        getLog().info("--- Android :" + options.android);
        getLog().info("--- Jackson :" + options.jackson);
        getLog().info("--- Containers :" + options.containers);
        getLog().info("--- Root :" + project.getBasedir().getAbsolutePath());

        TreeSet<File> inputs = new TreeSet<>();
        TreeSet<File> includes = new TreeSet<>();

        DirectoryScanner inputScanner = new DirectoryScanner();
        inputScanner.setIncludes(inputFiles.getIncludes());
        if (inputFiles.getExcludes() != null) {
            inputScanner.setExcludes(inputFiles.getExcludes());
        }
        inputScanner.setBasedir(project.getBasedir());
        inputScanner.scan();

        for (String file : inputScanner.getIncludedFiles()) {
            inputs.add(new File(project.getBasedir(), file));
        }

        getLog().info("--- Input Files:    " + Strings.join(", ", inputs));

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

            getLog().info("--- Included Dirs:  " + Strings.join(", ", includeScanner.getIncludedDirectories()));
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
                getLog().error(e.getMessage());
                getLog().info(".---------------------.");
                throw new MojoExecutionException("Failed to read thrift file: " + in.getName(), e);
            } catch (ParseException e) {
                getLog().error(e.getMessage());
                getLog().info(".---------------------.");
                throw new MojoFailureException("Failed to parse thrift file: " + in.getName(), e);
            }
        }

        Generator generator = new JGenerator(fileManager, loader.getRegistry(), options);


        for (CDocument doc : documents) {
            try {
                generator.generate(doc);
            } catch (IOException e) {
                getLog().error(e.getMessage());
                getLog().info(".---------------------.");
                throw new MojoExecutionException("Failed to write document: " + doc.getPackageName(), e);
            } catch (GeneratorException e) {
                getLog().error(e.getMessage());
                getLog().info(".---------------------.");
                throw new MojoFailureException("Failed to generate document: " + doc.getPackageName(), e);
            }
        }
    }
}
