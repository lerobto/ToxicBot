package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        super("unban", "<@User#XXXX>", "This Command can unban users. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length == 1 || args.length == 2) {
            Member member = message.getMember();

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

            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Information - Unbanned")
                    .setFooter("Unbanned from " + message.getAuthor().getName(), null);
            embedBuilder.addField("Name: ",
                    target.getUser().getName(), false);
            embedBuilder.addField("Status: ",
                    "Done", false);
            final MessageEmbed messageEmbed = embedBuilder.build();

            message.getTextChannel().sendMessage(messageEmbed).complete();

            //ToxicBot.getTerminal().writeMessage("");

            sendPrivateMessage(target.getUser(), "You have been unbanned from the ToxicMenu Discord Server!");

            Guild guild = ToxicBot.getJda().getGuildById("431909941138161674");
            guild.getController().unban(target.getUser()).queue();

            return CommandResponse.ACCEPTED;
        } else {
            if(!ToxicUser.isTeam(message.getMember(), message)) {
                return null;
            }

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