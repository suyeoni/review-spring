package com.example.java;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ReadWriteLockTest {

    static class Shared {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReadLock readLock = lock.readLock();
        WriteLock writeLock = lock.writeLock();
        Condition numWriteCondition = writeLock.newCondition();
        Condition strWriteCondition = writeLock.newCondition();

        StringBuilder test = new StringBuilder();

        public void read() {
            readLock.lock();
            // readLock.newCondition(); throw UnsupportedOperationException;
            System.out.println("[read] " + test);
            readLock.unlock();
        }

        public void writeString() {
            writeLock.lock();
            while (test.length() % 5 != 0) {
                try {
                    strWriteCondition.await(); // await 는 loop나 condition안에 있어야..
                } catch (InterruptedException ignore) { }
            }
            // write string
            System.out.println("[write] abc");
            test.append("abc");
            numWriteCondition.signal();
            writeLock.unlock();
        }

        public void writeNumber() {
            writeLock.lock();
            while (test.length() % 5 == 0) {
                try {
                    numWriteCondition.await();
                } catch (InterruptedException ignore) { }
            }
            // write num
            System.out.println("[write] 123");
            test.append("123");
            strWriteCondition.signal();
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        Shared shared = new Shared();
        for (int j = 0; j < 3; j++) {
            Thread strWriter = new Thread(() -> {
                while (true) {
                    shared.writeString();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) { }
                }
            });
            strWriter.start();
            Thread numWriter = new Thread(() -> {
                while (true) {
                    shared.writeNumber();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) { }
                }
            });
            numWriter.start();
            for (int i = 0; i < 10; i++) {
                Thread reader = new Thread(() -> {
                    while (true) {
                        shared.read();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignore) { }
                    }
                });
                reader.start();
            }
        }

    }
}
