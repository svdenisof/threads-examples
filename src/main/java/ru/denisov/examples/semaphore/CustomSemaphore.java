package ru.denisov.examples.semaphore;

public class CustomSemaphore {
    private int permits;

    public CustomSemaphore(final int thePermits) {
        permits = thePermits;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permits <= 0) {
            wait();
        }
        permits--;
    }

    public synchronized void release() {
        permits++;
        notifyAll();
    }
}
