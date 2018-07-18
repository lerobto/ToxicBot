package com.toxicmenu.discordbot.mysql;

public abstract class ThreadFactory {

    private static ThreadFactory instance = new ThreadFactory(){

        @Override
        public ThreadRunner createThread(final Runnable run) {
            return new ThreadRunner(){
                Thread thread;

                @Override
                public void start() {
                    if (this.thread != null) {
                        throw new IllegalStateException("Thread is alredy running!");
                    }
                    this.thread = new Thread(run);
                    this.thread.start();
                }

                @Override
                public void stop() {
                    if (this.thread == null) {
                        throw new IllegalStateException("Thread isnt running!");
                    }
                    this.thread.interrupt();
                    this.thread = null;
                }

                @Override
                public Thread getThread() {
                    return this.thread;
                }

                @Override
                public Runnable getOriginalRunable() {
                    return run;
                }
            };
        }

    };

    public static ThreadFactory getInstance() {
        return instance;
    }

    public static void setInstance(ThreadFactory instance) {
        ThreadFactory.instance = instance;
    }

    public static ThreadFactory getFactory() {
        return ThreadFactory.getInstance();
    }

    public abstract ThreadRunner createThread(Runnable var1);
}