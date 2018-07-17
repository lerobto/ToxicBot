package com.toxicmenu.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SystemLogger extends Logger {
    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher;

    public SystemLogger() {
        super("Logger", null);
        this.dispatcher = new LogDispatcher(this);
        this.setLevel(Level.ALL);
        try {
            new File("log/").mkdirs();
            FileHandler fileHandler = new FileHandler("log/log-%g.log", 16777216, 8, false);
            fileHandler.setFormatter(this.formatter);
            this.addHandler(fileHandler);
        }
        catch (IOException ex) {
            System.err.println("Could not register logger!");
            ex.printStackTrace();
        }
        this.dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        this.dispatcher.queue(record);
    }

    protected void doLog(LogRecord record) {
        super.log(record);
    }

    @Deprecated
    public void log(String msg) {
        this.log(Level.INFO, msg);
    }
}

