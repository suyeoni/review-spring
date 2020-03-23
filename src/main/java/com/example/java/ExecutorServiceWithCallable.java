package com.example.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceWithCallable {

    static class Task implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            Thread.sleep(3000);
            return new Random().nextInt();
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<String> future = executorService.submit(() -> {
            System.out.println("just return if task completes");
        }, "done");
        future.get(); // blocking

        // future is just placeholder
        Future<Integer> future2 = executorService.submit(new Task());

        Thread.sleep(1000);
        // perform unrelated operations

        Integer result = future2.get(); // but it will get result after 2 sec
        System.out.println(result);

        future2.get(3, TimeUnit.SECONDS); // time check

        List<Future<Integer>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futureList.add(executorService.submit(new Task()));
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(futureList.get(i));
        }

        // if task already in progress, do not cancel
        future.cancel(false);
        // though task already in progress, cancel
        future.cancel(true);

        future.isCancelled();
        future.isDone();

    }
}
