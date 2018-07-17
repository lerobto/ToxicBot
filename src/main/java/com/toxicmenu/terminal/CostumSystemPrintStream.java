package com.toxicmenu.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.commons.lang3.ObjectUtils;

public class CostumSystemPrintStream extends PrintStream {
    private final Terminal terminal;

    public CostumSystemPrintStream(Terminal t) {
        super(new OutputStream(){

            @Override
            public void write(int b) throws IOException {
                throw new RuntimeException("error 001");
            }
        });
        this.terminal = t;
    }

    public int hashCode() {
        return this.out.hashCode();
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public boolean equals(Object obj) {
        return this.out.equals(obj);
    }

    public String toString() {
        return "CP";
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean checkError() {
        return false;
    }

    @Override
    public void write(int b) {
        this.println(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        this.write(new String(buf, off, len));
    }

    @Override
    public void print(boolean b) {
        this.println(b);
    }

    @Override
    public void print(char c) {
        this.println(c);
    }

    @Override
    public void print(int i) {
        this.println(i);
    }

    @Override
    public void print(long l) {
        this.println(l);
    }

    @Override
    public void print(float f) {
        this.println(f);
    }

    @Override
    public void print(double d) {
        this.println(d);
    }

    @Override
    public void print(char[] s) {
        this.println(s);
    }

    @Override
    public void print(String s) {
        this.println(s);
    }

    @Override
    public void print(Object obj) {
        this.println(obj);
    }

    @Override
    public void println() {
        this.write("");
    }

    @Override
    public void println(boolean x) {
        this.write(String.valueOf(x));
    }

    @Override
    public void println(char x) {
        this.write(ObjectUtils.toString((Object)Character.valueOf(x)));
    }

    @Override
    public void println(int x) {
        this.write(ObjectUtils.toString((Object)x));
    }

    @Override
    public void println(long x) {
        this.write(ObjectUtils.toString((Object)x));
    }

    @Override
    public void println(float x) {
        this.write(ObjectUtils.toString((Object)Float.valueOf(x)));
    }

    @Override
    public void println(double x) {
        this.write(ObjectUtils.toString((Object)x));
    }

    @Override
    public void println(char[] x) {
        this.write(ObjectUtils.toString((Object)x));
    }

    @Override
    public void println(String x) {
        this.write("[" + Debugger.getLastCallerClass(this.getClass().getName()) + "] " + ObjectUtils.toString((Object)x));
    }

    @Override
    public void println(Object x) {
        this.write(ObjectUtils.toString((Object)x));
    }

    @Override
    public /* varargs */ PrintStream printf(String format, Object ... args) {
        this.write(String.format(format, args));
        return this;
    }

    @Override
    public /* varargs */ PrintStream printf(Locale l, String format, Object ... args) {
        this.write(String.format(l, format, args));
        return this;
    }

    @Override
    public /* varargs */ PrintStream format(String format, Object ... args) {
        this.write(String.format(format, args));
        return this;
    }

    @Override
    public /* varargs */ PrintStream format(Locale l, String format, Object ... args) {
        this.write(String.format(l, format, args));
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq) {
        this.write("\u00a7cAppend: " + csq);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        this.write("\u00a7cAppend: " + csq);
        return this;
    }

    @Override
    public PrintStream append(char c) {
        this.write("\u00a7cAppend: " + c);
        return this;
    }

    public void write(String message) {
        this.terminal.write(message);
    }

}

