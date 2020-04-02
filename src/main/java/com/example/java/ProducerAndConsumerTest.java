package com.example.java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerAndConsumerTest {

    interface Shared {
        void produce(Integer input);

        Integer consume();
    }

    /*
     * 문제점
     * [if 문]    여러 Thread가 (producer,consumer모두 2이상) 들어오는 경우
     *           lock을 얻지 않았음에도 불구하고 data에 접근할 수 있음 => while로 변경
     * [notify()] notifyAll로 변경. comment 아래 참고
     * */
    static class SharedData1 implements Shared {

        List<Integer> shared = new ArrayList<>();
        private final static int MAX_POOL = 10;

        @Override
        public synchronized void produce(Integer input) {
            if (shared.size() == MAX_POOL) { // -> while로 변경
                try {
                    wait(); // wait/notify는 한 object안에서만 가능 (IllegalMonitorStateException)
                } catch (InterruptedException e) { }
            }
            shared.add(input);
            notify(); // notifyAll로 변경 -> p p p c c 가 큐잉되어있을 때 한개만 깨우면,
            // consumer를 깨우고 싶어도 이 지점까지 3번은 와야 깨어나는데
            // 위의 조건 때문에 계속 wait하면 여기까지 오지도 못해
        }

        @Override
        public synchronized Integer consume() {
            if (shared.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) { }
            }
            Integer ret = shared.remove(0);
            notify();
            return ret;
        }
    }

    /*
     * 개선
     * */
    static class SharedData2 implements Shared {
        List<Integer> shared = new ArrayList<>();
        private final static int MAX_POOL = 10;
        ReentrantLock lock = new ReentrantLock();
        Condition readWait = lock.newCondition();
        Condition writeWait = lock.newCondition();

        /*Object의 wait/notify는 한 object에서만 해야돼*/
        // Object readWait = new Object();
        // Object writeWait = new Object();

        @Override
        public void produce(Integer input) {
            lock.lock();
            try {
                while (shared.size() == MAX_POOL) {
                    try {
                        // writeWait.wait();
                        writeWait.await();
                    } catch (InterruptedException e) { }
                }
                shared.add(input);
                // readWait.notifyAll();
                readWait.signalAll();
            } finally {
                lock.unlock();
            }

        }

        @Override
        public Integer consume() {
            lock.lock();
            try {
                while (shared.isEmpty()) {
                    try {
                        // readWait.wait();
                        readWait.await();
                    } catch (InterruptedException e) { }
                }
                Integer ret = shared.remove(0);
                // writeWait.notifyAll();
                writeWait.signalAll();
                return ret;
            } finally {
                lock.unlock();
            }
        }
    }

    static class Producer implements Runnable {

        Shared shared;
        String name;

        Producer(Shared shared, String name) {
            this.shared = shared;
            this.name = name;
        }

        @Override
        public void run() {
            int num = 0;
            while (true) {
                System.out.println("[" + name + "] " + (num + 1));
                shared.produce(++num);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
        }
    }

    static class Consumer implements Runnable {

        Shared shared;
        String name;

        Consumer(Shared shared, String name) {
            this.shared = shared;
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("[" + name + "] " + shared.consume());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) { }
            }
        }
    }

    public static void main(String[] args) {
        SharedData2 shared = new SharedData2();

        new Thread(new Producer(shared, "Producer 1")).start();
        new Thread(new Producer(shared, "Producer 2")).start();
        new Thread(new Consumer(shared, "Consumer 1")).start();
        new Thread(new Consumer(shared, "Consumer 2")).start();

        /*
         * Blocking queue
         * */
        final int MAX_POOL = 10;
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(MAX_POOL);

        Thread producer = new Thread(() -> {
            int num = 0;
            while (true) {
                try {
                    System.out.println("Produce " + (num + 1));
                    queue.put(++num);
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    if (!queue.isEmpty()) {
                        System.out.println("Consume " + queue.poll());
                    }
                    Thread.sleep(3000);
                } catch (InterruptedException e) { }
            }
        });

        producer.start();
        consumer.start();

    }
}
