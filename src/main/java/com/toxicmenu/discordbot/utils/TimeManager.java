package com.toxicmenu.discordbot.utils;

import com.toxicmenu.discordbot.ToxicBot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TimeManager {
    static DateFormat format = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    DateFormat hourFormat = new SimpleDateFormat("HH");
    DateFormat dayFormant = new SimpleDateFormat("dd");

    public static String getDate(long ende) {
        if (ende < 1L) {
            return "-1 Error";
        }
        try {
            Date date = new Date(ende);
            return format.format(date);
        }
        catch (Exception ex) {
            ToxicBot.getTerminal().writeMessage("Could not make date!");
            ToxicBot.getTerminal().writeMessage("Variable ende: " + ende);
            ToxicBot.getTerminal().writeMessage(Arrays.deepToString(ex.getStackTrace()));
            return "31.12.2018 - 23:59:59";
        }
    }

    public String getHour() {
        Date date = new Date(System.currentTimeMillis());
        return this.hourFormat.format(date);
    }

    public String getDay() {
        return this.dayFormant.format(new Date(System.currentTimeMillis()));
    }

    public static String formatTimeLength(long endTime) {
        String append;
        String[] end = TimeManager.getRemainingTime(endTime);
        StringBuilder stringBuilder = new StringBuilder();
        int tage = Integer.valueOf(end[0]);
        int stunden = Integer.valueOf(end[1]);
        int minuten = Integer.valueOf(end[2]);
        int sekunden = Integer.valueOf(end[0]);
        if (tage != 0) {
            append = tage == 1 ? String.valueOf(tage) + " Day" : String.valueOf(tage) + " Days";
            stringBuilder.append(append);
        }
        if (stunden != 0) {
            append = stunden == 1 ? String.valueOf(stunden) + " Hour" : String.valueOf(stunden) + " Hours";
            stringBuilder.append(append);
        }
        if (minuten != 0) {
            append = minuten == 1 ? String.valueOf(minuten) + " Minute" : String.valueOf(minuten) + " Minutes";
            stringBuilder.append(append);
        }
        if (sekunden != 0) {
            append = sekunden == 1 ? String.valueOf(sekunden) + " Second" : String.valueOf(sekunden) + " Seconds";
            stringBuilder.append(append);
        }
        return stringBuilder.toString();
    }

    private static String[] getRemainingTime(long milli) {
        long zeit = 0L;
        zeit = System.currentTimeMillis() > milli ? System.currentTimeMillis() - milli : milli - System.currentTimeMillis();
        int tage = 0;
        int stunden = 0;
        int minuten = 0;
        int sekunden = 0;
        if ((zeit /= 1000L) >= 86400L) {
            tage = (int)zeit / 86400;
        }
        if ((zeit -= (long)(tage * 86400)) > 3600L) {
            stunden = (int)zeit / 3600;
        }
        if ((zeit -= (long)(stunden * 3600)) > 60L) {
            minuten = (int)zeit / 60;
        }
        sekunden = (int)(zeit -= (long)(minuten * 60));
        String[] al = new String[]{Integer.toString(tage), Integer.toString(stunden), Integer.toString(minuten), Integer.toString(sekunden)};
        return al;
    }
}