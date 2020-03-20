package com.example.java;

public class HappensBefore {

    static class SharedData {
        int a = 1;
        int b = 1;
        int c = 1;
        /*volatile*/ int x = 1;
        int d = 1;

        public void read() {
            synchronized (this) {
                System.out.println("x : " + x);
            }
            System.out.println("a : " + a);
            System.out.println("b : " + b);
            System.out.println("c : " + c);
            System.out.println("d : " + d);

        }

        public void write() {
            a = 2;
            b = 2;
            c = 2;
            synchronized (this) {
                x = 2;
            }
            d = 2;
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
