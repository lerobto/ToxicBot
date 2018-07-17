package com.toxicmenu.terminal;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.fusesource.jansi.Ansi;

public enum ChatColor {
    BLACK('0', "black", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString()),
    DARK_BLUE('1', "dark_blue", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString()),
    DARK_GREEN('2', "dark_green", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString()),
    DARK_AQUA('3', "dark_aqua", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString()),
    DARK_RED('4', "dark_red", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString()),
    DARK_PURPLE('5', "dark_purple", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()),
    GOLD('6', "gold", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString()),
    GRAY('7', "gray", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString()),
    DARK_GRAY('8', "dark_gray", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString()),
    BLUE('9', "blue", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString()),
    GREEN('a', "green", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString()),
    AQUA('b', "aqua", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString()),
    RED('c', "red", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString()),
    LIGHT_PURPLE('d', "light_purple", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString()),
    YELLOW('e', "yellow", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString()),
    WHITE('f', "white", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString()),
    MAGIC('k', "obfuscated", Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()),
    BOLD('l', "bold", Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()),
    STRIKETHROUGH('m', "strikethrough", Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()),
    UNDERLINE('n', "underline", Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()),
    ITALIC('o', "italic", Ansi.ansi().a(Ansi.Attribute.ITALIC).toString()),
    RESET('r', "reset", Ansi.ansi().a(Ansi.Attribute.RESET).toString()),
    NEW_LINE('z', "new_line", Ansi.ansi().newline().toString());
    
    private static final Map<ChatColor, String> ANSI_REPLACEMENTS;
    public static final char COLOR_CHAR = '\u00a7';
    public static final String ALL_CODES;
    public static final Pattern STRIP_COLOR_PATTERN;
    private static final Map<Character, ChatColor> BY_CHAR;
    private final char code;
    private final String toString;
    private final String name;
    private String ansi_string;

    static {
        ANSI_REPLACEMENTS = new EnumMap<ChatColor, String>(ChatColor.class);
        STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR-Z]");
        BY_CHAR = new HashMap<Character, ChatColor>();
        String temp = "";
        ChatColor[] arrchatColor = ChatColor.values();
        int n = arrchatColor.length;
        int n2 = 0;
        while (n2 < n) {
            ChatColor colour = arrchatColor[n2];
            BY_CHAR.put(Character.valueOf(colour.code), colour);
            ANSI_REPLACEMENTS.put(colour, colour.ansi_string);
            temp = String.valueOf(colour.code).equals(String.valueOf(colour.code).toUpperCase()) ? String.valueOf(temp) + colour.code : String.valueOf(temp) + new StringBuilder(String.valueOf(colour.code)).toString().toUpperCase() + colour.code;
            ++n2;
        }
        ALL_CODES = temp;
    }

    public String getName() {
        return this.name;
    }

    private ChatColor(char code, String name, String ansi) {
        this.code = code;
        this.name = (String)name;
        this.ansi_string = (String)ansi;
        this.toString = new String(new char[]{'\u00a7', code});
    }

    public String toString() {
        return this.toString;
    }

    public static String stripColor(String input) {
        if (input == null) {
            return null;
        }
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] array_text = textToTranslate.toCharArray();
        int i = 0;
        while (i < array_text.length - 1) {
            if (array_text[i] == altColorChar && ALL_CODES.indexOf(array_text[i + 1]) > -1) {
                array_text[i] = 167;
                array_text[i + 1] = Character.toLowerCase(array_text[i + 1]);
            }
            ++i;
        }
        return new String(array_text);
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(Character.valueOf(code));
    }

    public static String toAnsiFormat(String s) {
        ChatColor[] arrchatColor = ChatColor.values();
        int n = arrchatColor.length;
        int n2 = 0;
        while (n2 < n) {
            ChatColor color = arrchatColor[n2];
            s = s.replaceAll("(?i)" + color.toString(), ANSI_REPLACEMENTS.get((Object)color));
            ++n2;
        }
        return String.valueOf(s) + Ansi.ansi().a(Ansi.Attribute.RESET).toString();
    }
}

