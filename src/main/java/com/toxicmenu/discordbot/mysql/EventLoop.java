package com.toxicmenu.discordbot.mysql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventLoop {
    public static final EventLoop DEFAULT_LOOP = new EventLoop(512);
    public static final EventLoop UNLIMITED_LOOP = new EventLoop(-1);
    private int MAX_THREADS = 0;
    private List<Runnable> todo = new ArrayList<Runnable>();
    private List<ThreadRunner> threads = new ArrayList<ThreadRunner>();

    public EventLoop(int maxthreads) {
        this.MAX_THREADS = maxthreads;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ThreadRunner join(final Runnable run) {
        List<ThreadRunner> list = this.threads;
        synchronized (list) {
            if (this.MAX_THREADS <= 0 || this.threads.size() < this.MAX_THREADS) {
                ThreadRunner t = ThreadFactory.getInstance().createThread(new Runnable(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void run() {
                        Object next;
                        try {
                            run.run();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        do {
                            next = null;
                            List list = EventLoop.this.todo;
                            synchronized (list) {
                                if (EventLoop.this.todo.isEmpty()) {
                                    break;
                                }
                                next = (Runnable)EventLoop.this.todo.remove(0);
                            }
                            run.run();
                        } while (true);
                        next = EventLoop.this.threads;
                        synchronized (next) {
                            for (ThreadRunner runner : EventLoop.this.threads) {
                                if (runner.getOriginalRunable() != this) continue;
                                EventLoop.this.threads.remove(runner);
                                return;
                            }
                        }
                    }
                });
                this.threads.add(t);
                t.start();
                return t;
            }
            List<Runnable> t = this.todo;
            synchronized (t) {
                this.todo.add(run);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCurruntThreads() {
        List<ThreadRunner> list = this.threads;
        synchronized (list) {
            return this.threads.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ThreadRunner> getWorkingThreads() {
        List<ThreadRunner> list = this.threads;
        synchronized (list) {
            return Collections.unmodifiableList(this.threads);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Runnable> getQueue() {
        List<Runnable> list = this.todo;
        synchronized (list) {
            return Collections.unmodifiableList(this.todo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void terminate() {
        List list = this.todo;
        synchronized (list) {
            this.todo.clear();
        }
        list = this.threads;
        synchronized (list) {
            for (ThreadRunner t : new ArrayList<ThreadRunner>(this.threads)) {
                try {
                    t.stop();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        EventLoop loop = new EventLoop(3);
        loop.join(() -> {
                    try {
                        Thread.sleep(800L);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        loop.join(() -> {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}