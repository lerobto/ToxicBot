package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicUser;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Role;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear", "[How much?]", "This Command can clear the channel for 100 Messages or more. Only usable as Moderator");
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        if (args.length == 1) {
            Member member = message.getMember();
            member.getRoles();

            if(!ToxicUser.isTeam(message.getMember(), message)) {
                return null;
            }

            try {
                MessageHistory history = new MessageHistory(message.getTextChannel());
                List<Message> msgs;
                if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
                    try {
                        while (true) {
                            msgs = history.retrievePast(1).complete();
                            msgs.get(0).delete().queue();
                        }
                    } catch (Exception ex) {
                        //Nichts tun
                    }

                    Message answer = message.getTextChannel().sendMessage(MSGS.success().setDescription(
                            "Successfully deleted all messages!"
                    ).build()).complete();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            answer.delete().queue();
                        }
                    }, 3000);

                }else if (args.length < 1 || (args.length > 0 ? getInt(args[0]) : 1) == 1 && (args.length > 0 ? getInt(args[0]) : 1) < 2) {
                    msgs = history.retrievePast(2).complete();
                    msgs.get(0).delete().queue();

                    Message answer = message.getTextChannel().sendMessage(MSGS.success().setDescription(
                            "Successfully deleted last message!"
                    ).build()).complete();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            answer.delete().queue();
                        }
                    }, 3000);

                } else if(args.length == 2) {
                    // 24/03/2013 21:54
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    StringBuilder builder = new StringBuilder();

                    for (String arg: args) {
                        builder.append(" " + arg);
                    }

                    try {
                        Date date = simpleDateFormat.parse(builder.toString());

                        boolean weiter = true;
                        try {
                            while (weiter) {
                                msgs = history.retrievePast(1).complete();
                                if (date.before(Date.from(msgs.get(0).getCreationTime().toZonedDateTime().toInstant()))) {
                                    msgs.get(0).delete().queue();
                                } else {
                                    weiter = false;
                                }

                            }

                            Message answer = message.getTextChannel().sendMessage(MSGS.success().setDescription(
                                    "Successfully deleted " + args[0] + " messages!"
                            ).build()).complete();

                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    answer.delete().queue();
                                }
                            }, 3000);
                        } catch (Exception ex) {
                            //Nichts tun
                        }
                    } catch (ParseException ex) {
                        message.getTextChannel().sendMessage(MSGS.error()
                                .addField("Error Type", "Wrong Timeformat.", false)
                                .addField("Description", "Pleas enter the Time in the right Timeformat:\n" + simpleDateFormat.format(new Date()), false)
                                .build()
                        ).queue();
                    }

                } else if (getInt(args[0]) <= 255) {
                    msgs = history.retrievePast(getInt(args[0])).complete();
                    message.getTextChannel().deleteMessages(msgs).queue();

                    Message answer = message.getTextChannel().sendMessage(MSGS.success().setDescription(
                            "Successfully deleted " + args[0] + " messages!"
                    ).build()).complete();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            answer.delete().queue();
                        }
                    }, 3000);
                } else {
                    message.getTextChannel().sendMessage(MSGS.error()
                            .addField("Error Type", "Message value out of bounds.", false)
                            .addField("Description", "The entered number if messages can not be more than 255 messages!", false)
                            .build()
                    ).queue();
                }


            } catch (Exception e) {
                message.getTextChannel().sendMessage(MSGS.error()
                        .addField("Error Type", e.getLocalizedMessage(), false)
                        .addField("Message", e.getMessage(), false)
                        .build()
                ).queue();
            }

            return CommandResponse.ACCEPTED;
        } else {
            if(!ToxicUser.isTeam(message.getMember(), message)) {
                return null;
            }

            return CommandResponse.SYNTAX_PRINTED;
        }
    }

    private int getInt(String arg) {

        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            return 0;
        }
    }
}