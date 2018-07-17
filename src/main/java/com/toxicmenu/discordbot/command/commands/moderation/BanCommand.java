package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class BanCommand extends Command {
    public BanCommand() {
        super("ban", "<@User#XXXX>", "This Command can ban users. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length != 1) {
            Member member = message.getMember();
            member.getRoles();

            for(Role role : member.getRoles()) {
                if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Owner"))))) {
                    break;
                } else {
                    message.getTextChannel().sendMessage("You have no permissions to execute this Command!");
                }
            }

            return CommandResponse.ACCEPTED;
        } else {
            return CommandResponse.SYNTAX_PRINTED;
        }
    }
}