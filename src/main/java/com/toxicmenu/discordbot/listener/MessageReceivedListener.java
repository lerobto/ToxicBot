package com.toxicmenu.discordbot.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getMessage().getTextChannel().getId().equalsIgnoreCase("469197809048879124")) {
            e.getMessage().delete().queue();
        }
    }
}