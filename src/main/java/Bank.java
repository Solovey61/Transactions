import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {
    private final Random random = new Random();
    private ConcurrentHashMap<String, Account> accounts;

    public Bank() {
        accounts = new ConcurrentHashMap<>();
    }

    private synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void addAccount(String accNumber, long money) {
        accounts.put(accNumber, new Account(money, accNumber));
    }

    /**
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */

    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Account fromAccount = getAccount(fromAccountNum);
        Account toAccount = getAccount(toAccountNum);

        if (fromAccount == toAccount) {
            System.err.println("Can't transfer money to the same account");
            return;
        }

        if (fromAccount == null) {
            System.err.println(String.format("Account #%s was not found", fromAccountNum));
            return;
        }
        if (toAccount == null) {
            System.err.println(String.format("Account #%s was not found", toAccountNum));
            return;
        }

        boolean isBusy = fromAccount.isBusy() || toAccount.isBusy();
        if (isBusy)
            System.out.println("At least one account is busy. Should wait...");
        while (isBusy) {
            isBusy = fromAccount.isBusy() || toAccount.isBusy();
        }

        Account lockFirst = fromAccountNum.compareTo(toAccountNum) > 0 ? fromAccount : toAccount;
        Account lockSecond = lockFirst == fromAccount ? toAccount : fromAccount;

        synchronized (lockFirst) {
            synchronized (lockSecond) {
                if (fromAccount.isLocked() || toAccount.isLocked()) {
                    System.out.println(fromAccount.isLocked() ? toAccount.isLocked() ?
                            String.format("Accounts #%s and #%s are locked", fromAccountNum, toAccountNum) :
                            String.format("Account #%s is locked", fromAccountNum) :
                            String.format("Account #%s is locked", toAccountNum));
                }
                if (fromAccount.isNormal() && toAccount.isNormal()) {
                    fromAccount.setBusy();
                    toAccount.setBusy();
                    if (getBalance(fromAccountNum) >= amount) {
                        fromAccount.raise(amount);
                        toAccount.put(amount);
                    }
                    if (amount > 50000 && isFraud(fromAccountNum, toAccountNum, amount)) {
                        fromAccount.setLocked();
                        toAccount.setLocked();
                        System.out.println(String.format("Transfer %1$s from account #%2$s to #%3$s is finished but " +
                                "in result of " +
                                "security check accounts #%2$s and #%3$s are locked", amount, fromAccountNum,
                                toAccountNum));
                    } else {
                        fromAccount.setNormal();
                        toAccount.setNormal();
                        System.out.println(String.format("Transfer %s from %s to %s is finished successfully", amount
                                , fromAccountNum, toAccountNum));
                    }
                }
            }
        }
    }

    public Account getAccount(String number) {
        return accounts.get(number);
    }

    public long getBalance(String accountNum) {
        return getAccount(accountNum).getMoney();
    }

    public ConcurrentHashMap<String, Account> getAccounts() {
        return accounts;
    }
}
