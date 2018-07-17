package com.toxicmenu.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class LogDispatcher extends Thread {
    private final SystemLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<LogRecord>();

    public LogDispatcher(SystemLogger logger) {
        super("Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            LogRecord record;
            try {
                record = this.queue.take();
            }
            catch (InterruptedException ex) {
                continue;
            }
            int length = record.getMessage().getBytes().length;
            if (length > 65536) {
                System.err.println("§cTry to logging a too long message. (Length: " + length + ")");
                continue;
            }
            this.logger.doLog(record);
        }
        for (LogRecord record : this.queue) {
            this.logger.doLog(record);
        }
    }

    public void queue(LogRecord record) {
        if (!this.isInterrupted()) {
            this.queue.add(record);
        }
    }
}

