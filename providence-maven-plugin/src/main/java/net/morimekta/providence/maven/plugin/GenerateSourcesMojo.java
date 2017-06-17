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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import java.io.File;

/**
 * Generate providence sources from thrift definitions.
 */
@Mojo(name = "compile",
      defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class GenerateSourcesMojo extends BaseGenerateSourcesMojo {
    /**
     * Skip the providence compile step for this module.
     */
    @Parameter(alias = "skip",
               property = "providence.skip",
               defaultValue = "false")
    protected boolean skipCompile = false;

    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/providence",
               property = "providence.main.output",
               alias = "outputDir")
    protected File output = null;

    /**
     * Files to compile. By default will select all '.thrift' files in
     * 'src/main/providence/' and subdirectories. Simple includes can be
     * specified by property <code>providence.main.input</code>.
     */
    @Parameter(alias = "files")
    protected IncludeExcludeFileSelector input;

    /**
     * Additional directories to find include files for thrift compilation.
     * The extra files there will not be compiled into source code.
     */
    @Parameter(alias = "includeDirs")
    protected IncludeExcludeFileSelector includes;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCompile) {
            getLog().info("Skipping providence:compile");
            return;
        }

        String defaultInputIncludes = System.getProperties()
                                            .getProperty("providence.main.input",
                                                         "src/main/providence/**/*.thrift");
        if (executeInternal(includes, output, input, defaultInputIncludes, false)) {
            project.addCompileSourceRoot(output.getPath());
        }
    }
}
