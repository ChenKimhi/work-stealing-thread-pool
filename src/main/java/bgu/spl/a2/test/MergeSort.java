package bgu.spl.a2.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    /**
     * sorts the array using work staeling thread pool
     */
    @Override
    protected void start() {
        List<Task<int[]>> tasks = new ArrayList<>();
        int size = array.length;
        if (array != null && size >= 2) {

            int mid = size / 2;
            int[] arr1 = new int[mid];
            int[] arr2 = new int[size - mid];
            for (int i = 0; i < mid; i++) {
                arr1[i] = array[i];
            }
            for (int i = 0; i < size-mid; i++) {
                arr2[i] = array[i + mid];
            }
            MergeSort task1 = new MergeSort(arr1);
            MergeSort task2 = new MergeSort(arr2);
            this.spawn(task1, task2);
            tasks.add(task1);
            tasks.add(task2);


            this.whenResolved(tasks, () -> {

                int[] aSorted;
                int[] bSorted;
                int[] answer;
                aSorted = task1.getResult().get();
                bSorted = task2.getResult().get();
                answer = merge(aSorted, bSorted);

                complete(answer);

            });
        } else{
            this.complete(array);
        }

        // throw new UnsupportedOperationException("Not Implemented Yet.");
    }

    public static int[] merge(int[] arr1, int[] arr2) {

        int ind = 0, i1 = 0, i2 = 0;
        int len1 = arr1.length, len2 = arr2.length;
        int[] ans = new int[len1 + len2];
        while (i1 < len1 & i2 < len2) {
            if (arr1[i1] < arr2[i2]) {
                ans[ind] = arr1[i1];
                i1 = i1 + 1;
            } else {
                ans[ind] = arr2[i2];
                i2 = i2 + 1;
            }
            ind = ind + 1;
        }
        for (int i = i1; i < len1; i = i + 1) {
            ans[ind] = arr1[i];
            ind = ind + 1;
        }
        for (int i = i2; i < len2; i = i + 1) {
            ans[ind] = arr2[i];
            ind = ind + 1;
        }

        return ans;
    }

    public static void main(String[] args) throws InterruptedException {

        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 1000000; // you may check on different number of elements if you
        // like
        int[] array = new Random().ints(n).toArray();

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            // warning - a large print!! - you can remove this line if you wish
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });

        l.await();
        pool.shutdown();

    }

}