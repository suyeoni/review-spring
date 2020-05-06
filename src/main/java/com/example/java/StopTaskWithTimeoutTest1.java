package com.example.java;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class StopTaskWithTimeoutTest1 {
    static class TaskWithVolatile implements Runnable { //쓰 지 마
        private volatile boolean keepRunning = true;

        @Override
        public void run() {
            while (keepRunning) {
                // task
                System.out.println("[TaskWithVolatile] task start.." + System.currentTimeMillis());
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ignore) { }
                System.out.println("[TaskWithVolatile] task end.." + System.currentTimeMillis());
            }
            System.out.println("[TaskWithVolatile] task stop.." + System.currentTimeMillis());
        }

        public void stop() {
            keepRunning = false;
        }
    }

    static class TaskWithAtomic implements Runnable {
        public AtomicBoolean keepRunning = new AtomicBoolean(true);

        @Override
        public void run() {
            while (keepRunning.get()) {
                // task
                System.out.println("[TaskWithAtomic] task start.." + System.currentTimeMillis());
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ignore) { }
                System.out.println("[TaskWithAtomic] task end.." + System.currentTimeMillis());
            }
            System.out.println("[TaskWithAtomic] task stop.." + System.currentTimeMillis());
        }

        public void stop() {
            keepRunning.set(false);
        }
    }

    static class RunnableTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // task
                System.out.println("[TaskWithAtomic] task start.." + System.currentTimeMillis());
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ignore) { }
                System.out.println("[TaskWithAtomic] task end.." + System.currentTimeMillis());
            }
            System.out.println("[TaskWithAtomic] task stop.." + System.currentTimeMillis());
        }
    }

    public static void main(String[] args) {
        TaskWithVolatile taskWithVolatile = new TaskWithVolatile();
        Thread thread1 = new Thread(taskWithVolatile);
        thread1.start();

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ignore) { }

        System.out.println("[TaskWithVolatile] attempt to stop task.. " + System.currentTimeMillis());
        taskWithVolatile.stop(); // while문으로 갈 때까지 기다림

        TaskWithAtomic taskWithAtomic = new TaskWithAtomic();
        Thread thread2 = new Thread(taskWithAtomic);
        thread2.start();

        // 다른 thread 에서 sleep 걸고 싶을때
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            System.out.println("[TaskWithAtomic] attempt to stop task.. " + System.currentTimeMillis());
            taskWithAtomic.stop();
        }, 5 * 1000, TimeUnit.MILLISECONDS);
//        scheduler.shutdownNow(); // main 에서 scheduler 바로 꺼버리기떄문에 조심

        RunnableTask task = new RunnableTask();
        Thread thread3 = new Thread(task);
        thread3.start();

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ignore) { }

        System.out.println("[RunnableTask] attempt to stop task.. " + System.currentTimeMillis());
        thread3.interrupt(); // 바로 끝
    }
}
