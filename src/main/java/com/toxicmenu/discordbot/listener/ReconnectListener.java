package com.toxicmenu.discordbot.listener;

import com.toxicmenu.discordbot.ToxicBot;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReconnectListener extends ListenerAdapter {

    @Override
    public void onReconnect(ReconnectedEvent event) {
        ToxicBot.getTerminal().writeMessage("Reconnect...");
    }
}