package net.morimekta.providence.tools.compiler.options;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentList;
import net.morimekta.console.args.BaseOption;
import net.morimekta.providence.tools.compiler.Language;

import java.util.function.Consumer;

/**
 * Cli options handler for stream specification (file / url, appendEnumClass).
 */
public class HelpOption extends BaseOption {
    private final Consumer<HelpSpec> consumer;

    public HelpOption(String name,
                      String shortNames,
                      String usage,
                      Consumer<HelpSpec> consumer) {
        super(name, shortNames, "[language]", usage, null, false, false, false);
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
        consumer.accept(new HelpSpec(getLanguage(args)));
        return args.remaining();
    }

    @Override
    public int apply(ArgumentList args) {
        consumer.accept(new HelpSpec(getLanguage(args)));
        return args.remaining();
    }

    private Language getLanguage(ArgumentList args) {
        if (args.remaining() > 2) {
            throw new ArgumentException("Only one help spec allowed");
        }

        Language language = null;

        if (args.remaining() > 1) {
            String lang = args.get(1);
            for (Language spec : Language.values()) {
                if (lang.equalsIgnoreCase(spec.name())) {
                    language = spec;
                    break;
                }
            }
            if (language == null) {
                throw new ArgumentException("Unknown language " + lang);
            }
        }

        return language;
    }
}
