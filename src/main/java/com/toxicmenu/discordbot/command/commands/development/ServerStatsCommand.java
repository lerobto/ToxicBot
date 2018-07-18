package com.toxicmenu.discordbot.command.commands.development;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class ServerStatsCommand extends Command {
    public ServerStatsCommand() {
        super("serverstats", "", "This Command can be get Data like IDs from User");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();

        if (args.length == 0) {
            if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                return null;
            }

            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE).setTitle("Server Informations")
                    .setFooter("Server: " + member.getGuild().getName(), null);
            addInfoToMessageEmbed(embedBuilder, member.getGuild());
            final MessageEmbed messageEmbed = embedBuilder.build();

            message.getTextChannel().sendMessage(messageEmbed).complete();

            /*for(Role role : message.getMember().getGuild().getRoles()) {
                if(role.getName().equals("Muted")) {
                    sendPrivateMessage(message.getAuthor(), "LongID: " + role.getIdLong());
                    sendPrivateMessage(message.getAuthor(), "ID: " + role.getId());
                }
            }*/

            return CommandResponse.ACCEPTED;
        } else {
            if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                return null;
            }

            return CommandResponse.SYNTAX_PRINTED;
        }
    }

    private void addInfoToMessageEmbed(EmbedBuilder embedBuilder, Guild guild) {
        embedBuilder.addField("Name: ", "`" + guild.getName() + "`", false);
        embedBuilder.addField("ID: ", "`" + guild.getId() + "`", false);
        embedBuilder.addField("Owner: ", guild.getOwner().getAsMention(), false);
        embedBuilder.addField("Users: ", "`" + String.valueOf(guild.getMembers().size()) + "`", false);
        embedBuilder.addField("Categories: ", "`" + String.valueOf(guild.getCategories().size()) + "`", false);
        embedBuilder.addField("Text Channels: ", "`" + String.valueOf(guild.getTextChannels().size()) + "`", false);
        embedBuilder.addField("Voice Channels: ", "`" + String.valueOf(guild.getVoiceChannels().size()) + "`", false);
        embedBuilder.addField("Emoij's: ", "`" + guild.getEmotes().size() + "`", false);
        embedBuilder.addField("Creation Time: ", "`" + guild.getCreationTime().toString() + "`", false);
    }
}