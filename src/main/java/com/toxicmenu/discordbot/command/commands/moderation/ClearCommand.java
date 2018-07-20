package com.toxicmenu.discordbot.command.commands.moderation;

import com.toxicmenu.discordbot.api.MSGS;
import com.toxicmenu.discordbot.api.ToxicChannel;
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
        if (!ToxicUser.isTeam(message.getMember(), message)) {
            return null;
        }

        if (ToxicChannel.checkWhitelist(message.getTextChannel().getId())) {
            message.getTextChannel().sendMessage(MSGS.error().setDescription("You cannot clear messages in this Channel!").build()).complete();
            return null;
        }

        if (args[0].equalsIgnoreCase("bypass")) {
            if(ToxicUser.checkAdmin(message.getMember(), message)) {
                ToxicUser.sendPrivateMessage(message.getAuthor(), MSGS.error().setDescription("Sorry! \n We can't bypass you because we had an error!").build());
                return CommandResponse.ACCEPTED;

                /*try {
                    if(!ToxicUser.getBypass().contains(message.getAuthor().getId())) {
                        ToxicUser.getBypass().add(message.getAuthor().getId());
                        ToxicUser.sendPrivateMessage(message.getAuthor(), MSGS.success().setDescription("You have been added to the bypass list!").build());
                    } else {
                        ToxicUser.sendPrivateMessage(message.getAuthor(), MSGS.error().setDescription("You are already on the bypass list!").build());
                    }

                    return CommandResponse.ACCEPTED;
                } catch (Exception ex) {
                    ToxicUser.sendPrivateMessage(message.getAuthor(), MSGS.warn().setDescription("Ooops an error occured! \n Error: `" + ex.getMessage() + "` \n Please report them to our Team!").build());
                }*/
            }
        }

        if(args.length == 0) {
            return CommandResponse.SYNTAX_PRINTED;
        }

        try {
            MessageHistory history = new MessageHistory(message.getTextChannel());
            List<Message> msgs;
            if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
                if(ToxicUser.checkAdmin(message.getMember(), message)) {
                    return null;
                }

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

            } else if (args.length < 1 || (args.length > 0 ? getInt(args[0]) : 1) == 1 && (args.length > 0 ? getInt(args[0]) : 1) < 2) {
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

            } else if (args.length == 2) {
                // 24/03/2013 21:54
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                StringBuilder builder = new StringBuilder();

                for (String arg : args) {
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

            } else if (getInt(args[0]) <= 100) {
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
            } else if (getInt(args[0]) > getInt("100")) {
                if (ToxicUser.getBypass().contains(message.getAuthor().getId())) {
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

                    return CommandResponse.ACCEPTED;
                }

                message.getTextChannel().sendMessage(MSGS.error().setDescription("").setDescription("You need to be bypass to clear more than 100 Messages!").addField("How to bypass?", "Admins+ can be bypass with `!clear bypass` \n" +
                        "if you have write that command, you able to clear 100+ messages", false).build()).queue();
            } else {
                message.getTextChannel().sendMessage(MSGS.error()
                        .addField("Error Type", "Message value out of bounds.", false)
                        .addField("Description", "The entered number if messages can not be more than 255 messages!", false)
                        .addField("How to bypass?", "Admins+ can be bypass with `!clear bypass` \n" +
                                "if you have write that command, you able to clear 100+ messages", false)
                        .build()
                ).queue();
            }


        } catch (Exception e) {
            message.getTextChannel().sendMessage(MSGS.error()
                    .addField("Error Type", e.getCause().toString(), false)
                    .addField("Message", e.getMessage(), false)
                    .build()
            ).queue();
        }
        return CommandResponse.ACCEPTED;
    }

    private int getInt(String arg) {

        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            return 0;
        }
    }
}