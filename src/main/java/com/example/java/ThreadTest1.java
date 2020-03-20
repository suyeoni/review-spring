package com.example.java;

public class ThreadTest1 {
    // static :: 상위 클래스와 상관없이 클래스를 선언하겠다
    static class SharedData {
        // shared
        int num;
        boolean isSet = false;

        // synchronized 없으면 runtime error
        // wait : only be called from a synchronized block
        public synchronized void set(int num) {
            while (isSet) {
                try {
                    wait();
                } catch (InterruptedException ignore) {}
            }
            System.out.println("set num : " + num);
            this.num = num;
            isSet = true;
            notify();  // notify to consumer thread
        }

        public synchronized int get() {
            while (!isSet) {
                try {
                    wait();
                } catch (InterruptedException ignore) {}
            }
            System.out.println("get num : " + num);
            isSet = false;
            notify(); // notify to producer thread
            return num;
        }
    }

    public static void main(String[] args) {
        SharedData shared = new SharedData();

        /*
         * @FunctionalInterface public interface Runnable
         * */
        Thread producer = new Thread(() -> {
            int num = 0;
            while (true) {
                shared.set(num++);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) { }
            }
        });

        Thread consumer = new Thread(() -> {
            int num = 0;
            while (true) {
                shared.get();
                try {
                    // Thread sleep is static method, and doesn't release lock.
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) { }
            }
        });

        producer.start();
        consumer.start();

        // producer가 consumer보다 처리속도가 빠르면 producer에게 다시 nack등을 던져 다시 받아와야한다
        // consumer가 producer보다 빠르면 back-pressure를 통해 consumer가 더 처리 할 수 있으니 더 많이 달라고 할 수 있다
        // -> reactor에서 제공한다
    }

}
