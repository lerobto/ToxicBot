package com.toxicmenu.discordbot.listener;

import com.toxicmenu.discordbot.mysql.UserAPI;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getMember().getUser().isBot()) return;

        PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();


        event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("466071787713855511")).queue();

        UserAPI user = new UserAPI(event.getUser().getId());

        if(user.userExists()) {
            event.getGuild().getTextChannelById("462598081280606209").sendMessage("**" + event.getMember().getAsMention() + "** rejoined our Server! Welcome :tada: :hugging:").queue();

            if(user.get("muted").equalsIgnoreCase("true")) {
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("469687618242871307")).queue();

                pc.sendMessage("Welcome back ** " + event.getMember().getAsMention() + " **\n" +
                        "You have been muted again. You are rejoined our Discord but you will not be unmuted ;)"
                ).queue();

                return;
            } else {
                pc.sendMessage("Welcome back ** " + event.getMember().getAsMention() + " **! \n" +
                        "We are glad that you came back!"
                ).queue();
            }
        } else {
            user.create();
            event.getGuild().getTextChannelById("462598081280606209").sendMessage("**" + event.getMember().getAsMention() + "** joined our Server! Welcome :tada: :hugging:").queue();

            pc.sendMessage("Hey, ** " + event.getMember().getAsMention() + " ** and welcome to the **ToxicMenu Official Server**! \n" +
                    "If you will download the Modmenu go to #downloads, if you have questions ask us in #\uD83E\uDD14support\uD83E\uDD14"
            ).queue();
        }
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (event.getMember().getUser().isBot()) return;

        event.getGuild().getTextChannelById("462598081280606209").sendMessage("**" + event.getMember().getUser().getName() + "** just left the server :slight_frown:").queue();
    }
}