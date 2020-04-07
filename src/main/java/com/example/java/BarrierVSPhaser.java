package com.example.java;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class BarrierVSPhaser {

    static class TaskWithBarrier implements Runnable {
        private CyclicBarrier barrier;
        private String name;
        private int sleep;

        public TaskWithBarrier(CyclicBarrier barrier, String name, int sleep) {
            this.barrier = barrier;
            this.name = name;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) { }
                try {
                    System.out.println("[" + name + "] done & wait others. time: " + System.currentTimeMillis());
                    barrier.await();
                    System.out.println("[" + name + "] all task complete. time: " + System.currentTimeMillis());
                } catch (InterruptedException | BrokenBarrierException e) {}
            }
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
            while (true) {
                System.out.println("[" + name + "] Phase " + phaser.getPhase() + " Start!");
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) { }
                System.out.println("[" + name + "] done & wait others. time: " + System.currentTimeMillis());
                phaser.arriveAndAwaitAdvance();
                System.out.println("[" + name + "] all task complete. time: " + System.currentTimeMillis());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        /*
         * CyclicBarrier
         * */
        CyclicBarrier barrier = new CyclicBarrier(3); // party 수 (== count 수)
        pool.submit(new TaskWithBarrier(barrier, "Task1", 2000));
        pool.submit(new TaskWithBarrier(barrier, "Task2", 500));
        pool.submit(new TaskWithBarrier(barrier, "Task3", 1400));

        /*
        *   [Task2] done & wait others. time: 1586262888969
            [Task3] done & wait others. time: 1586262889874
            [Task1] done & wait others. time: 1586262890470
            [Task1] all task complete. time: 1586262890470
            [Task2] all task complete. time: 1586262890470
            [Task3] all task complete. time: 1586262890470
        * */
        Thread.sleep(10000);


        /*
         * Phaser
         * */
        Phaser phaser = new Phaser(3); // party 수 (== count 수)

        pool.submit(new TaskWithPhaser(phaser, "Task1", 2000));
        pool.submit(new TaskWithPhaser(phaser, "Task2", 500));
        pool.submit(new TaskWithPhaser(phaser, "Task3", 1400));

        /*
        *   [Task1] Phase 0 Start!
            [Task3] Phase 0 Start!
            [Task2] Phase 0 Start!
            [Task2] done & wait others. time: 1586263086914
            [Task3] done & wait others. time: 1586263087816
            [Task1] done & wait others. time: 1586263088414
            [Task1] all task complete. time: 1586263088414
            [Task1] Phase 1 Start!
            [Task2] all task complete. time: 1586263088414
            [Task2] Phase 1 Start!
            [Task3] all task complete. time: 1586263088414
            [Task3] Phase 1 Start!
        * */

        Thread.sleep(10000);
    }
}
