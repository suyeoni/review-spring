package com.example.java;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class AdderAccumulatorTest {
    static class TaskWithAtomic implements Runnable {
        private AtomicLong atomic;
        private int sleep;

        public TaskWithAtomic(AtomicLong atomic, int sleep) {
            this.atomic = atomic;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) { }
            System.out.println("incrementAndGet : " + atomic.incrementAndGet()
                               + " sleep : " + sleep
                               + " time : " + System.currentTimeMillis()); // ++n
            // adder.getAndIncrement(); n++
        }
    }

    static class TaskWithAdder implements Runnable {
        private LongAdder adder;
        private int sleep;

        public TaskWithAdder(LongAdder adder, int sleep) {
            this.adder = adder;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) { }
            adder.increment();
            System.out.println("merged values : " + adder.longValue() // 호출할때 마다 머지함
                               + " sleep : " + sleep
                               + " time : " + System.currentTimeMillis());
        }
    }

    static class TaskWithAccumulator implements Runnable {
        private LongAccumulator accumulator;
        private int sleep;

        public TaskWithAccumulator(LongAccumulator accumulator, int sleep) {
            this.accumulator = accumulator;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) { }
            int random = new Random().nextInt(10);
            accumulator.accumulate(random);
            System.out.println("random number : " + random
                               + " sleep : " + sleep
                               + " time : " + System.currentTimeMillis());
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        /*
         * Atomic
         * */
        AtomicLong sharedAtomic = new AtomicLong(0);
        IntStream.range(0, 20).forEach(i -> {
            threadPool.submit(new TaskWithAtomic(sharedAtomic, new Random().nextInt(2000)));
        });

        Thread.sleep(5000);
        System.out.println("Atomic Result : " + sharedAtomic.get());
        

        /*
         * Adder
         * */
        LongAdder adder = new LongAdder();
        IntStream.range(0, 10).forEach(i -> {
            threadPool.submit(new TaskWithAdder(adder, 0));
        });
        Thread.sleep(1000);
        System.out.println("Adder Result : " + adder.sum());

        /*
        *   incrementAndGet : 2 sleep : 0 time : 1586310463412
            incrementAndGet : 3 sleep : 0 time : 1586310463412
            incrementAndGet : 2 sleep : 0 time : 1586310463412
            incrementAndGet : 4 sleep : 0 time : 1586310463413
            incrementAndGet : 5 sleep : 0 time : 1586310463413
            incrementAndGet : 7 sleep : 0 time : 1586310463413
            incrementAndGet : 6 sleep : 0 time : 1586310463413
            incrementAndGet : 9 sleep : 0 time : 1586310463413
            incrementAndGet : 8 sleep : 0 time : 1586310463413
            incrementAndGet : 10 sleep : 0 time : 1586310463413
            Adder Result : 10
        * */


        /*
         * Accumulator
         * */
        LongAccumulator accumulator = new LongAccumulator(Math::max, 0L);
        IntStream.range(0, 10).forEach(i -> {
            threadPool.submit(new TaskWithAccumulator(accumulator, 0));
        });
        Thread.sleep(1000);
        System.out.println("Accumulator Result : " + accumulator.get());

        /*
        *   random number : 3 sleep : 0 time : 1586310790783
            random number : 7 sleep : 0 time : 1586310790783
            random number : 1 sleep : 0 time : 1586310790783
            random number : 5 sleep : 0 time : 1586310790783
            random number : 4 sleep : 0 time : 1586310790783
            random number : 6 sleep : 0 time : 1586310790783
            random number : 2 sleep : 0 time : 1586310790783
            random number : 8 sleep : 0 time : 1586310790783
            random number : 4 sleep : 0 time : 1586310790783
            random number : 1 sleep : 0 time : 1586310790783
            Accumulator Result : 8
        * */
    }
}
