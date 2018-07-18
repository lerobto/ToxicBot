package com.toxicmenu.discordbot.command.commands.administration;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class RoleCommand extends Command {
    public RoleCommand() {
        super("role", "<@User#XXXX> <Rank>", "No Description");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();

        if (args.length == 2) {
            if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                return null;
            }

            Member target = null;
            Role role = null;

            for (Member members : message.getMentionedMembers()) {
                if(members.getAsMention().equalsIgnoreCase(args[0])) {
                    target = members;

                    break;
                }
            }

            for (Role roles : member.getGuild().getRoles()) {
                if(roles.getAsMention().equalsIgnoreCase(args[1])) {
                    role = roles;

                    break;
                }
            }

            if(target == null) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("No User found with that Mention!")
                        .setFooter("Your search for " + args[0], null);
                final MessageEmbed messageEmbed = embedBuilder.build();

                message.getTextChannel().sendMessage(messageEmbed).complete();
            }

            if(role == null) {
                final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("No Role found with that Mention!")
                        .setFooter("Your search for " + args[1], null);
                final MessageEmbed messageEmbed = embedBuilder.build();

                message.getTextChannel().sendMessage(messageEmbed).complete();
            }

            if(!target.getRoles().contains(role)) {
                try {
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Information - Rank Update")
                            .setFooter("Rank Change from " + message.getAuthor().getName(), null);
                    embedBuilder.addField("Name: ",
                            target.getUser().getName(), false);
                    embedBuilder.addField("Status: ",
                            "Done", false);
                    embedBuilder.addField("Added Rank: ",
                            role.getName(), false);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    //ToxicBot.getTerminal().writeMessage("");

                    Guild guild = member.getGuild();
                    guild.getController().addSingleRoleToMember(target, role).queue();

                    sendPrivateMessage(target.getGuild().getMemberById("234282812818063361").getUser(), messageEmbed);
                    sendPrivateMessage(target.getGuild().getMemberById("279349790045765632").getUser(), messageEmbed);
                } catch (Exception ex) {
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("Information - Rank Update")
                            .setFooter("Rank Change from " + message.getAuthor().getName(), null);
                    embedBuilder.addField("Name: ",
                            target.getUser().getName(), false);
                    embedBuilder.addField("Status: ",
                            "Failed", false);
                    embedBuilder.addField("Try Added Rank: ",
                            role.getName(), false);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    sendPrivateMessage(target.getGuild().getMemberById("234282812818063361").getUser(), messageEmbed);
                    sendPrivateMessage(target.getGuild().getMemberById("279349790045765632").getUser(), messageEmbed);
                }
            } else if(target.getRoles().contains(role)) {
                try {
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Information - Rank Update")
                            .setFooter("Rank Change from " + message.getAuthor().getName(), null);
                    embedBuilder.addField("Name: ",
                            target.getUser().getName(), false);
                    embedBuilder.addField("Status: ",
                            "Done", false);
                    embedBuilder.addField("Removed Rank: ",
                            role.getName(), false);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    //ToxicBot.getTerminal().writeMessage("");

                    Guild guild = member.getGuild();
                    guild.getController().removeSingleRoleFromMember(target, role).queue();

                    sendPrivateMessage(target.getGuild().getMemberById("234282812818063361").getUser(), messageEmbed);
                    sendPrivateMessage(target.getGuild().getMemberById("279349790045765632").getUser(), messageEmbed);
                } catch (Exception ex) {
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("Information - Rank Update")
                            .setFooter("Rank Change from " + message.getAuthor().getName(), null);
                    embedBuilder.addField("Name: ",
                            target.getUser().getName(), false);
                    embedBuilder.addField("Status: ",
                            "Failed", false);
                    embedBuilder.addField("Try Removed Rank: ",
                            role.getName(), false);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    sendPrivateMessage(target.getGuild().getMemberById("234282812818063361").getUser(), messageEmbed);
                    sendPrivateMessage(target.getGuild().getMemberById("279349790045765632").getUser(), messageEmbed);
                }
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