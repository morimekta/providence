package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.util.FileManager;

import java.io.PrintStream;
import java.util.Collection;

public class JavaGeneratorFactory implements GeneratorFactory {
    @Override
    public String generatorName() {
        return "java";
    }

    @Override
    public String generatorDescription() {
        return "Generates java (1.8+) classes.";
    }

    @Override
    public void printGeneratorOptionsHelp(PrintStream out) {
        out.println(" - android             : Add android parcelable interface to model classes.");
        out.println(" - jackson             : Add jackson 2 annotations to model classes.");
        out.println(" - no_rw_binary        : Skip adding the binary RW methods to generated code. [Default on]");
        out.println(" - hazelcast_portable  : Add hazelcast portable to annotated model classes, and add portable\n" +
                    "                         factories.");
        out.println(" - no_generated_annotation_version : Remove providence version from the <code>@Generated</code>\n" +
                    "                         annotation for each generated class. [Default on]");
        out.println(" - public_constructors : Generate public constructors for all structs and exceptions. Have no\n" +
                    "                         effect on unions.");
    }

    @Override
    public Generator createGenerator(FileManager manager,
                                     GeneratorOptions generatorOptions,
                                     Collection<String> options) {
        return new JavaGenerator(manager);
    }
}
