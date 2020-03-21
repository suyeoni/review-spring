package com.example.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("[" + System.currentTimeMillis() + "] Thread name : " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignore) { }
        }
    }

    public static void main(String[] args) {
        /*
         * fixed thread pool
         * */
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(/*numOfThread*/10);
        for (int i = 0; i < 1000; i++) {
            fixedExecutor.execute(new Task());
        }
        System.out.println("Thread name : " + Thread.currentThread().getName());

        /*
         * cached thread pool
         * */
        ExecutorService cachedExecutor = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; i++) {
            // if all thread are busy, then create new thread and place thread to pool
            // if thread is idle for 60 seconds, the kill the thread.
            cachedExecutor.execute(new Task());
        }
        System.out.println("Thread name : " + Thread.currentThread().getName());

        /*
         * scheduled thread pool
         * */
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10 /*fixed*/);
        System.out.println("main thread time : " + System.currentTimeMillis());
        // just 1 time after 5 seconds
        scheduledExecutor.schedule(() -> {
            System.out.println("executor.schedule() [" + System.currentTimeMillis()
                               + "] Thread name : " + Thread.currentThread().getName());
        }, 5, TimeUnit.SECONDS);

        // every 3 time repeatedly
        scheduledExecutor.scheduleAtFixedRate(new Task(), 5, 3, TimeUnit.SECONDS);

        // every 3 time repeatedly "after previous task completes"
        // in this case, every 5 (2+3) sec
        scheduledExecutor.scheduleWithFixedDelay(new Task(), 5, 3, TimeUnit.SECONDS);


        /*
         * single thread pool
         * */
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 1000; i++) {
            // guarantee sequence because there's only one thread
            // recreate thread if thread is killed by task
            singleThreadExecutor.execute(new Task());
        }
    }

}
