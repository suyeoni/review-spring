package com.example.java;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class StopTaskWithTimeoutTest2 {

    static class Task implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("[Callable] task start.." + System.currentTimeMillis());
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) { }
                System.out.println("[Callable] task end.." + System.currentTimeMillis());
                return 1;
            }
            System.out.println("[Callable] task stop.." + System.currentTimeMillis());
            return -1;
        }
    }

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<Integer> future = executorService.submit(new Task());

        try {
            future.get(5 * 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException ignore) {
            System.out.println("Exception occurs. " + ignore.getMessage());
        } catch (TimeoutException e) {
            System.out.println("[Callable] attempt to stop task.. " + System.currentTimeMillis());
            boolean cancelled = future.cancel(true);
            System.out.println("Future cancelled " + cancelled);
        }

        /*
        * [Callable] task start..1585720159796
          [Callable] attempt to stop task.. 1585720164800
          Future cancelled true
          [Callable] task end..1585720164800
        * */

        ExecutorService executorService2 = Executors.newFixedThreadPool(1);

        Future<Integer> future2 = executorService2.submit(new Task());

        try {
            Thread.sleep(5 * 1000);
            System.out.println("[Callable] attempt to stop task.. " + System.currentTimeMillis());
            executorService2.shutdownNow();
        } catch (InterruptedException ignore) {
            System.out.println("Exception occurs. " + ignore.getMessage());
        }

        /*
        * [Callable] task start..1585720476500
          [Callable] attempt to stop task.. 1585720481503
          [Callable] task end..1585720481504
        * */
    }
}
