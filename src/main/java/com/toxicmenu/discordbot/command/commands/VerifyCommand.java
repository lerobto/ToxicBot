package com.toxicmenu.discordbot.command.commands;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicGenerator;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import com.toxicmenu.discordbot.mysql.LicenseAPI;
import com.toxicmenu.discordbot.utils.TimeManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class VerifyCommand extends Command {
    public VerifyCommand() {
        super("verify", "", "No Description");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();

        if (args.length == 0) {
            try {
                message.delete().queue();

                if(!message.getTextChannel().getId().equalsIgnoreCase("469197809048879124")) {
                    sendPrivateMessage(member.getUser(), MSGS.error().setDescription("Please use the `!verify` Command only\n" +
                            "in **#verification**!").build());
                    return null;
                }

                if(member.getRoles().contains(member.getGuild().getRoleById("469193894211354645"))) {
                    sendPrivateMessage(member.getUser(), MSGS.error().setDescription("You are already verified!\n" +
                            "If you want to verify yourself on another account,\n" +
                            "then please email us at `buyer@ToxicMenu.com`!\n").build());
                    return null;
                }

                LicenseAPI license = new LicenseAPI(member.getUser().getId());

                if (license.userExists()) {
                    if (license.get("status").equalsIgnoreCase("valid")) {
                        sendPrivateMessage(member.getUser(), MSGS.success().setDescription("Your License is valid and you have get the Role **buyer**").build());
                        member.getGuild().getController().addSingleRoleToMember(member, member.getGuild().getRoleById("469193894211354645")).queue();
                    } else if (license.get("status").equalsIgnoreCase("invalid")) {
                        sendPrivateMessage(member.getUser(), MSGS.warn().setDescription("Your License is invalid so you can't get the **buyer** Role.").build());
                        member.getGuild().getController().removeSingleRoleFromMember(member, member.getGuild().getRoleById("469193894211354645")).queue();
                    }
                } else {
                    sendPrivateMessage(member.getUser(), MSGS.error().setDescription("You need an License to reserve the **buyer** Role. \n Look in our FAQ to see where you can buy the Menu!").build());
                }
            } catch (Exception ex) {
                sendPrivateMessage(member.getUser(), MSGS.warn().setDescription("Ooops an error occured! \n Error: `" + ex.getMessage() + "` \n Please report them to our Team!").build());
            }

            return CommandResponse.ACCEPTED;
        } else {
            if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                return null;
            }

            return CommandResponse.SYNTAX_PRINTED;
        }
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
