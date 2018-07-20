package com.toxicmenu.discordbot.command.commands.development;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class GetIdCommand extends Command {

    public GetIdCommand() {
        super("getdataforrank", "<Rank>", "This Command can be get Data like IDs from User");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();
        if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
            message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
            return null;
        }

        if (args.length == 1) {
            try {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE).setTitle("Rank Data")
                        .setFooter("Rank: " + args[0], null);
                for (Role role : ToxicBot.getJda().getRoles()) {
                    if(role.getName().equalsIgnoreCase(args[0])) {
                        addInfoToMessageEmbed(embedBuilder, role);
                    }
                }
                final MessageEmbed messageEmbed = embedBuilder.build();
                sendPrivateMessage(message.getAuthor(), messageEmbed);
            } catch (Exception e) {
                sendPrivateMessage(message.getAuthor(), MSGS.error().setDescription("We had some errors to give you Informations about the Rank `" + args[0] + "`").build());
            }

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

    private void addInfoToMessageEmbed(EmbedBuilder embedBuilder, Role role) {
        embedBuilder.addField("Rank Name: ",
                "`" + role.getName() + "`", false);
        embedBuilder.addField("ID: ",
                "`" + role.getId() + "`", false);
        embedBuilder.addField("LongID: ",
                "`" + String.valueOf(role.getIdLong()) + "`", false);
        embedBuilder.addField("Mention: ",
                role.getAsMention(), false);
        embedBuilder.addField("Creation Time: ",
                "`" + role.getCreationTime().toString() + "`", false);
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