package com.toxicmenu.terminal;

public interface Terminal {
    public void install();

    public void uninstall();

    @Deprecated
    default public void write(String message) {
        this.writeMessage(message);
    }

    public void writeMessage(String var1);

    default public void log(String msg) {
        System.out.println(msg);
    }

    void writeMessage(int var1);

    void writeDebug(String message);
}