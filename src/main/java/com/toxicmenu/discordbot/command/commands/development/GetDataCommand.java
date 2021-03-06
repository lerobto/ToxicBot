package com.toxicmenu.discordbot.command.commands.development;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.util.List;

public class GetDataCommand extends Command {
    public GetDataCommand() {
        super("getdata", "", "This Command can be get Data like IDs from User");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();

        if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
            message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
            return null;
        }

        if (args.length == 0) {
            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE).setTitle("Profile Data")
                    .setFooter("User: " + message.getAuthor().getName(), null);
            addInfoToMessageEmbed(embedBuilder, message.getAuthor());
            final MessageEmbed messageEmbed = embedBuilder.build();

            sendPrivateMessage(message.getAuthor(), messageEmbed);

            /*for(Role role : message.getMember().getGuild().getRoles()) {
                if(role.getName().equals("Muted")) {
                    sendPrivateMessage(message.getAuthor(), "LongID: " + role.getIdLong());
                    sendPrivateMessage(message.getAuthor(), "ID: " + role.getId());
                }
            }*/

            return CommandResponse.ACCEPTED;
        } else {
            return CommandResponse.SYNTAX_PRINTED;
        }
    }

    private void addInfoToMessageEmbed(EmbedBuilder embedBuilder, User user) {
        embedBuilder.addField("Name: ",
                "`" + user.getName() + "`", false);
        embedBuilder.addField("ID: ",
                "`" + user.getId() + "`", false);
        embedBuilder.addField("Mention: ",
                user.getAsMention(), false);
        embedBuilder.addField("Creation Time: ",
                "`" + user.getCreationTime().toString() + "`", false);
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