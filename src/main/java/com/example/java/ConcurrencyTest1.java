package com.example.java;

public class ConcurrencyTest1 {

    static class SharedData {
        /*volatile*/ boolean flag = true;

        public void read() {
            if/*while*/ (flag) {
                System.out.println("execute");
            }
        }

        public void write() {
            flag = false;
        }
    }

    public static void main(String[] args) {
        SharedData shared = new SharedData();

        Thread reader = new Thread(() -> {
            shared.read();
        });

        Thread writer = new Thread(() -> {
            shared.write();
        });

        writer.start();
        reader.start();
    }
}


