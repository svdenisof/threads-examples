package ru.denisov.examples.ats;

public class BankAccount {
    private int balance = 0;

    public synchronized void deposit(final int amount) {
        balance += amount;
        System.out.println("Пополнение на " + amount + ". Баланс: " + balance);
        notifyAll();
    }

    public synchronized void withdraw(final int amount) throws InterruptedException {
        while (balance < amount) {
            System.out.println("Недостаточно средств. Ожидайте...");
            wait();
        }
        balance -= amount;
        System.out.println("Снятие " + amount + ". Баланс: " + balance);
    }
}
