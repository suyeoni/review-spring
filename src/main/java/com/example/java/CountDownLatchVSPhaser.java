package com.example.java;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class CountDownLatchVSPhaser {

    static class TaskWithLatch implements Runnable {
        private CountDownLatch latch;
        private String name;
        private int sleep;

        public TaskWithLatch(CountDownLatch latch, String name, int sleep) {
            this.latch = latch;
            this.name = name;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) { }
            latch.countDown();
            System.out.println("[" + name + "] countdown!");
        }
    }

    static class TaskWithPhaser implements Runnable {
        private Phaser phaser;
        private String name;
        private int sleep;

        public TaskWithPhaser(Phaser phaser, String name, int sleep) {
            this.phaser = phaser;
            this.name = name;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) { }
            phaser.arrive();
            System.out.println("[" + name + "] arrived!");
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        /*
         * CountDownLatch
         * */
        CountDownLatch latch = new CountDownLatch(3);
        pool.submit(new TaskWithLatch(latch, "Task1", 2000));
        pool.submit(new TaskWithLatch(latch, "Task2", 500));
        pool.submit(new TaskWithLatch(latch, "Task3", 1400));

        latch.await();
        System.out.println("Countdown Done!");


        /*
         * Phaser
         * */

        Phaser phaser = new Phaser(3); // party 수 (== count 수)

        for (int i = 0; i < 10; i++) {
            System.out.println("Phase " + phaser.getPhase() + " Start!");
            pool.submit(new TaskWithPhaser(phaser, "Task1", 2000));
            pool.submit(new TaskWithPhaser(phaser, "Task2", 500));
            pool.submit(new TaskWithPhaser(phaser, "Task3", 1400));
            phaser.awaitAdvance(i); // first phase done
        }

    }
}
