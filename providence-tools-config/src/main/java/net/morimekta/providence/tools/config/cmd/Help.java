package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.SubCommandSet;
import net.morimekta.providence.config.ProvidenceConfig;

/**
 * Simple help command.
 */
public class Help implements Command {
    private final SubCommandSet<Command> commandSet;
    private final ArgumentParser parent;

    private String command = null;

    public Help(SubCommandSet<Command> commandSet,
                ArgumentParser parent) {
        this.commandSet = commandSet;
        this.parent = parent;
    }

    @Override
    public void execute(ProvidenceConfig config) {
        if (command == null) {
            System.out.println(parent.getDescription() + " - " + parent.getVersion());
            System.out.println("Usage: " + parent.getSingleLineUsage());
            System.out.println();
            parent.printUsage(System.out);
            System.out.println();
            System.out.println("Available Commands:");
            System.out.println();
            commandSet.printUsage(System.out);
        } else {
            System.out.println(parent.getDescription() + " - " + parent.getVersion());
            System.out.println("Usage: " + commandSet.getSingleLineUsage(command));
            System.out.println();
            commandSet.printUsage(System.out, command);
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " [...] help", parent.getVersion(), "");
        parser.add(new Argument("cmd", "Command to show help for", this::setCommand, null, null, false, false, false));
        return parser;
    }
}
