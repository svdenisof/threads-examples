package ru.denisov.examples.producerConsumerNotify;

import java.util.Queue;

public class ConsumerWaitNotify implements Runnable {
    private final Queue<Integer> queue;
    private final int maxElements;
    private int consumedCount = 0;

    public ConsumerWaitNotify(final Queue<Integer> theQueue, final int theMaxElement) {
        queue = theQueue;
        maxElements = theMaxElement;
    }

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();
        while (consumedCount < maxElements) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                int value = queue.poll();
                System.out.println("2:["+threadName+"] - Потребитель обработал: " + value);
                consumedCount++;
                queue.notifyAll();
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
