package com.toxicmenu.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import com.toxicmenu.discordbot.ToxicBot;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import jline.internal.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class JLineTerminal implements Terminal {
    private ConsoleReader console;
    private Thread reader;
    private boolean active = true;
    private String message;

    @Override
    public void install() {
        AnsiConsole.systemInstall();
        System.setOut(new CostumSystemPrintStream(this));
        System.setErr(new CostumSystemPrintStream(this){

            @Override
            public void write(String message) {
                ToxicBot.getLogger().log(Level.WARNING, message);
                if(ToxicBot.isDevMode()) {
                    JLineTerminal.this.writeMessage("§8[§4FEHLER§8] §7" + message, false);
                }
            }
        });
        this.initReader();
    }

    private void initReader() {
        try {
            this.console = new ConsoleReader(System.in, (OutputStream)AnsiConsole.out());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (this.reader == null) {
            this.reader = new Thread(){

                @Override
                public void run() {
                    while (this.isAlive()) {
                        if (!JLineTerminal.this.active) {
                            try {
                                Thread.sleep(10L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        try {
                            try {
                                JLineTerminal.this.console.getCursorBuffer();
                                StringBuilder b = new StringBuilder();
                                b.append((Object)JLineTerminal.this.console.getCursorBuffer());
                                b = null;
                            }
                            catch (Exception e) {
                                try {
                                    CursorBuffer buffer = JLineTerminal.this.console.getCursorBuffer();
                                    buffer.getClass().getField("buffer").setAccessible(true);
                                    buffer.getClass().getField("buffer").set((Object)buffer, new StringBuilder());
                                    buffer.cursor = 0;
                                }
                                catch (Exception ex) {
                                    try {
                                        CursorBuffer buffer = JLineTerminal.this.console.getCursorBuffer();
                                        buffer.getClass().getField("buffer").setAccessible(true);
                                        buffer.getClass().getField("buffer").set((Object)buffer, new StringBuilder());
                                        buffer.cursor = 0;
                                    }
                                    catch (Exception exx) {
                                        exx.printStackTrace();
                                    }
                                    if(ToxicBot.isDevMode()) {
                                        JLineTerminal.this.writeMessage("§cHard buffer reset!");
                                    }
                                }
                            }
                            String in = JLineTerminal.this.console.readLine();
                            if ("".equalsIgnoreCase(in)) continue;
                            String command = in.split(" ")[0];
                            String[] args = new String[]{};
                            if (in.split(" ").length > 1) {
                                args = Arrays.copyOfRange(in.split(" "), 1, in.split(" ").length);
                            }
                            CommandRegistry.runCommand(command, args, JLineTerminal.this);
                        }
                        catch (Exception e) {
                            JLineTerminal.this.writeMessage("§cAn error happend while performing this commands:");
                            if(ToxicBot.isDevMode()) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
        }
        this.reader.start();
    }

    @Override
    public void uninstall() {
        this.console.shutdown();
        this.reader.interrupt();
        AnsiConsole.systemUninstall();
    }

    @Override
    public void write(String message) {

    }

    public void lock() {
        this.lock(null);
    }

    private String getPromt() {
        String prefix = "";
        prefix = String.valueOf(prefix) + "§a> §7";
        return prefix;
    }

    public ConsoleReader getConsolenReader() {
        return this.console;
    }

    @Override
    public synchronized void writeMessage(String message) {
        this.writeMessage(message, true);
    }

    @Override
    public void log(String msg) {

    }

    @Override
    public void writeMessage(int var1) {

    }

    @Override
    public void writeDebug(String message) {
        if (ToxicBot.isDevMode() || ToxicBot.isDebug()) {
            writeMessage("§8[§c§lDEBUG§8] §7" + message, false);
        }
    }

    public synchronized void writeMessage(String message, boolean log) {
        if (message == null || message.length() == 0) {
            return;
        }
        if (message.split("\n").length > 1) {
            String[] arrstring = message.split("\n");
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String s = arrstring[n2];
                this.writeMessage(s);
                ++n2;
            }
            return;
        }
        try {
            if (log) {
                ToxicBot.getLogger().log(Level.INFO, message);
            }
            message = "[" + new Date().toString() + "] " + message;
            String promt = "";
            String input_message = "";
            int cursor = 0;
            if (!this.active) {
                promt = ChatColor.toAnsiFormat(this.message);
                cursor = promt.length();
            } else {
                input_message = this.console.getCursorBuffer().toString();
                promt = "\r" + this.getPromt();
                cursor = this.console.getCursorBuffer().cursor;
            }
            while (Ansi.stripAnsi((String)ChatColor.stripColor(message)).length() < input_message.length() + Ansi.stripAnsi((String)ChatColor.stripColor(promt)).length()) {
                message = String.valueOf(message) + " ";
            }
            AnsiConsole.out.println("\r" + ChatColor.toAnsiFormat(message));
            this.console.resetPromptLine(ChatColor.toAnsiFormat(promt), Ansi.stripAnsi((String)input_message), cursor);
        }
        catch (Exception promt) {
            // empty catch block
        }
    }

    public void lock(String message) {
        this.active = false;
        if (message == null) {
            message = "";
        }
        try {
            this.console.killLine();
            this.message = ChatColor.toAnsiFormat(message);
            this.console.resetPromptLine(this.message, "", this.message.length());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        this.active = true;
        try {
            this.console.killLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}