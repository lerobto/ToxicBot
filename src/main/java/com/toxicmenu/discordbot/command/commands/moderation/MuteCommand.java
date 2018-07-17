package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;

import java.awt.*;

public class MuteCommand extends Command {
    public MuteCommand() {
        super("toxicmute", "<@User#XXXX> <Reason>", "This Command can mute users. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length == 2) {
            Member member = message.getMember();

            for(Role role : member.getRoles()) {
                if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Owner"))))) {
                    break;
                } else if(role.getName().equalsIgnoreCase("User")) {
                    message.getTextChannel().sendMessage("You have no permissions to execute this Command!").complete();
                }
            }

            Member target = null;

            for (Member members : message.getMentionedMembers()) {
                if(members.getAsMention().equalsIgnoreCase(args[0])) {
                    target = members;

                    break;
                }
            }

            if(target == null) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("No User found with that Name!")
                        .setFooter("Your search for " + args[0], null);
                final MessageEmbed messageEmbed = embedBuilder.build();

                message.getTextChannel().sendMessage(messageEmbed).complete();
            }

            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("New Mute incoming!")
                    .setFooter("Muted from " + message.getAuthor().getName(), null);
            embedBuilder.addField("Name: ",
                    target.getUser().getName(), false);
            embedBuilder.addField("Status: ",
                    "Muted", false);
            embedBuilder.addField("Duration: ",
                    "Unlimited", false);
            embedBuilder.addField("Reason: ",
                    args[1], false);
            final MessageEmbed messageEmbed = embedBuilder.build();

            message.getTextChannel().sendMessage(messageEmbed).complete();

            ToxicBot.getTerminal().writeMessage(member.getGuild().getRoleById("467111273058795521").getName());

            new GuildController(member.getGuild()).addSingleRoleToMember(target, member.getGuild().getRoleById("467111273058795521"));

            return CommandResponse.ACCEPTED;
        } else {
            return CommandResponse.SYNTAX_PRINTED;
        }
    }
}