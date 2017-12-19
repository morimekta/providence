package net.morimekta.providence.generator.format.js;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.util.FileManager;

import java.io.PrintStream;
import java.util.Collection;

public class JsGeneratorFactory implements GeneratorFactory {
    @Override
    public String generatorName() {
        return "js";
    }

    @Override
    public String generatorDescription() {
        return "Generates JavaScript (es5.1 or es6).";
    }

    @Override
    public void printGeneratorOptionsHelp(PrintStream out) {
        System.out.println(" - es51                : Generate for ECMA Script 5.1 (no maps, promises).");
        System.out.println(" - ts                  : Generate definition files for typescript.");
        System.out.println(" - closure             : Generate google closure dependencies (goog.require and goog.provide).");
        System.out.println(" - node_js             : Generate node.js module wrapper.");
        System.out.println(" - pvd                 : Provide core providence models for services if needed.");
    }

    private JSOptions makeJSOptions(Collection<String> optionNames) {
        JSOptions options = new JSOptions();
        for (String opt : optionNames) {
            switch (opt) {
                case "es51":
                    options.es51 = true;
                    break;
                case "ts":
                    options.type_script = true;
                    break;
                case "closure":
                    options.closure = true;
                    break;
                case "node.js":
                    options.node_js = true;
                    break;
                case "pvd":
                    options.pvd = true;
                    break;
                default:
                    throw new RuntimeException("No such option for js generator: " + opt);
            }
        }
        options.validate();
        return options;
    }

    @Override
    public Generator createGenerator(FileManager manager,
                                     GeneratorOptions generatorOptions,
                                     Collection<String> options) {
        return new JSGenerator(manager, generatorOptions, makeJSOptions(options));
    }
}
