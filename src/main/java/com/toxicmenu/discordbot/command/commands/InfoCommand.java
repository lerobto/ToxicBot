package com.toxicmenu.discordbot.command.commands;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import java.awt.*;

public class InfoCommand extends Command {
    public InfoCommand() {
        super("info", "", "No Description");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length == 0) {
            MessageEmbed messageEmbed = MSGS.block().setColor(Color.BLUE).addField("Name: ", "`ToxicBot`", false).addField("Version: ", "`" + ToxicBot.getVersion() + "`", false).addField("Last Build: ", "`" + ToxicBot.getLastBuild() + "`", false).build();

            message.getTextChannel().sendMessage(messageEmbed).complete();

            return CommandResponse.ACCEPTED;
        } else {
            return CommandResponse.SYNTAX_PRINTED;
        }
    }
}
