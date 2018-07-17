package com.toxicmenu.log;

import java.beans.ConstructorProperties;
import java.util.logging.Level;
import com.toxicmenu.discordbot.ToxicBot;
import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

public class JettySystemLogger extends AbstractLogger {
    private final String name;

    public /* varargs */ void warn(String paramString, Object ... paramVarArgs) {
        ToxicBot.getLogger().warning(String.format(paramString, paramVarArgs));
    }

    public void warn(Throwable paramThrowable) {
        ToxicBot.getLogger().log(Level.WARNING, paramThrowable.getMessage(), paramThrowable);
    }

    public void warn(String paramString, Throwable paramThrowable) {
        ToxicBot.getLogger().log(Level.WARNING, paramString, paramThrowable);
    }

    public /* varargs */ void info(String paramString, Object ... paramVarArgs) {
        ToxicBot.getLogger().info(String.format(paramString, paramVarArgs));
    }

    public void info(Throwable paramThrowable) {
        ToxicBot.getLogger().log(Level.INFO, paramThrowable.getMessage(), paramThrowable);
    }

    public void info(String paramString, Throwable paramThrowable) {
        ToxicBot.getLogger().log(Level.INFO, paramString, paramThrowable);
    }

    public boolean isDebugEnabled() {
        return Boolean.getBoolean("jetty.debug");
    }

    public void setDebugEnabled(boolean paramBoolean) {
        System.setProperty("jetty.debug", String.valueOf(paramBoolean));
    }

    public /* varargs */ void debug(String paramString, Object ... paramVarArgs) {
        if (this.isDebugEnabled()) {
            this.info(paramString, paramVarArgs);
        }
    }

    public void debug(Throwable paramThrowable) {
        if (this.isDebugEnabled()) {
            this.warn(paramThrowable);
        }
    }

    public void debug(String paramString, Throwable paramThrowable) {
        if (this.isDebugEnabled()) {
            this.warn(paramString, paramThrowable);
        }
    }

    public void ignore(Throwable paramThrowable) {
    }

    protected Logger newLogger(String paramString) {
        return new JettySystemLogger(paramString);
    }

    @ConstructorProperties(value={"name"})
    public JettySystemLogger(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

