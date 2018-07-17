package com.toxicmenu.terminal;

public class Debugger {
    public static void printMessage(String message) {
        System.out.println("[Debugger] [" + Debugger.getLastCallerClass(new String[0]) + "] -> " + message);
    }

    public static /* varargs */ String getLastCallerClass(String ... ex) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        int i = 0;
        while (i < stack.length) {
            block4 : {
                StackTraceElement currunt = stack[i];
                if (!(currunt.getClassName().equalsIgnoreCase(Debugger.class.getName()) || currunt.getClassName().equalsIgnoreCase("java.lang.Thread") || currunt.getClassName().contains("dev.wolveringer.dataserver.terminal."))) {
                    String[] arrstring = ex;
                    int n = arrstring.length;
                    int n2 = 0;
                    while (n2 < n) {
                        String e = arrstring[n2];
                        if (!currunt.getClassName().startsWith(e)) {
                            ++n2;
                            continue;
                        }
                        break block4;
                    }
                    return String.valueOf(currunt.getClassName()) + ":" + currunt.getLineNumber();
                }
            }
            ++i;
        }
        return "unknown:-1";
    }
}

