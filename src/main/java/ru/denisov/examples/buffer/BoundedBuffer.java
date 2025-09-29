package ru.denisov.examples.buffer;

import java.util.LinkedList;
import java.util.Queue;

public class BoundedBuffer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int maxSize;

    public BoundedBuffer(final int size) {
        maxSize = size;
    }

    public synchronized void produce(final int value) throws InterruptedException {
        while (buffer.size() == maxSize) {
            System.out.println("Буфер полон. Производитель ждет...");
            wait();
        }
        buffer.add(value);
        System.out.println("Добавлено: " + value + " (размер: " + buffer.size() + ")");
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while(buffer.isEmpty()) {
            System.out.println("Буфер пуст. Потребитель ждет...");
            wait();
        }
        int value = buffer.poll();
        System.out.println("Забрано: " + value + " (размер: " + buffer.size() + ")");
        notifyAll();
        return value;
    }
}
