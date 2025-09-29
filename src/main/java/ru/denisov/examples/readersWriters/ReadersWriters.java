package ru.denisov.examples.readersWriters;

public class ReadersWriters {
    private int readers = 0;
    private boolean isWriting = false;

    public synchronized void startReading() throws InterruptedException {
        while (isWriting) {
            wait();
        }
        readers++;
        System.out.println("Читатель начал работу. Всего читателей: " + readers);
    }

    public synchronized void endReading() {
        readers--;
        if (readers == 0) {
           notifyAll();
        }
        System.out.println("Читатель завершил работу. Осталось: " + readers);
    }

    public synchronized void startWriting() throws InterruptedException {
        while (isWriting || readers > 0) {
            wait();
        }
        isWriting = true;
        System.out.println("Писатель начал работу.");
    }

    public synchronized void endWriting() {
        isWriting = false;
        notifyAll();
        System.out.println("Писатель завершил работу.");
    }
}
