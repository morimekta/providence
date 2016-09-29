package net.morimekta.providence.tools.config;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.tools.config.options.Options;
import net.morimekta.providence.util.pretty.TokenizerException;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Tool for Providence.
 */
public class Config {
    public static void main(String... args) {
        new Config().run(args);
    }

    public void run(String... args) {
        try {
            Properties properties = new Properties();
            properties.load(Config.class.getResourceAsStream("/build.properties"));

            Options op = new Options();
            ArgumentParser cli = op.getArgumentParser("pvdcfg",
                                                      "v" + properties.getProperty("build.version"),
                                                      "Providence Config Tool");
            try {
                cli.parse(args);
                if (op.isHelp()) {
                    System.out.println(cli.getDescription() + " - " + cli.getVersion());
                    System.out.println("Usage: " + cli.getSingleLineUsage());
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available Commands:");
                    System.out.println();
                    op.getCommandSet()
                      .printUsage(System.out);
                    return;
                }

                cli.validate();
                op.execute();
            } catch (TokenizerException e) {
                System.err.println(e.asString());
                exit(1);
            } catch (SerializerException e) {
                System.err.println("Serialization error: " + e.toString());
                exit(1);
            } catch (ArgumentException e) {
                System.err.println(e.toString());
                // e.printStackTrace();
                exit(1);
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    protected void exit(int code) {
        System.exit(code);
    }
}
