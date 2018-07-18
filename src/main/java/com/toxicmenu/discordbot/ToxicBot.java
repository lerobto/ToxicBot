package com.toxicmenu.discordbot;

import com.toxicmenu.discordbot.command.CommandRegistry;
import com.toxicmenu.discordbot.command.commands.CommandListCommand;
import com.toxicmenu.discordbot.command.commands.administration.RoleCommand;
import com.toxicmenu.discordbot.command.commands.administration.StaffCommand;
import com.toxicmenu.discordbot.command.commands.development.GetDataCommand;
import com.toxicmenu.discordbot.command.commands.development.GetIdCommand;
import com.toxicmenu.discordbot.command.commands.development.ServerStatsCommand;
import com.toxicmenu.discordbot.command.commands.moderation.*;
import com.toxicmenu.discordbot.command.impl.DefaultCommandRegistry;
import com.toxicmenu.discordbot.listener.JoinEvent;
import com.toxicmenu.discordbot.listener.ReconnectListener;
import com.toxicmenu.log.SystemLogger;
import com.toxicmenu.terminal.Terminal;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.apache.commons.cli.*;

public class ToxicBot {
    @Getter
    protected static Terminal terminal;
    @Getter
    protected static SystemLogger logger;
    @Getter
    private static String version = "v1.0";
    private static boolean closed = false;
    private static Object closeLock;
    @Getter
    @Setter
    private static JDA jda;
    @Getter
    private static ToxicBot instance;
    @Getter
    private CommandRegistry commandRegistry;
    @Getter
    private static boolean debug;
    @Getter
    private static boolean devMode;
    @Getter
    private static long startTime;

    public static void shutdown() {
        Object object = closeLock;
        synchronized (object) {
            if (closed) {
                return;
            }
            closed = true;
        }
        getJda().shutdown();
        getTerminal().writeMessage("The Bot is stopping now...");
    }

    static {
        startTime = System.currentTimeMillis();
        closeLock = new Object();
        closed = false;
    }

    public ToxicBot(String[] args) {
        commandRegistry = new DefaultCommandRegistry();

        instance = this;
        version = "v1.0"; //TODO: Change version every Update
        CommandLine cmd;
        Options options = new Options();
        //options.addOption(Option.builder().argName("COMMAND_TEMPLATE").longOpt("COMMAND_TEMPLATE").desc("COMMAND_DESCRIPTION").hasArg(false).required(false).build());
        DefaultParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e) {
            getTerminal().writeMessage(e.getMessage());
            formatter.printHelp("ToxicBot", options);
            System.exit(1);
            return;
        }

        getTerminal().writeMessage("___________          .__      __________        __   ");
        getTerminal().writeMessage("\\__    ___/______  __|__| ____\\______   \\ _____/  |_ ");
        getTerminal().writeMessage("  |    | /  _ \\  \\/  /  |/ ___\\|    |  _//  _ \\   __\\");
        getTerminal().writeMessage("  |    |(  <_> >    <|  \\  \\___|    |   (  <_> )  |  ");
        getTerminal().writeMessage("  |____| \\____/__/\\_ \\__|\\___  >______  /\\____/|__|  ");
        getTerminal().writeMessage("                    \\/       \\/       \\/             ");
        getTerminal().writeMessage("Created by @ToxicJohn#2091 and @єℓяσвтσѕѕσнη#8992");

        Runtime.getRuntime().addShutdownHook(new Thread(ToxicBot::shutdown));

        try {
            JDABuilder bot = new JDABuilder(AccountType.BOT);
            bot.setToken("NDY3MDg4MDkzMDMyMjE4NjQ0.DilhMw.OPu2a1HaLUhdpTsjw_KaO5QJHzk");

            bot.setGame(Game.streaming("Play with ToxicMenu 1.8", "https://www.twitch.tv/ToxicJohnTV"));

            JDA jda = bot.buildAsync();
            jda.setAutoReconnect(true);

            setJda(jda);
        } catch (Exception e) {
            getTerminal().writeMessage("Error: " + e.getMessage());
        }

        if (getJda() == null) {
            getTerminal().writeMessage("JDA is null. Shutting down...");
            System.exit(0);
        }

        getJda().addEventListener(this.commandRegistry);
        getJda().addEventListener(new JoinEvent());
        getJda().addEventListener(new ReconnectListener());
        registerCommands();
    }

    private void registerCommands() {
        final CommandListCommand commandListCommand = new CommandListCommand();
        this.commandRegistry.registerCommand(commandListCommand);
        this.commandRegistry.registerCommand(new GetDataCommand());
        this.commandRegistry.registerCommand(new GetIdCommand());
        this.commandRegistry.registerCommand(new ClearCommand());
        this.commandRegistry.registerCommand(new BanCommand());
        this.commandRegistry.registerCommand(new MuteCommand());
        this.commandRegistry.registerCommand(new UnbanCommand());
        this.commandRegistry.registerCommand(new UnmuteCommand());
        this.commandRegistry.registerCommand(new ServerStatsCommand());
        this.commandRegistry.registerCommand(new RoleCommand());
        this.commandRegistry.registerCommand(new StaffCommand());
        commandListCommand.initialize();
    }
}