package ru.denisov.examples.producerConsumer;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private final BlockingQueue<Integer> queue;

    public Consumer(final BlockingQueue<Integer> theQueue) {
        queue = theQueue;
    }

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();
        try {
            while (true) {
                Integer value = queue.take();
                System.out.println("1:["+threadName+"] - Потребитель обработал: " + value);
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
