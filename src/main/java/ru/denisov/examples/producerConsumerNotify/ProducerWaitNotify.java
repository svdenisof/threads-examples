package ru.denisov.examples.producerConsumerNotify;

import java.util.Queue;

public class ProducerWaitNotify implements Runnable {
    private final Queue<Integer> queue;
    private final int maxSize;
    private final int maxElements;
    private int producedCount = 0;

    public ProducerWaitNotify(final Queue<Integer> theQueue, final int theMaxSize, final int theMaxElement) {
        queue = theQueue;
        maxSize = theMaxSize;
        maxElements = theMaxElement;
    }

    @Override
    public void run() {
        int value = 0;
        var threadName = Thread.currentThread().getName();
        while (producedCount < maxElements) {
            synchronized (queue) {
                while (queue.size() == maxSize) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("2:["+threadName+"] - Производитель создал: " + value);
                queue.add(value++);
                producedCount++;
                queue.notifyAll();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
