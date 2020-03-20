package com.example.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(/*numOfThread*/10);

        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                // default thread name pool-1-thread-#(1~10)
                System.out.println("Thread name : " + Thread.currentThread().getName());
            });
        }

        // it must be main
        System.out.println("Thread name : " + Thread.currentThread().getName());
    }
}
