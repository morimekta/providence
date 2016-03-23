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

import net.morimekta.providence.generator.format.java.JOptions;
import net.morimekta.providence.generator.util.FileManager;
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
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;

/**
 *
 */
@Mojo(name = "generate-providence-sources",
      defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class ProvidenceSourceGeneratorMojo extends AbstractMojo {
    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources",
               required = true)
    private File outputDir = null;

    /**
     * Location of the output java source.
     */
    @Parameter
    private File[] includeDir = null;

    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "DEFAULT")
    private JOptions.Containers containers = JOptions.Containers.DEFAULT;

    @Parameter
    private boolean android = false;

    @Parameter
    private boolean jackson = false;

    @Parameter
    private IncludeExcludeFileSelector inputFiles;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        FileManager fileManager = new FileManager(outputDir);
        // Collections.addAll(includes, includeDir);

        // Collections.addAll(inputFiles, files.);

        Parser parser = new ThriftParser();

        JOptions options = new JOptions();
        if (containers != null) {
            options.containers = containers;
        }
        options.android = android;
        options.jackson = jackson;

        getLog().error("--- Android :" + options.android);
        getLog().error("--- Jackson :" + options.jackson);
        getLog().error("--- Containers :" + options.containers);
        getLog().error("--- IncludeFiles" + Strings.join(", ", inputFiles.getIncludes()));
        getLog().error("--- ExcludeFiles" + Strings.join(", ", inputFiles.getExcludes()));
    }

    public FileManager getFileManager() throws CmdLineException {
        return new FileManager(outputDir);
    }

}
