package bank;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentBank {
    private final CopyOnWriteArrayList<BankAccount> accounts;
    private AtomicLong idCounter = new AtomicLong(0);

    public ConcurrentBank() {
        this.accounts = new CopyOnWriteArrayList<>();
    }

    public BankAccount createAccount(long initAmount) {
        BankAccount newAccount = new BankAccount(idCounter.getAndIncrement(), initAmount);
        accounts.add(newAccount);
        return newAccount;
    }

    public void transfer(BankAccount from, BankAccount to, long amount) {
        if (from.getId() == to.getId()) {
            return;
        }

        long fromId = from.getId();
        long toId = to.getId();

        BankAccount first = fromId < toId ? from : to;
        BankAccount second = fromId < toId ? to : from;

        synchronized (first.getLock()) {
            synchronized (second.getLock()) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public long getTotalBalance() {
        long res = 0;
        for (BankAccount acc : accounts) {
            res += acc.getBalance();
        }
        return res;
    }
}
