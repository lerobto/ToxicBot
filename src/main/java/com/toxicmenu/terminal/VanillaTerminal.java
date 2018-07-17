package com.toxicmenu.terminal;

import com.toxicmenu.discordbot.ToxicBot;

import java.util.Arrays;
import java.util.Scanner;

public class VanillaTerminal implements Terminal {
    private Thread readerThread;
    private Scanner reader;

    @Override
    public void install() {
        if (System.console() == null) {
            this.reader = new Scanner(System.in);
        }
        this.readerThread = new Thread(() -> {
            do {
                String line = null;
                line = this.reader != null ? this.reader.nextLine() : System.console().readLine();
                if (line == null || line.isEmpty()) {
                    try {
                        Thread.sleep(50L);
                    }
                    catch (Exception exception) {}
                    continue;
                }
                String command = line.split(" ")[0];
                String[] args = new String[]{};
                if (line.split(" ").length > 1) {
                    args = Arrays.copyOfRange(line.split(" "), 1, line.split(" ").length);
                }
                CommandRegistry.runCommand(command, args, this);
            } while (true);
        }
        );
        this.readerThread.start();
    }

    @Override
    public void uninstall() {
        this.readerThread.stop();
        this.readerThread = null;
    }

    @Override
    public void write(String message) {

    }

    @Override
    public void writeDebug(String message) {
        if (ToxicBot.isDevMode() || ToxicBot.isDebug()) {
            writeMessage("[DEBUG] " + message);
        }
    }

    @Override
    public void writeMessage(String message) {
        for (int i = 0; i < 10; i++) {
            message.replace("§" + i, "");
        }
        System.out.println(message.replace("§a", "").replace("§b", "").replace("§c", "").replace("§e", "").replace("§f", ""));
    }

    @Override
    public void log(String msg) {

    }

    @Override
    public void writeMessage(int var1) {

    }
}