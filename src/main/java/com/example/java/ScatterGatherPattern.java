package com.example.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScatterGatherPattern {

    public static void main(String[] args) throws Exception {
        System.out.println(getPrices3());
    }

    /*
     * CompletableFuture But not works. Sync job
     * */
    public static Set<String> notWork() throws Exception {
        final Set<String> result = new HashSet<>();

        System.out.println("start : " + System.currentTimeMillis());

        CompletableFuture<String> t1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) { }
            return "t1 " + System.currentTimeMillis();
        });
        CompletableFuture<String> t2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
            return "t2 " + System.currentTimeMillis();
        });
        CompletableFuture<String> t3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) { }
            return "t3 " + System.currentTimeMillis();
        });

        CompletionStage<Set<String>> completableResult =
                CompletableFuture.allOf(t1, t2, t3)
                                 .thenApply(ignoredVoid -> {
                                     result.add(t1.join());
                                     result.add(t2.join());
                                     result.add(t3.join());
                                     // 모두 끝날때까지 기다리기 때문에 하나가 timeout나면 모두 못가져옴
                                     return result;
                                 });

        try {
            ((CompletableFuture<Set<String>>) completableResult).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
            System.out.println("Timeout!");
        }
        return result;
    }

    /*
     * CompletableFuture
     * */
    public static Set<String> getPrices3() throws Exception {

        final Set<String> result = new HashSet<>();

        System.out.println("start : " + System.currentTimeMillis());

        CompletableFuture<Void> task1 = CompletableFuture.runAsync(new Task3(result, "task1", 5000));
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(new Task3(result, "task2", 1000));
        CompletableFuture<Void> task3 = CompletableFuture.runAsync(new Task3(result, "task3", 2000));
        // 아니면 completable future반환에 결과값 넣어서 머지해도 됨
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);
        try {
            allTasks.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
            System.out.println("Timeout!");
            // task1이 아직 안끝나서 발생할거임. 모두 3초안에 돌면 발생안함.
        }
        System.out.println("end : " + System.currentTimeMillis());

        return result;
    }

    static class Task3 implements Runnable {

        private Set<String> result;
        private String name;
        private int sleep;

        public Task3(Set<String> result, String name, int sleep) {
            this.result = result;
            this.name = name;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
                result.add("[" + name + "] " + System.currentTimeMillis());
            } catch (InterruptedException e) {
            }
        }
    }

    /*
     * CountLatch
     * */
    public static Set<String> getPrices2() throws Exception {

        Set<String> result = Collections.synchronizedSet(new HashSet<>());
        CountDownLatch latch = new CountDownLatch(3);

        System.out.println("start : " + System.currentTimeMillis());
        new Thread(new Task2(result, "task1", 5000, latch)).start();
        new Thread(new Task2(result, "task2", 1000, latch)).start();
        new Thread(new Task2(result, "task3", 2000, latch)).start();

        // latch.await(); // 모든것이 끝날때까지 기다림 (따라서 총 5초)
        latch.await(3, TimeUnit.SECONDS); // 3초 까지만 기다림 + 3초안에 모든것이 끝나면 바로리턴
        System.out.println("end : " + System.currentTimeMillis());
        return result;
    }

    static class Task2 implements Runnable {

        private Set<String> result;
        private String name;
        private int sleep;
        private CountDownLatch latch;

        public Task2(Set<String> result, String name, int sleep, CountDownLatch latch) {
            this.result = result;
            this.name = name;
            this.sleep = sleep;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
                result.add("[" + name + "] " + System.currentTimeMillis());
                latch.countDown();
            } catch (InterruptedException e) {
            }
        }
    }

    /*
     * General thread
     * */
    public static Set<String> getPrices1() {

        Set<String> result = Collections.synchronizedSet(new HashSet<>());

        System.out.println("start : " + System.currentTimeMillis());
        new Thread(new Task1(result, "task1", 5000/*1000*/)).start();
        new Thread(new Task1(result, "task2", 1000)).start();
        new Thread(new Task1(result, "task3", 2000)).start();

        try {
            Thread.sleep(3000);
            // task2, 3는 이미 끝났지만 각각 2,1초씩 기다려야됨
            // 만약에 모두 제시간에 끝나면 바로 return 해줘!
        } catch (InterruptedException ignore) { }

        System.out.println("end : " + System.currentTimeMillis());
        return result;
    }

    static class Task1 implements Runnable {

        private Set<String> result;
        private String name;
        private int sleep;

        public Task1(Set<String> result, String name, int sleep) {
            this.result = result;
            this.name = name;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleep);
                result.add("[" + name + "] " + System.currentTimeMillis());
            } catch (InterruptedException e) {
            }
        }
    }
}
