package com.toxicmenu.terminal;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.ArrayUtils;

public class CommandRegistry {
    private static CopyOnWriteArrayList<CommandHolder> cmds = new CopyOnWriteArrayList();

    static {
        //CommandRegistry.registerCommand(new CommandHelp(), "help");
        //CommandRegistry.registerCommand(new CommandEnd(), "end", "stop");
    }

    public static /* varargs */ void registerCommand(CommandExecutor cmd, String ... commands) {
        int i = 0;
        while (i < commands.length) {
            commands[i] = commands[i].toLowerCase();
            ++i;
        }
        cmds.add(new CommandHolder(commands[0], Arrays.asList((String[])ArrayUtils.subarray((Object[])commands, (int)1, (int)commands.length)), cmd));
    }

    public static void runCommand(String command, String[] args, Terminal writer) {
        for (CommandHolder h : cmds) {
            if (!h.accept(command)) continue;
            h.executor.onCommand(command, writer, args);
            return;
        }
        writer.writeMessage("§cCommand not found. help for more informations");
    }

    public static CopyOnWriteArrayList<CommandHolder> getCommands() {
        return cmds;
    }

    public static void help(String command, Terminal writer) {
        for (CommandHolder h : cmds) {
            if (!h.accept(command)) continue;
            h.executor.printHelp(false);
            return;
        }
        writer.writeMessage("§cCommand not found. Typ help for more informations");
    }

    public static class CommandHolder {
        private String command;
        private List<String> alias;
        private CommandExecutor executor;

        public boolean accept(String is) {
            if (!this.command.equalsIgnoreCase(is) && !this.alias.contains(is.toLowerCase())) {
                return false;
            }
            return true;
        }

        @ConstructorProperties(value={"commands", "alias", "executor"})
        public CommandHolder(String command, List<String> alias, CommandExecutor executor) {
            this.command = command;
            this.alias = alias;
            this.executor = executor;
        }

        public String getCommand() {
            return this.command;
        }

        public List<String> getAlias() {
            return this.alias;
        }

        public CommandExecutor getExecutor() {
            return this.executor;
        }
    }

}

