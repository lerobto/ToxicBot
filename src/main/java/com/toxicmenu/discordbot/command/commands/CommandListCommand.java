package com.toxicmenu.discordbot.command.commands;

import com.toxicmenu.discordbot.Constants;
import com.toxicmenu.discordbot.ToxicBot;
import com.toxicmenu.discordbot.command.Command;
import com.toxicmenu.discordbot.command.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class CommandListCommand extends Command {
    private LinkedList<MessageEmbed> cachedMessageEmbedMap = new LinkedList<>();

    private Map<Message, Integer> registeredHelpCommandMessages = new HashMap<>();
    private static final String ARROW_RIGHT_EMOTE = new String(new byte[]{-30, -98, -95}, StandardCharsets.UTF_8);
    private static final String ARROW_LEFT_EMOTE = new String(new byte[]{-30, -84, -123}, StandardCharsets.UTF_8);

    private static final int ITEM_AMOUNT_PER_SITE = 4;

    public CommandListCommand() {
        super("commandlist", "", "Gives you an overview of all commands.");
    }

    public void initialize() {
        ToxicBot.getInstance().getJda().addEventListener(new CommandListReactionListener());
        int site = 0;
        int iterateTo;
        final List<Command> commandList = ToxicBot.getInstance().getCommandRegistry().getRegisteredCommands();
        do {
            site++;
            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GRAY).setTitle("Command Overview")
                    .setFooter("Page " + String.valueOf(site), null);
            iterateTo = addCommandsToMessageEmbed(site, embedBuilder, commandList);
            final MessageEmbed messageEmbed = embedBuilder.build();
            this.cachedMessageEmbedMap.add(messageEmbed);
        } while (iterateTo < commandList.size());
    }

    private int addCommandsToMessageEmbed(int site, EmbedBuilder embedBuilder, List<Command> commandList) {
        final int offset = (site - 1) * ITEM_AMOUNT_PER_SITE;
        final int iterateTo = offset + ITEM_AMOUNT_PER_SITE;
        if (offset == iterateTo) {
            throw new IllegalStateException("Can not display site " + site + " because it would not contain any item!");
        }
        for (int i = offset; i < iterateTo && i != commandList.size(); i++) {
            final Command command = commandList.get(i);
            embedBuilder.addField(ToxicBot.getInstance().getCommandRegistry().getPrefix() + command.getCommandName(),
                    command.getDescription(), false);
        }
        return iterateTo;
    }

    @Override
    public CommandResponse triggerCommand(Message message, String[] args) {
        this.displayCommandListSite(message.getTextChannel());
        return CommandResponse.ACCEPTED;
    }

    private void displayCommandListSite(final TextChannel textChannel) {
        this.displayCommandListSite(1, textChannel, null);
    }

    private void displayCommandListSite(final int site, final TextChannel textChannel, final Message editMessage) {
        final MessageEmbed messageEmbed = this.cachedMessageEmbedMap.get(site - 1);
        final RestAction<Message> restAction;
        if (editMessage != null) {
            restAction = editMessage.editMessage(messageEmbed);
        } else {
            restAction = textChannel.sendMessage(messageEmbed);
        }
        restAction.queue(message -> {
            if (site > 1) {
                message.addReaction(ARROW_LEFT_EMOTE).queue(ignoredVoid -> {
                    if (site < CommandListCommand.this.cachedMessageEmbedMap.size()) {
                        message.addReaction(ARROW_RIGHT_EMOTE).queue(new PutMessageMapConsumer(message, site));
                    } else {
                        new PutMessageMapConsumer(message, site).accept(null);
                    }
                });
            } else {
                message.addReaction(ARROW_RIGHT_EMOTE).queue(new PutMessageMapConsumer(message, site));
            }
        });
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private class PutMessageMapConsumer implements Consumer<Void> {
        private final Message message;
        private final int site;

        @Override
        public void accept(Void ignoredVoid) {
            CommandListCommand.this.registeredHelpCommandMessages.put(this.message, this.site);
            Constants.EXECUTOR_SERVICE.execute(() -> {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(60));
                    PutMessageMapConsumer.this.message.clearReactions().queue(anotherIgnoredVoid ->
                            CommandListCommand.this.registeredHelpCommandMessages.remove(PutMessageMapConsumer.this.message));
                } catch (InterruptedException e) {
                    ToxicBot.getTerminal().writeMessage("§cThere was an error while sleeping until the help messages gets removed.");
                }
            });
        }
    }

    private class CommandListReactionListener extends ListenerAdapter {
        @Override
        public void onMessageReactionAdd(MessageReactionAddEvent event) {
            final int currentSite;
            generalMessageCheck:
            {
                for (Message message : CommandListCommand.this.registeredHelpCommandMessages.keySet()) {
                    if (message.getTextChannel().getId().equals(event.getTextChannel().getId()) &&
                            message.getId().equals(event.getMessageId())) {
                        currentSite = CommandListCommand.this.registeredHelpCommandMessages.get(message);
                        break generalMessageCheck;
                    }
                }
                return;
            }
            if (event.getReaction().isSelf()) return;
            event.getReaction().getUsers().queue(users -> {
                if (!users.contains(event.getJDA().getSelfUser())) return;
                String reactionName = event.getReaction().getReactionEmote().getName();
                final int newSite;
                if (reactionName.equals(ARROW_LEFT_EMOTE)) {
                    newSite = currentSite - 1;
                } else if (reactionName.equals(ARROW_RIGHT_EMOTE)) {
                    newSite = currentSite + 1;
                } else {
                    return;
                }
                event.getTextChannel().getMessageById(event.getMessageId()).queue(editMessage ->
                        editMessage.clearReactions().queue(ignoredVoid ->
                                CommandListCommand.this.displayCommandListSite(newSite, event.getTextChannel(), editMessage)));
            });
        }
    }
}