package com.toxicmenu.discordbot.command.commands.administration;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicGenerator;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import com.toxicmenu.discordbot.mysql.LicenseAPI;
import com.toxicmenu.discordbot.utils.TimeManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class LicenseCommand extends Command {
    public LicenseCommand() {
        super("license", "info <@User#XXXX>", "No Description");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        Member member = message.getMember();

        if (args.length == 2) {
            if(!(member.getUser().getId().equalsIgnoreCase("234282812818063361") || (member.getUser().getId().equalsIgnoreCase("279349790045765632")))) {
                message.getTextChannel().sendMessage(MSGS.error().setDescription("You have no permissions to execute this Command!").build()).complete();
                return null;
            }

            if(args[0].equalsIgnoreCase("info")) {
                Member target = null;

                for (Member members : message.getMentionedMembers()) {
                    if(members.getAsMention().equalsIgnoreCase(args[1])) {
                        target = members;
                        break;
                    }
                }

                if(target == null) {
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setDescription("No User found with that Name!")
                            .setFooter("Your search for " + args[1], null);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    message.getTextChannel().sendMessage(messageEmbed).complete();
                }

                try {
                    LicenseAPI license = new LicenseAPI(target.getUser().getId());

                    if(license.userExists()) {
                        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("License - Informations")
                                .setFooter("Database: ToxicMenu.com", null);
                        addInfoToMessageEmbed(embedBuilder, target, license);
                        final MessageEmbed messageEmbed = embedBuilder.build();

                        sendPrivateMessage(member.getUser(), messageEmbed);
                    } else {
                        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("License - Informations")
                                .setDescription("The specified user does not have a license!")
                                .setFooter("Database: ToxicMenu.com", null)
                                .setFooter("Searched user: " + target.getUser().getName(), null);
                        final MessageEmbed messageEmbed = embedBuilder.build();

                        sendPrivateMessage(member.getUser(), messageEmbed);
                    }
                } catch(Exception ex) {
                    sendPrivateMessage(member.getUser(), MSGS.warn().setDescription("Ooops an error occured! \n Error: `" + ex.getMessage() + "`").build());
                }
            } else if(args[0].equalsIgnoreCase("generate")) {
                Member target = null;

                for (Member members : message.getMentionedMembers()) {
                    if(members.getAsMention().equalsIgnoreCase(args[1])) {
                        target = members;
                        break;
                    }
                }

                if(target == null) {
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setDescription("No User found with that Name!")
                            .setFooter("Your search for " + args[1], null);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    message.getTextChannel().sendMessage(messageEmbed).complete();
                }

                try {
                    LicenseAPI license = new LicenseAPI(target.getUser().getId());
                    String licensekey = ToxicGenerator.generateLicense() + "-" + ToxicGenerator.generateLicense() + "-" + ToxicGenerator.generateLicense() + "-" + ToxicGenerator.generateLicense();

                    license.createLicense(licensekey, "valid", String.valueOf(System.currentTimeMillis()));
                    final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("License Generated - Informations")
                            .setFooter("Database: ToxicMenu.com", null);
                    addInfoToMessageEmbed(embedBuilder, target, license);
                    final MessageEmbed messageEmbed = embedBuilder.build();

                    sendPrivateMessage(member.getUser(), messageEmbed);
                } catch(Exception ex) {
                    sendPrivateMessage(member.getUser(), MSGS.warn().setDescription("Ooops an error occured! \n Error: `" + ex.getMessage() + "` \n Please report them to our Team!").build());
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

    private void addInfoToMessageEmbed(EmbedBuilder embedBuilder, Member member, LicenseAPI license) {
        if(!license.get("status").equalsIgnoreCase("valid")) {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setFooter("The License is not valid!", null);
        } else if(license.get("status").equalsIgnoreCase("valid")) {
            embedBuilder.setFooter("The License is valid!", null);
        }

        embedBuilder.addField("Name: ", "`" + member.getUser().getName() + "`", true);
        embedBuilder.addField("License: ", "`" + license.get("licensekey") + "`", true);
        embedBuilder.addField("Status: ", "`" + license.get("status") + "`", true);
        embedBuilder.addField("Created on: ", "`" + TimeManager.getDate(Long.valueOf(license.get("createdate"))) + "`", true);
        if(!license.get("lastuse").startsWith("Never")) {
            embedBuilder.addField("Last use on: ", "`" + TimeManager.getDate(Long.valueOf(license.get("lastuse"))) + "`", true);
        } else {
            embedBuilder.addField("Last use on: ", "`Never used`", true);
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
