package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class BanCommand extends Command {
    public BanCommand() {
        super("ban", "<@User#XXXX> [No Reason]", "This Command can ban users. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length == 1 || args.length == 2) {
            Member member = message.getMember();
            String reason = "No Reason";

            if(!ToxicUser.isTeam(member, message)) {
                return null;
            }

            for(Role role : member.getRoles()) {
                if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Ruski") || (role.getName().equalsIgnoreCase("Owner")))))) {
                    break;
                } else if(role.getName().equalsIgnoreCase("User")) {
                    message.getTextChannel().sendMessage("You have no permissions to execute this Command!").complete();
                    return null;
                } else {
                    message.getTextChannel().sendMessage("You have no permissions to execute this Command!").complete();
                    return null;
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
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("No User found with that Name!")
                        .setFooter("Your search for " + args[0], null);
                final MessageEmbed messageEmbed = embedBuilder.build();

                message.getTextChannel().sendMessage(messageEmbed).complete();
            }

            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Information - Ban")
                    .setFooter("Ban from " + message.getAuthor().getName(), null);
            embedBuilder.addField("Name: ",
                    target.getUser().getName(), false);
            embedBuilder.addField("Status: ",
                    "Done", false);
            embedBuilder.addField("Duration: ",
                    "30 Days", false);
            embedBuilder.addField("Reason: ",
                    reason, false);
            final MessageEmbed messageEmbed = embedBuilder.build();

            message.getTextChannel().sendMessage(messageEmbed).complete();

            //ToxicBot.getTerminal().writeMessage("");

            sendPrivateMessage(target.getUser(), "You have been banned from the ToxicMenu Discord Server!");
            sendPrivateMessage(target.getUser(), "Reason: " + reason);
            sendPrivateMessage(target.getUser(), "**You will be unbanned in 30 Days**");

            Guild guild = ToxicBot.getJda().getGuildById("431909941138161674");
            guild.getController().ban(target, 30).queue();

            return CommandResponse.ACCEPTED;
        } else {
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