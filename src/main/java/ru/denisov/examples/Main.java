package ru.denisov.examples;

import ru.denisov.examples.ats.BankAccount;
import ru.denisov.examples.buffer.BoundedBuffer;
import ru.denisov.examples.producerConsumer.Consumer;
import ru.denisov.examples.producerConsumer.Producer;
import ru.denisov.examples.producerConsumerNotify.ConsumerWaitNotify;
import ru.denisov.examples.producerConsumerNotify.ProducerWaitNotify;
import ru.denisov.examples.readersWriters.ReadersWriters;
import ru.denisov.examples.semaphore.CustomSemaphore;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(final String[] args) throws InterruptedException {

        System.out.println("Hello, World!");

        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);

        Thread producerThread = new Thread(new Producer(blockingQueue));
        Thread consumerThread = new Thread(new Consumer(blockingQueue));

        producerThread.start();
        consumerThread.start();
        //============================================================================
        Queue<Integer> queue = new LinkedList<>();
        int maxSize = 10;
        int maxElements = 20;

        Thread producer = new Thread(new ProducerWaitNotify(queue, maxSize, maxElements));
        Thread consumer = new Thread(new ConsumerWaitNotify(queue, maxElements));

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Программа завершена.");
        //=================================================================================
        BankAccount account = new BankAccount();

        new Thread(() -> {
            try {
                account.withdraw(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            account.deposit(1000);
        }).start();

        //========================================================================================
        BoundedBuffer buffer = new BoundedBuffer(3);

        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.produce(i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.consume();
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
        }).start();

        //=========================================================================
        CustomSemaphore semaphore = new CustomSemaphore(2);

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " вошел в критическую секцию");
                    Thread.sleep(1000);
                    semaphore.release();
                    System.out.println(Thread.currentThread().getName() + " вышел");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        //===========================================================================

        ReadersWriters rw = new ReadersWriters();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try {
                    rw.startWriting();
                    Thread.sleep(1000);
                    rw.endWriting();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    rw.startReading();
                    Thread.sleep(1500);
                    rw.endReading();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}