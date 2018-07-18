package com.toxicmenu.discordbot.mysql;

public interface ThreadRunner {
    public void start();

    public void stop();

    public Thread getThread();

    public Runnable getOriginalRunable();
}