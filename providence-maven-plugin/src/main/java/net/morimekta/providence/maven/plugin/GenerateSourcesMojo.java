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
     * Location of the output java source.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/providence")
    private File outputDir = null;

    /**
     * Files to compile. By default will select all '.thrift' files in
     * 'src/main/providence/' and subdirectories.
     */
    @Parameter(alias = "inputFiles")
    protected IncludeExcludeFileSelector files;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (executeInternal(outputDir, files, "src/main/providence/**/*.thrift", false)) {
            project.addCompileSourceRoot(outputDir.getPath());
        }
    }
}
