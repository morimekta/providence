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
 * Generate providence test sources from thrift definitions.
 */
@Mojo(name = "testCompile",
      defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
      instantiationStrategy = InstantiationStrategy.PER_LOOKUP)
public class GenerateTestSourcesMojo extends BaseGenerateSourcesMojo {
    /**
     * Skip the providence test compile step for this module.
     */
    @Parameter(alias = "skip", defaultValue = "false")
    protected boolean skipTestCompile = false;

    /**
     * Location of the output java source.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/providence")
    private File outputDir = null;

    /**
     * Files to compile. By default will select all '.thrift' files in
     * 'src/test/providence/' and subdirectories.
     */
    @Parameter(alias = "inputFiles")
    protected IncludeExcludeFileSelector testFiles;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTestCompile) {
            getLog().info("Skipping providence:testCompile");
            return;
        }
        if (executeInternal(outputDir, testFiles, "src/test/providence/**/*.thrift", true)) {
            project.addTestCompileSourceRoot(outputDir.getPath());
        }
    }
}
