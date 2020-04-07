package com.example.java;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForkJoinPoolTest {
    public static void main(String[] args) {
        int[] nums = IntStream.rangeClosed(1, 1000).toArray();
        List<Integer> values = Arrays.stream(nums).boxed().collect(Collectors.toList());
        SumTask task = new SumTask(values);
        int result = new ForkJoinPool().commonPool().invoke(task);
        /*
        *   left : 0, right : 2
            left : 8, right : 10
            left : 3, right : 5
            left : 2, right : 3
            left : 5, right : 7
            left : 7, right : 8
            result : 55
        * */
        System.out.println("result : " + result);
    }

    static class SumTask extends RecursiveTask<Integer> {
        private static final int LIMIT = 10;
        private List<Integer> values;
        private int left;
        private int right;

        public SumTask(List<Integer> values) {
            this(values, 0, values.size());
        }

        public SumTask(List<Integer> values, int left, int right) {
            this.values = values;
            this.left = left;
            this.right = right;
        }

        @Override
        protected Integer compute() {
            int length = right - left;
            if (length <= LIMIT) {
                System.out.println("left : " + left + ", right : " + right + " time : " + System.currentTimeMillis());
                return values.subList(left, right).stream().reduce(0, (acc, ele) -> acc + ele);
            }
            int mid = (right - left) / 2;
            SumTask leftTask = new SumTask(values, left, left + mid);
            leftTask.fork();
            SumTask rightTask = new SumTask(values, left + mid, right);
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();
            return leftResult + rightResult;
        }
    }
}
