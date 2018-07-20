package com.toxicmenu.discordbot.command.commands.administration;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import com.toxicmenu.discordbot.mysql.UserAPI;
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

        if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
            message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
            return null;
        }

        if (args.length == 1) {
            if(args[0].equalsIgnoreCase("Roles")) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.WHITE).setTitle("Staff Ranks/Roles - Informations")
                        .setFooter("Server: " + member.getGuild().getName(), null);
                addInfoToMessageEmbed(embedBuilder);
                final MessageEmbed messageEmbed = embedBuilder.build();

                sendPrivateMessage(member.getUser(), messageEmbed);
            } else if(args[0].equalsIgnoreCase("reimport")) {
                try {
                    Integer imports = 0;

                    for (Member members : member.getGuild().getMembers()) {
                        UserAPI user = new UserAPI(members.getUser().getId());
                        if(!user.userExists()) {
                            user.create();

                            //sendPrivateMessage(members.getUser(), MSGS.success().setDescription("Hey **" + members.getUser().getName() + "**! \n We have add you in our Data System! \n If you leave we don't delete your data. \n You have a question? \n Ask us in #\uD83E\uDD14support\uD83E\uDD14").build());
                            imports += 1;
                        }
                    }

                    sendPrivateMessage(member.getGuild().getMemberById("234282812818063361").getUser(), MSGS.success().setDescription("Hey **Paul**! \n I have import `" + imports + "` in our Database! \n So if users are muted they can be rejoined, \nbut they are get the Muted role again!").build());
                    sendPrivateMessage(member.getGuild().getMemberById("279349790045765632").getUser(), MSGS.success().setDescription("Hey **John**! \n I have import `" + imports + "` in our Database! \n So if users are muted they can be rejoined, \nbut they are get the Muted role again!").build());
                } catch (Exception ex) {
                    sendPrivateMessage(member.getUser(), MSGS.warn().setDescription("Ooops an error occured! \n Error: `" + ex.getMessage() + "` \n Please report them to our Team!").build());
                }
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