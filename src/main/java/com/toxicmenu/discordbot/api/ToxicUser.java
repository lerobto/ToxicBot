package com.toxicmenu.discordbot.api;

import com.toxicmenu.discordbot.ToxicBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class ToxicUser {

    public static boolean isTeam(Member member, Message message) {
        if(checkTeam(member, message)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkTeam(Member member, Message message) {
        try {
            for(Role role : member.getRoles()) {
                if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Ruski") || (role.getName().equalsIgnoreCase("Owner")))))) {
                    return true;
                } else if(role.getName().equalsIgnoreCase("User")) {
                    ToxicBot.getTerminal().writeMessage(member.getUser().getName() + " has no Permission to use an Command.");
                    message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                    return false;
                }
            }
        } catch (Exception ex) {
            ToxicBot.getTerminal().writeMessage(ex.getMessage());
        }

        return false;
    }
}