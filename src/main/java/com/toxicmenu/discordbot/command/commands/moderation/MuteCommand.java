package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;

import java.awt.*;

public class MuteCommand extends Command {
    public MuteCommand() {
        super("mute", "<@User#XXXX> [Reason]", "This Command can mute users. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length == 1 || args.length == 2) {
            Member member = message.getMember();
            String reason = "No Reason";

            if(args.length == 2) {
                reason = args[1];
            }

            if(!ToxicUser.isTeam(message.getMember(), message)) {
                return null;
            }

            Member target = null;

            for (Member members : message.getMentionedMembers()) {
                if(members.getAsMention().equalsIgnoreCase(args[0])) {
                    target = members;

                    break;
                }
            }

            if(target == null) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("No User found with that Name!")
                        .setFooter("Your search for " + args[0], null);
                final MessageEmbed messageEmbed = embedBuilder.build();

                message.getTextChannel().sendMessage(messageEmbed).complete();
            }

            if(ToxicUser.isStaff(target, message)) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You cannot mute this User!").build()).complete();
                return null;
            }

            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Information - Mute")
                    .setFooter("Muted from " + message.getAuthor().getName(), null);
            embedBuilder.addField("Name: ",
                    target.getUser().getName(), false);
            embedBuilder.addField("Status: ",
                    "Done", false);
            embedBuilder.addField("Duration: ",
                    "Unlimited", false);
            embedBuilder.addField("Reason: ",
                    reason, false);
            final MessageEmbed messageEmbed = embedBuilder.build();

            message.getTextChannel().sendMessage(messageEmbed).complete();

            //ToxicBot.getTerminal().writeMessage("");

            Guild guild = ToxicBot.getJda().getGuildById("431909941138161674");
            guild.getController().addSingleRoleToMember(target, guild.getRoleById("467123966096441384")).queue();

            sendPrivateMessage(target.getUser(), "You have been muted on the ToxicMenu Discord Server!");
            sendPrivateMessage(target.getUser(), "Reason: " + reason);

            return CommandResponse.ACCEPTED;
        } else {
            ToxicUser.isTeam(message.getMember(), message);

            return CommandResponse.SYNTAX_PRINTED;
        }
    }

    public void sendPrivateMessage(User user, String message) {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(message).queue();
        });
    }

    public void sendPrivateMessage(User user, MessageEmbed message) {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(message).queue();
        });
    }
}