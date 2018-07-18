package com.toxicmenu.discordbot.listener;

import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getMember().getUser().isBot()) return;

        event.getGuild().getTextChannelById("462598081280606209").sendMessage("**" + event.getMember().getAsMention() + "** joined our Server! Welcome :tada: :hugging:").queue();


        event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("466071787713855511")).queue();

        PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();
        pc.sendMessage("Hey, ** " + event.getMember().getAsMention() + " ** and welcome to the **ToxicMenu Official Server**! \n" +
                       "If you will download the Modmenu go to #downloads, if you have questions ask us in #\uD83E\uDD14support\uD83E\uDD14"
            ).queue();
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (event.getMember().getUser().isBot()) return;

        event.getGuild().getTextChannelById("462598081280606209").sendMessage("**" + event.getMember().getAsMention() + "** just left the server :slight_frown:").queue();
    }
}