package com.example.java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionLockTest {

    private Lock lock = new ReentrantLock(); // default is fairness : true
    //    private Lock lock = new ReentrantLock(false); // fairness : false
    private Condition condition = lock.newCondition();

    private void thread1() {
        lock.lock(); // like synchronized block
        try {
            condition.await(); // like wait()
        } catch (InterruptedException ignore) { }
        lock.unlock(); // like synchronized block
    }

    private void thread2() {
        lock.lock(); // like synchronized block
        try {
            condition.signal(); // like notify() - fairness FIFO
//            condition.signalAll(); // like notifyAll()
        } catch (Exception ignore) { }
        lock.unlock(); // like synchronized block
    }

    private void thread3() {
        try {
            // lock.tryLock();
            boolean available = lock.tryLock(3, TimeUnit.SECONDS);
            if (available) {
                // do something with shared data
            } else {
                // can't get a lock, but do something without shared data
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static class Shared {
        private Lock lock = new ReentrantLock();
        private Condition added = lock.newCondition();
        private Condition removed = lock.newCondition();
        private List<Integer> myQ = new ArrayList<>();
        private static final int MAX = 10;

        private void produce(Integer num) {
            lock.lock();
            while (myQ.size() == MAX) {
                try {
                    removed.await();
                } catch (InterruptedException ignore) { }
            }
            System.out.println("[produce] " + num);
            myQ.add(num);
            added.signal();
            lock.unlock();
        }

        private Integer consume() {
            lock.lock();
            while (myQ.isEmpty()) {
                try {
                    added.await();
                } catch (InterruptedException ignore) { }
            }
            Integer ret = myQ.remove(0); // get
            removed.signal();
            lock.unlock();
            return ret;
        }
    }

    public static void main(String[] args) {
        Shared shared = new Shared();
        Thread producer = new Thread(() -> {
            int num = 0;
            while (true) {
                shared.produce(num++);
                try {
                    Thread.sleep(100); // producer가 매우 빠름
                } catch (InterruptedException ignore) { }
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                System.out.println("[consume] " + shared.consume());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) { }
            }
        });

        producer.start();
        consumer.start();

        /*
        *   [produce] 0
            [consume] 0 v
            [produce] 1
            [produce] 2
            [produce] 3
            [produce] 4
            [produce] 5
            [produce] 6
            [produce] 7
            [produce] 8
            [produce] 9
            [consume] 1 v
            [produce] 10
            [produce] 11
            [consume] 2 v
            [produce] 12
        * */
    }
}
