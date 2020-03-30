package com.example.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {

    static class Task implements Runnable {
        private Semaphore semaphore;
        private int num;

        public Task(Semaphore semaphore, int num) {
            this.semaphore = semaphore;
            this.num = num;
        }

        @Override
        public void run() {
//            try {
//                semaphore.acquire();
//            } catch (InterruptedException e) { }

            // do not throw exception
            semaphore.acquireUninterruptibly();
            semaphore.acquireUninterruptibly(2);
            // get total 3 permits

            System.out.println("run.. (" + num + ")");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) { }

            semaphore.release(2);
            semaphore.release();
        }
    }

    static class Task2 implements Runnable {
        private Semaphore semaphore;
        private int num;

        public Task2(Semaphore semaphore, int num) {
            this.semaphore = semaphore;
            this.num = num;
        }

        @Override
        public void run() {
            try {
                // boolean available = semaphore.tryAcquire(2);
                boolean available = semaphore.tryAcquire(2, 500, TimeUnit.MILLISECONDS);
                if (available) {
                    System.out.println("run.. (" + num + ")");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) { }
                    semaphore.release(2);
                } else {
                    System.out.println("cannot get semaphore");
                }
            } catch (InterruptedException e) { }

        }
    }

    private static final int TASK_NUM = 10;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(TASK_NUM);
        Semaphore semaphore = new Semaphore(5);

        for (int i = 0; i < TASK_NUM; i++) {
            executor.submit(new Task(semaphore, i));
        }

        /*
        *   run.. (0)
            run.. (1)
            run.. (2)
            run.. (5)
            run.. (6)
            run.. (7)
            run.. (8)
            run.. (9)
            run.. (3) -> wait queue에 들어갔었음
            run.. (4) -> //
        * */

        ExecutorService executor2 = Executors.newFixedThreadPool(TASK_NUM);
        Semaphore semaphore2 = new Semaphore(6);

        for (int i = 0; i < TASK_NUM; i++) {
            executor2.submit(new Task2(semaphore2, i));
        }

        /*
        *   run.. (1)
            run.. (0)
            run.. (2)
            cannot get semaphore
            cannot get semaphore
            cannot get semaphore
            cannot get semaphore
            cannot get semaphore
            cannot get semaphore
            cannot get semaphore
        * */
    }
}
