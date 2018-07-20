package com.toxicmenu.discordbot.command.commands.administration;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class TestCommand extends Command {
    public TestCommand() {
        super("test", "", "No Description");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();
        if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
            message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
            return null;
        }

        if (args.length == 1) {
            if(args[0].equalsIgnoreCase("Roles")) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE);
                if(ToxicBot.isTest()) {
                    embedBuilder.setColor(Color.GREEN);
                    embedBuilder.setDescription("You have disabled the Testmode, all Commands are now available for team members and Users");
                } else if(!ToxicBot.isTest()) {
                    embedBuilder.setColor(Color.RED);
                    embedBuilder.setDescription("You have enabled the Testmode, all Commands are restricted for team members and Users");
                }
                final MessageEmbed messageEmbed = embedBuilder.build();

                sendPrivateMessage(member.getUser(), messageEmbed);
            }

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
