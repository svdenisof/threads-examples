package ru.denisov.examples.producerConsumer;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<Integer> queue;

    public Producer(final BlockingQueue<Integer> theQueue) {
        queue = theQueue;
    }

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println("1:["+threadName+"] - Производитель создал: " + i);
                queue.put(i);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
