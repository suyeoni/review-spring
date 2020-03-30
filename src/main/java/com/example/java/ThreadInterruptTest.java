package com.example.java;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadInterruptTest {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            int num = 0;
            while (true) {
                System.out.println("num : " + ++num);
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Interrupt occurs.");
                    boolean inter = Thread.interrupted();
                    System.out.println("Interrupt state : " + inter);
                    inter = Thread.interrupted(); // reset state
                    System.out.println("Interrupt state : " + inter);
                    return;
                }
            }
        });
        thread.start();
        thread.interrupt();

        /*
        *   num : 1
            Interrupt occurs.
            Interrupt state : true
            Interrupt state : false
        * */

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Integer> future = executorService.submit(() -> {
            throw new Exception("test");
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            System.out.println("catch InterruptedException! " + e.getMessage());
        } catch (ExecutionException e) {
            System.out.println("catch ExecutionException! " + e.getMessage());
        }

    }
}
