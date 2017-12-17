package net.morimekta.providence.generator.format.json;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.util.FileManager;

import java.io.PrintStream;
import java.util.Collection;

/**
 * Factory for JSON generator.
 */
public class JsonGeneratorFactory implements GeneratorFactory {
    @Override
    public String generatorName() {
        return "json";
    }

    @Override
    public String generatorDescription() {
        return "Print out the IDL as json files.";
    }

    @Override
    public void printGeneratorOptionsHelp(PrintStream out) {
        out.println("No options available for json generator.");
    }

    @Override
    public Generator createGenerator(FileManager manager,
                                     GeneratorOptions generatorOptions,
                                     Collection<String> options) {
        return new JsonGenerator(manager);
    }
}
