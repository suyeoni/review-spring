package com.example.java;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("[" + System.currentTimeMillis() + "] Thread name : " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignore) { }
        }
    }

    public static void main(String[] args) {
        /*
         * default RejectedExecutionHandler has 'AbortPolicy' - throw RejectedExecutionException
         * others
         *  - DiscardPolicy : discard new task silently
         *  - DiscardOldestPolicy : discard oldest task in queue, and put new task to queue
         *  - CallerRunsPolicy : executed by caller(if main call executor, then, main executes rejected task)
         * */

        /*
         * fixed thread pool
         * */
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(/*numOfThread*/10);
        // thread num fixed, so task queue size can grow -> linked blocking queue
        /*
        * return new ThreadPoolExecutor(corePoolSize= nThreads(input),
        *                               maximumPoolSize= nThreads(input),
                                        keepAliveTime= 0L,
                                        unit= TimeUnit.MILLISECONDS,
                                        workQueue= new LinkedBlockingQueue<Runnable>(),
                                        threadFactory= Executors.defaultThreadFactory(),
                                        rejectedExecutionHandler= defaultHandler);
        * */
        for (int i = 0; i < 1000; i++) {
            fixedExecutor.execute(new Task());
        }
        System.out.println("Thread name : " + Thread.currentThread().getName());

        /*
         deal with rejected task
          */
        try {
            fixedExecutor.execute(new Task());
        } catch (RejectedExecutionException e) {
            // error
        }

        ExecutorService custom = new ThreadPoolExecutor(10, 10, 0L,
                                                        TimeUnit.MILLISECONDS,
                                                        new LinkedBlockingQueue<Runnable>(),
                                                        Executors.defaultThreadFactory(),
                                                        new RejectedExecutionHandler() {
                                                            @Override
                                                            public void rejectedExecution(Runnable r,
                                                                                          ThreadPoolExecutor executor) {
                                                                // do something
                                                            }
                                                        });

        /*
         * cached thread pool
         * */
        ExecutorService cachedExecutor = Executors.newCachedThreadPool();
        // thread num not fixed, so no need to store task in queue. just pass queue
        // synchoronous queue has only one slot. it's exchange point for a single element between two threads
        /*
        * return new ThreadPoolExecutor(corePoolSize= 0,
        *                               maximumPoolSize= Integer.MAX_VALUE,
                                        keepAliveTime= 60L,
                                        unit= TimeUnit.SECONDS,
                                        workQueue= new SynchronousQueue<Runnable>(),
                                        threadFactory= Executors.defaultThreadFactory(),
                                        rejectedExecutionHandler= defaultHandler);
        * */
        for (int i = 0; i < 1000; i++) {
            // if all thread are busy, then create new thread and place thread to pool
            // if thread is idle for 60 seconds, the kill the thread.
            cachedExecutor.execute(new Task());
        }
        System.out.println("Thread name : " + Thread.currentThread().getName());

        /*
         * scheduled thread pool
         * */
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10 /*fixed*/);
        // special queue for delay (and thread num is fixed, so task queue size can grow)
        // [in DelayedWorkQueue] Resizes the heap array.  Call only when holding lock.
        /*private void grow() {
            int oldCapacity = queue.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1); // grow 50%
            if (newCapacity < 0) // overflow
                newCapacity = Integer.MAX_VALUE;
            queue = Arrays.copyOf(queue, newCapacity);
        }*/
        /*
        * return new ThreadPoolExecutor(corePoolSize= corePoolSize(input),
        *                               maximumPoolSize= Integer.MAX_VALUE,
                                        keepAliveTime= 0L
                                        unit= TimeUnit.MILLISECONDS,
                                        workQueue= new DelayedWorkQueue(),
                                        threadFactory= Executors.defaultThreadFactory(),
                                        rejectedExecutionHandler= defaultHandler);
        * */

        System.out.println("main thread time : " + System.currentTimeMillis());
        // just 1 time after 5 seconds
        scheduledExecutor.schedule(() -> {
            System.out.println("executor.schedule() [" + System.currentTimeMillis()
                               + "] Thread name : " + Thread.currentThread().getName());
        }, 5, TimeUnit.SECONDS);

        // every 3 time repeatedly
        scheduledExecutor.scheduleAtFixedRate(new Task(), 5, 3, TimeUnit.SECONDS);

        // every 3 time repeatedly "after previous task completes"
        // in this case, every 5 (2+3) sec
        scheduledExecutor.scheduleWithFixedDelay(new Task(), 5, 3, TimeUnit.SECONDS);


        /*
         * single thread pool
         * */
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        // thread num fixed, so task queue size can grow -> linked blocking queue
        /*
        * return new ThreadPoolExecutor(corePoolSize= 1,
        *                               maximumPoolSize= 1,
                                        keepAliveTime= 0L
                                        unit= TimeUnit.MILLISECONDS,
                                        workQueue= new LinkedBlockingQueue<Runnable>()),
                                        threadFactory= Executors.defaultThreadFactory(),
                                        rejectedExecutionHandler= defaultHandler);
        * */
        for (int i = 0; i < 1000; i++) {
            // guarantee sequence because there's only one thread
            // recreate thread if thread is killed by task
            singleThreadExecutor.execute(new Task());
        }


        /*
         * Life Cycle
         * */

        try {
            // initiate shutdown
            fixedExecutor.shutdown();
            // true, since shutdown has begun
            fixedExecutor.isShutdown();
            // if there are some tasks not completed, it returns false
            // all tasks are completed, returns true
            // including queued one as well
            fixedExecutor.isTerminated();
            // block until all tasks are completed or if timeout occurs
            fixedExecutor.awaitTermination(3, TimeUnit.SECONDS);
            // initiate shutdown now, and returns all queued tasks.
            List<Runnable> remainedJob = fixedExecutor.shutdownNow();
        } catch (InterruptedException ignore) { }
    }

}
