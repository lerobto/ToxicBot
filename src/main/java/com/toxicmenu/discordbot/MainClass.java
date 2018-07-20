package com.toxicmenu.discordbot;

import com.toxicmenu.discordbot.api.ToxicChannel;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.log.JettySystemLogger;
import com.toxicmenu.log.SystemLogger;
import com.toxicmenu.terminal.JLineTerminal;
import com.toxicmenu.terminal.VanillaTerminal;
import org.eclipse.jetty.util.log.Log;

import java.util.ArrayList;

public class MainClass {

    public static void main(String[] args) {
        if (System.console() != null) {
            ToxicBot.terminal = new JLineTerminal();
        } else {
            ToxicBot.terminal = new VanillaTerminal();
        }
        ToxicBot.getTerminal().install();
        ToxicBot.logger = new SystemLogger();
        Log.setLog(new JettySystemLogger("jetty"));
        if (!"UTF-8".equalsIgnoreCase(System.getProperty("file.encoding"))) {
            ToxicBot.getTerminal().writeMessage("Changing default file-encoding to UTF-8");
            System.setProperty("file.encoding", "UTF-8");
        }
        new ToxicBot(args);
        ToxicUser.bypass = new ArrayList<>();
        ToxicChannel.whitelist = new ArrayList<>();
    }
}