package bank;

public class BankAccount {
    private final long id;
    private long money;
    private final Object lock = new Object();

    public Object getLock() {
        return lock;
    }

    public BankAccount(long id, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be not negative");
        }
        this.id = id;
        this.money = amount;
    }

    public void deposit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be positive");
        }
        synchronized (lock) {
            this.money += amount;
        }
    }

    public void withdraw(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be positive");
        }
        synchronized (lock) {
            if (money < amount) {
                throw new IllegalArgumentException("Not enough funds");
            }
            this.money -= amount;
        }
    }

    public long getBalance() {
        synchronized (lock) {
            return money;
        }
    }

    public long getId() {
        return id;
    }
}
