package com.example.java;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureTest {

    public static void main(String[] args) throws Exception {
        ExecutorService executor1 = Executors.newFixedThreadPool(10);
        ExecutorService executor2 = Executors.newFixedThreadPool(10);

        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.supplyAsync(() -> process1("0"), executor1)
                         .thenApply(str -> process2(str)/*, executor2 // error */)
                         .exceptionally(e -> "error") // runtime error catch
                         .thenApplyAsync(str -> process3(str), executor2)
                         .thenAccept(str -> process4(str));

        Thread.sleep(10000);
    }

    private static String process1(String str) {
        System.out.println("doing process 1 for ... " + str);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) { }
        return str + "1";
    }

    private static String process2(String str) {
//        System.out.println("doing process 2 for ... " + str);
        throw new RuntimeException();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ignore) { }
//        return str + "2";
    }

    private static String process3(String str) {
        System.out.println("doing process 3 for ... " + str);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) { }
        return str + "3";
    }

    private static void process4(String str) {
        System.out.println("done : " + str);
    }
}
