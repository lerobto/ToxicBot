package com.toxicmenu.terminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ArgumentList {
    private List<Argument> args = new ArrayList<Argument>();

    public List<Argument> getArguments() {
        return this.args;
    }

    ArgumentList(List<Argument> args) {
        this.args = args;
    }

    public static ArgumentListBuilder builder() {
        return new ArgumentListBuilder();
    }

    public static class Argument {
        private String argument;
        private String message;
        private boolean outdated;

        public Argument(String argument, String message, boolean outdated) {
            this.argument = argument;
            this.message = message;
            this.outdated = outdated;
        }

        public Argument(String argument, String message) {
            this.argument = argument;
            this.message = message;
        }

        public String format() {
            return String.valueOf(this.argument) + " >> " + (this.outdated ? "§c" : "§a") + this.message;
        }

        public String getArgument() {
            return this.argument;
        }

        public String getMessage() {
            return this.message;
        }

        public boolean isOutdated() {
            return this.outdated;
        }
    }

    public static class ArgumentListBuilder {
        private ArrayList<Argument> args;

        ArgumentListBuilder() {
        }

        public ArgumentListBuilder arg(Argument arg) {
            if (this.args == null) {
                this.args = new ArrayList();
            }
            this.args.add(arg);
            return this;
        }

        public ArgumentListBuilder args(Collection<? extends Argument> args) {
            if (this.args == null) {
                this.args = new ArrayList();
            }
            this.args.addAll(args);
            return this;
        }

        public ArgumentListBuilder clearArgs() {
            if (this.args != null) {
                this.args.clear();
            }
            return this;
        }

        public ArgumentList build() {
            List<Argument> args;
            switch (this.args == null ? 0 : this.args.size()) {
                case 0: {
                    args = Collections.emptyList();
                    break;
                }
                case 1: {
                    args = Collections.singletonList(this.args.get(0));
                    break;
                }
                default: {
                    args = Collections.unmodifiableList(new ArrayList<Argument>(this.args));
                }
            }
            return new ArgumentList(args);
        }

        public String toString() {
            return "ArgumentList.ArgumentListBuilder(args=" + this.args + ")";
        }
    }

}

