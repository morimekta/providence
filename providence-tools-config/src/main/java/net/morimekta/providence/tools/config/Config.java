package net.morimekta.providence.tools.config;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.util.pretty.TokenizerException;

import java.io.IOException;

import static net.morimekta.providence.tools.common.options.Utils.getVersionString;

/**
 * Configuration Tool for Providence.
 */
public class Config {
    private final STTY tty;

    public Config() {
        this(new STTY());
    }

    protected Config(STTY tty) {
        this.tty = tty;
    }

    public static void main(String... args) {
        new Config().run(args);
    }

    public void run(String... args) {
        try {
            ConfigOptions op = new ConfigOptions(tty);
            ArgumentParser cli = op.getArgumentParser("pvdcfg",
                                                      "Providence Config Tool");
            try {
                cli.parse(args);
                if (op.isHelp()) {
                    System.out.println(cli.getDescription() + " - " + getVersionString());
                    System.out.println("Usage: " + cli.getSingleLineUsage());
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available Commands:");
                    System.out.println();
                    op.getCommandSet().printUsage(System.out);
                    return;
                } else if (op.version) {
                    System.out.println(cli.getDescription() + " - " + getVersionString());
                    return;
                }

                cli.validate();
                op.execute();
                return;
            } catch (ArgumentException e) {
                System.err.println("Invalid argument: " + e.getMessage());
                System.err.println("Usage: " + cli.getSingleLineUsage());
                if (op.verbose) {
                    e.printStackTrace();
                }
            } catch (TokenizerException e) {
                System.err.println(e.asString());
                if (op.verbose) {
                    e.printStackTrace();
                }
            } catch (SerializerException e) {
                System.err.println("Serialization error: " + e.toString());
                if (op.verbose) {
                    e.printStackTrace();
                }
            } catch (IOException | RuntimeException e) {
                System.err.println("IO Error: " + e.toString());
                if (op.verbose) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        exit(1);
    }

    protected void exit(int code) {
        System.exit(code);
    }
}
