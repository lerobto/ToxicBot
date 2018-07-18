package com.toxicmenu.discordbot.command.commands.administration;

import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class StaffCommand extends Command {
    public StaffCommand() {
        super("staff", "<Roles|GiveRole>", "No Description");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();

        if (args.length == 1) {
            if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                return null;
            }

            if(args[0].equalsIgnoreCase("Roles")) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE).setTitle("Staff Ranks/Roles - Informations")
                        .setFooter("Server: " + member.getGuild().getName(), null);
                addInfoToMessageEmbed(embedBuilder);
                final MessageEmbed messageEmbed = embedBuilder.build();

                sendPrivateMessage(member.getUser(), messageEmbed);
            } else if(args[0].equalsIgnoreCase("GiveRole")) {
                Role role = member.getGuild().getRoleById("469134456838684693");
                Integer staffs = 0;

                for (Member members : member.getGuild().getMembers()) {
                    if(ToxicUser.isStaff(members, message)) {
                        members.getGuild().getController().addSingleRoleToMember(members, role).queue();
                        staffs += 1;
                    }
                }

                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE).setTitle("Update Ranks - Information")
                        .setFooter("Server: " + member.getGuild().getName(), null);
                embedBuilder.addField("Changed Ranks for: ", "`" + staffs + " Users`", false);
                final MessageEmbed messageEmbed = embedBuilder.build();

                sendPrivateMessage(member.getUser(), messageEmbed);
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

    private void addInfoToMessageEmbed(EmbedBuilder embedBuilder) {
        embedBuilder.addField("Owner", "", false);
        embedBuilder.addField("Ruski", "", false);
        embedBuilder.addField("Agent", "", false);
        embedBuilder.addField("Admins", "", false);
        embedBuilder.addField("Moderators", "", false);
        embedBuilder.addField("\uD83E\uDD14SUPPORT\uD83E\uDD14", "", false);
        embedBuilder.addField("SUPPORT {NO TAGS}", "", false);
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