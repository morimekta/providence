package net.morimekta.providence.tools.compiler.options;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentList;
import net.morimekta.console.args.BaseOption;
import net.morimekta.providence.generator.GeneratorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Cli options handler for stream specification (file / url, appendEnumClass).
 */
public class HelpOption extends BaseOption {
    private final Consumer<HelpSpec>               consumer;
    private final Supplier<List<GeneratorFactory>> factoryListSupplier;

    public HelpOption(String name,
                      String shortNames,
                      String usage,
                      Supplier<List<GeneratorFactory>> factoryListSupplier,
                      Consumer<HelpSpec> consumer) {
        super(name, shortNames, "[language]", usage, null, false, false, false);
        this.factoryListSupplier = factoryListSupplier;
        this.consumer = consumer;
    }

    @Override
    public void validate() {

    }

    @Override
    public int applyShort(String opts, ArgumentList args) {
        if (opts.length() > 1) {
            throw new ArgumentException("Only one help param: " + args.get(0));
        }
        consumer.accept(new HelpSpec(getGenerator(args)));
        return args.remaining();
    }

    @Override
    public int apply(ArgumentList args) {
        consumer.accept(new HelpSpec(getGenerator(args)));
        return args.remaining();
    }

    private GeneratorFactory getGenerator(ArgumentList args) {
        if (args.remaining() > 2) {
            throw new ArgumentException("Only one help spec allowed");
        }

        if (args.remaining() == 2) {
            String spec = args.get(1);
            List<GeneratorFactory> factories = factoryListSupplier.get();
            Map<String, GeneratorFactory> factoryMap = new HashMap<>();
            for (GeneratorFactory factory : factories) {
                factoryMap.put(factory.generatorName()
                                      .toLowerCase(), factory);
            }

            GeneratorFactory factory = factoryMap.get(spec.toLowerCase());
            if (factory == null) {
                throw new ArgumentException("Unknown output language " + spec);
            }
            return factory;
        }
        return null;
    }
}
