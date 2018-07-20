package com.toxicmenu.discordbot.api;

import com.toxicmenu.discordbot.ToxicBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ToxicUser {

    @Getter
    public static ArrayList<String> bypass;

    public static boolean isStaff(Member member, Message message) {
        if(checkTeam(member, message, false)) {
            if (ToxicBot.isTest()) {
                if (checkDev(member, message, false)) {
                    return true;
                } else {
                    return false;
                }
            } else if (!ToxicBot.isTest()) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    public static boolean isTeam(Member member, Message message) {
        if(checkTeam(member, message, true)) {
            if(checkDev(member, message, true)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean checkTeam(Member member, Message message, Boolean messages) {
        try {
            for(Role role : member.getRoles()) {
                if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Ruski") || (role.getName().equalsIgnoreCase("Owner")))))) {
                    return true;
                } else if(role.getName().equalsIgnoreCase("User")) {
                    if(messages) {
                        message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                    }
                    return false;
                }
            }
        } catch (Exception ex) {
            ToxicBot.getTerminal().writeMessage(ex.getMessage());
        }

        return false;
    }

    public static boolean checkDev(Member member, Message message, Boolean messages) {
        try {
            if(ToxicBot.isTest()) {
                for(Role role : member.getRoles()) {
                    if(role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Ruski") || (role.getName().equalsIgnoreCase("Owner")))) {
                        return true;
                    } else if(role.getName().equalsIgnoreCase("User")) {
                        if(messages) {
                            message.getTextChannel().sendMessage(MSGS.warn().setDescription("Currently no commands are available because they are still under development or in test mode.").build()).complete();
                        }
                        return false;
                    }
                }
            } else {
                return true;
            }
        } catch (Exception ex) {
            ToxicBot.getTerminal().writeMessage(ex.getMessage());
        }

        return false;
    }

    public static boolean checkAdmin(Member member, Message message) {
        try {
            if(ToxicBot.isTest()) {
                for(Role role : member.getRoles()) {
                    if(role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Ruski") || (role.getName().equalsIgnoreCase("Owner"))))) {
                        return true;
                    } else if(role.getName().equalsIgnoreCase("User")) {
                        message.getTextChannel().sendMessage(MSGS.warn().setDescription("You need to be Admin or higher.").build()).complete();
                        return false;
                    }
                }
            } else {
                return true;
            }
        } catch (Exception ex) {
            ToxicBot.getTerminal().writeMessage(ex.getMessage());
        }

        return false;
    }

    public static void sendPrivateMessage(User user, String message) {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(message).queue();
        });
    }

    public static void sendPrivateMessage(User user, MessageEmbed message) {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(message).queue();
        });
    }
}