Вот содержимое файла `ProducerConsumerSolution.md`, который содержит полный ответ с примерами решения задачи "Производитель-Потребитель" на Java с ограничением времени выполнения:

```markdown
# Решение задачи "Производитель-Потребитель" на Java

## Вариант 1: Использование `BlockingQueue` (рекомендуемый способ)

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerExample {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        Thread producerThread = new Thread(new Producer(queue));
        Thread consumerThread = new Thread(new Consumer(queue));

        producerThread.start();
        consumerThread.start();
    }
}

class Producer implements Runnable {
    private final BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println("Производитель создал: " + i);
                queue.put(i);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Integer value = queue.take();
                System.out.println("Потребитель обработал: " + value);
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## Вариант 2: Использование `wait()` и `notify()` с ограничением по количеству элементов

```java
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerWaitNotify {
    public static void main(String[] args) throws InterruptedException {
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
    }
}

class ProducerWaitNotify implements Runnable {
    private final Queue<Integer> queue;
    private final int maxSize;
    private final int maxElements;
    private int producedCount = 0;

    public ProducerWaitNotify(Queue<Integer> queue, int maxSize, int maxElements) {
        this.queue = queue;
        this.maxSize = maxSize;
        this.maxElements = maxElements;
    }

    @Override
    public void run() {
        int value = 0;
        while (producedCount < maxElements) {
            synchronized (queue) {
                while (queue.size() == maxSize) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("Производитель создал: " + value);
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

class ConsumerWaitNotify implements Runnable {
    private final Queue<Integer> queue;
    private final int maxElements;
    private int consumedCount = 0;

    public ConsumerWaitNotify(Queue<Integer> queue, int maxElements) {
        this.queue = queue;
        this.maxElements = maxElements;
    }

    @Override
    public void run() {
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
                System.out.println("Потребитель обработал: " + value);
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
```

## Вариант 3: Ограничение по времени выполнения (таймаут)

```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProducerConsumerWithTimeout {
    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> queue = new LinkedList<>();
        int maxSize = 10;
        int timeoutSeconds = 5;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new ProducerWaitNotify(queue, maxSize, Integer.MAX_VALUE));
        executor.submit(new ConsumerWaitNotify(queue, Integer.MAX_VALUE));

        executor.shutdown();
        if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            System.out.println("Программа остановлена по таймауту.");
        }
    }
}
```

## Сравнение подходов

| Подход                  | Преимущества                          | Недостатки                     |
|-------------------------|---------------------------------------|--------------------------------|
| `BlockingQueue`         | Простота реализации, потокобезопасность | Менее гибкий для сложных сценариев |
| `wait()/notify()`       | Больше контроля над синхронизацией    | Сложнее реализовать правильно  |
| Ограничение по времени  | Позволяет контролировать время работы | Может завершить работу неожиданно |

Рекомендуется использовать `BlockingQueue` для большинства стандартных случаев, а `wait()/notify()` - когда требуется более тонкое управление потоками.

Вот сохранённый ответ в формате Markdown (`.md`). Вы можете скопировать его и сохранить в файл, например, `hoare_monitor_examples.md`.

```markdown
# Монитор Хоара в Java: примеры

Монитор Хоара — это механизм синхронизации, использующий **условные переменные** и **мьютексы**. В Java он реализуется через ключевое слово `synchronized`, а также методы `wait()`, `notify()` и `notifyAll()`.

## 1. Банковский счет с синхронизированными операциями

```java
public class BankAccount {
    private int balance = 0;

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Пополнение на " + amount + ". Баланс: " + balance);
        notifyAll();
    }

    public synchronized void withdraw(int amount) throws InterruptedException {
        while (balance < amount) {
            System.out.println("Недостаточно средств. Ожидание...");
            wait();
        }
        balance -= amount;
        System.out.println("Снятие " + amount + ". Баланс: " + balance);
    }

    public static void main(String[] args) {
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
    }
}
```

**Вывод:**
```
Недостаточно средств. Ожидание...
Пополнение на 1000. Баланс: 1000
Снятие 500. Баланс: 500
```

---

## 2. Ограниченный буфер (Producer-Consumer)

```java
import java.util.LinkedList;
import java.util.Queue;

public class BoundedBuffer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int maxSize;

    public BoundedBuffer(int size) {
        this.maxSize = size;
    }

    public synchronized void produce(int value) throws InterruptedException {
        while (buffer.size() == maxSize) {
            System.out.println("Буфер полон. Производитель ждёт...");
            wait();
        }
        buffer.add(value);
        System.out.println("Добавлено: " + value + " (размер: " + buffer.size() + ")");
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            System.out.println("Буфер пуст. Потребитель ждёт...");
            wait();
        }
        int value = buffer.poll();
        System.out.println("Забрано: " + value + " (размер: " + buffer.size() + ")");
        notifyAll();
        return value;
    }

    public static void main(String[] args) {
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
    }
}
```

**Вывод:**
```
Добавлено: 1 (размер: 1)
Забрано: 1 (размер: 0)
Добавлено: 2 (размер: 1)
Добавлено: 3 (размер: 2)
Забрано: 2 (размер: 1)
...
```

---

## 3. Семафор на основе монитора

```java
public class CustomSemaphore {
    private int permits;

    public CustomSemaphore(int permits) {
        this.permits = permits;
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

    public static void main(String[] args) {
        CustomSemaphore semaphore = new CustomSemaphore(2);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " вошёл в критическую секцию");
                    Thread.sleep(1000);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```

**Вывод:**
```
Thread-0 вошёл в критическую секцию
Thread-1 вошёл в критическую секцию
Thread-2 вошёл в критическую секцию
...
```

---

## 4. Читатели-Писатели (Readers-Writers)

```java
public class ReadersWriters {
    private int readers = 0;
    private boolean isWriting = false;

    public synchronized void startReading() throws InterruptedException {
        while (isWriting) {
            wait();
        }
        readers++;
    }

    public synchronized void endReading() {
        readers--;
        if (readers == 0) notifyAll();
    }

    public synchronized void startWriting() throws InterruptedException {
        while (isWriting || readers > 0) {
            wait();
        }
        isWriting = true;
    }

    public synchronized void endWriting() {
        isWriting = false;
        notifyAll();
    }

    public static void main(String[] args) {
        ReadersWriters rw = new ReadersWriters();
        // Пример использования
    }
}
```

---

### Ключевые моменты:
- **`synchronized`** гарантирует, что только один поток выполняет блок кода.
- **`wait()`** освобождает монитор и переводит поток в ожидание.
- **`notifyAll()`** пробуждает все ожидающие потоки.
- **Цикл `while`** для проверки условий защищает от ложных пробуждений.
```
