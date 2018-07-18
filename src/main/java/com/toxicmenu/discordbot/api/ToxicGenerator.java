package com.toxicmenu.discordbot.api;

import java.util.Random;

public class ToxicGenerator {

    public static String generateLicense() {
        Random random = new Random();
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < 5; index++) {
            builder.append(possible.charAt(random.nextInt(possible.length())));
        }

        String gameId = builder.toString();

        return gameId;
    }
}