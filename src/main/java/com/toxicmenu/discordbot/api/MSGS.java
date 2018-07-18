package com.toxicmenu.discordbot.api;

import net.dv8tion.jda.core.EmbedBuilder;
import java.awt.*;

public class MSGS {

    public static EmbedBuilder success() {
        return new EmbedBuilder().setColor(Color.GREEN);
    }

    public static EmbedBuilder error() {
        return new EmbedBuilder().setColor(Color.RED);
    }

    public static EmbedBuilder warn() {
        return new EmbedBuilder().setColor(Color.ORANGE);
    }
}