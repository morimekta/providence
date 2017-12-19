package net.morimekta.providence.tools.config;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.pretty.TokenizerException;

import java.io.IOException;

import static net.morimekta.providence.tools.common.Utils.getVersionString;

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
        ConfigOptions op = new ConfigOptions(tty);
        try {
            ArgumentParser cli = op.getArgumentParser("pvdcfg",
                                                      "Providence Config Tool");
            try {
                cli.parse(args);
                if (op.showHelp()) {
                    System.out.println(cli.getDescription() + " - " + getVersionString());
                    System.out.println("Usage: " + cli.getSingleLineUsage());
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available Commands:");
                    System.out.println();
                    op.getCommandSet().printUsage(System.out);
                    return;
                } else if (op.showVersion()) {
                    System.out.println(cli.getDescription() + " - " + getVersionString());
                    return;
                }

                cli.validate();
                op.execute();
                return;
            } catch (ArgumentException e) {
                System.err.println("Invalid argument: " + e.getMessage());
                System.err.println("Usage: " + cli.getSingleLineUsage());
                if (op.verbose()) {
                    e.printStackTrace();
                }
            } catch (ParseException e) {
                System.out.flush();
                System.err.println(e.asString());
                if (op.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (TokenizerException e) {
                System.out.flush();
                System.err.println(e.asString());
                if (op.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (SerializerException e) {
                System.out.flush();
                System.err.println("Serialization error: " + e.toString());
                if (op.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (IOException | RuntimeException e) {
                System.out.flush();
                System.err.println("IO Error: " + e.toString());
                if (op.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.flush();
            System.err.println("Unhandled exception: " + e.toString());
            if (op.verbose()) {
                System.err.println();
                e.printStackTrace();
            }
        }
        exit(1);
    }

    protected void exit(int code) {
        System.exit(code);
    }
}
