package net.morimekta.providence.tools.config.cmd;

import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.tools.config.ConfigOptions;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base command of the type that needs a real providence config.
 */
public abstract class CommandBase implements Command {
    @Override
    public void execute(ConfigOptions options) throws IOException {
        Map<String, File> includeMap = options.getIncludeMap(options.getIncludes());

        Set<File> rootSet = includeMap.values()
                                      .stream()
                                      .map(File::getParentFile)
                                      .collect(Collectors.toSet());
        TypeLoader loader = new TypeLoader(rootSet);
        for (File file : includeMap.values()) {
            loader.load(file);
        }

        execute(new ProvidenceConfig(loader.getProgramRegistry(), null, options.isStrict()));
    }

    public abstract void execute(ProvidenceConfig config) throws IOException;
}
