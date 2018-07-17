package com.toxicmenu.terminal;

import java.io.PrintWriter;
import java.util.Arrays;

import com.toxicmenu.discordbot.ToxicBot;
import jline.TerminalFactory;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public interface CommandExecutor {
    public static final Options options = new Options();
    public static final CommandLineParser optionsParser = new BasicParser();
    public static final HelpFormatter optionsHelper = new HelpFormatter();

    public void onCommand(String var1, Terminal var2, String[] var3);

    public ArgumentList getArguments();

    default public CommandLine paradiseOptions(String[] args, int start) {
        return this.paradiseOptions(args, start, true);
    }

    default public CommandLine paradiseOptions(String[] args, int start, boolean sendHelp) {
        try {
            CommandLine line = optionsParser.parse(options, Arrays.copyOfRange(args, start, args.length));
            if (line == null) {
                throw new Exception("line == null");
            }
            return line;
        }
        catch (Exception e) {
            if (sendHelp) {
                ToxicBot.getTerminal().write("§cException: " + e.getMessage());
                optionsHelper.printUsage(new PrintWriter(new CostumSystemPrintStream(ToxicBot.getTerminal())), TerminalFactory.get().getWidth(), "Command Help", options);
            }
            return null;
        }
    }

    default public void printHelp(boolean wrongUsage) {
        if (wrongUsage) {
            ToxicBot.getTerminal().writeMessage("§cWrong commands usage!");
            ToxicBot.getTerminal().writeMessage("§cAvariable options:");
        }
        if (this.getArguments() != null) {
            for (ArgumentList.Argument s : this.getArguments().getArguments()) {
                ToxicBot.getTerminal().writeMessage(s.format());
            }
        } else {
            ToxicBot.getTerminal().writeMessage("§cNo argument list/help avariable!");
        }
    }

    default public String createArgumentInfo(String args, String usage) {
        return "§c" + args + " §7| §a" + usage;
    }
}