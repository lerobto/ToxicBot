package com.toxicmenu.discordbot.api;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class ToxicUser {

    public static boolean isTeam(Member member, Message message) {
        for(Role role : member.getRoles()) {
            if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Ruski") || (role.getName().equalsIgnoreCase("Owner")))))) {
                return true;
            } else if(role.getName().equalsIgnoreCase("User")) {
                message.getTextChannel().sendMessage("You have no permissions to execute this Command!").complete();
                return false;
            } else {
                message.getTextChannel().sendMessage("You have no permissions to execute this Command!").complete();
                return false;
            }
        }

        return false;
    }
}