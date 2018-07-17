package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Role;
import java.util.List;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear", "[How much?]", "This Command can clear the channel for 100 Messages or more. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message event, String[] args) {
        if (args.length != 1) {
            Member member = event.getMember();
            member.getRoles();

            for(Role role : member.getRoles()) {
                if(role.getName().equalsIgnoreCase("Moderators") || (role.getName().equalsIgnoreCase("Admins") || (role.getName().equalsIgnoreCase("Agent") || (role.getName().equalsIgnoreCase("Owner"))))) {
                    break;
                } else if(role.getName().equalsIgnoreCase("User")) {
                    event.getTextChannel().sendMessage("You have no permissions to execute this Command!").complete();
                }
            }

            try {
                MessageHistory history = new MessageHistory(event.getTextChannel());
                List<Message> msgs;
                if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
                    try {
                        while (true) {
                            msgs = history.retrievePast(1).complete();
                            msgs.get(0).delete().queue();
                        }
                    } catch (Exception ex) {
                        //Nothing
                    }
                }else if (args.length < 1 || (args.length > 0 ? Integer.valueOf(args[0]) : 1) == 1 && (args.length > 0 ? Integer.valueOf(args[0]) : 1) < 2) {
                    msgs = history.retrievePast(2).complete();
                    msgs.get(0).delete().queue();
                } else if (Integer.valueOf(args[0]) <= 100) {
                    msgs = history.retrievePast(Integer.valueOf(args[0])).complete();
                    event.getTextChannel().deleteMessages(msgs).queue();
                }
            } catch (Exception e) {
            }

            return CommandResponse.ACCEPTED;
        } else {
            return CommandResponse.SYNTAX_PRINTED;
        }
    }
}