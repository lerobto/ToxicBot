package com.toxicmenu.discordbot.api;

import java.util.ArrayList;

public class ToxicChannel {

    public static ArrayList<String> whitelist;

    public static boolean checkWhitelist(String id) {
        if(whitelist.contains(id)) {
            return true;
        }

        return false;
    }
}
