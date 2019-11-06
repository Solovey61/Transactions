import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BankTest {
    Bank bank;
    Bank bankWithLockedAccounts;

    @Before
    public void init() {
        bank = new Bank();
        for (int i = 0; i < 10; i++) {
            bank.addAccount(String.valueOf(i), 500000);
        }

        bankWithLockedAccounts = new Bank();
        for (int i = 0; i < 10; i++) {
            bankWithLockedAccounts.addAccount(String.valueOf(i), 500000);
            bankWithLockedAccounts.getAccount(String.valueOf(i)).setLocked();
        }
    }

    @Test
    public void testTransferBetweenLockedAccounts() throws InterruptedException {
        bankWithLockedAccounts.transfer("0", "1", 30000);
        bankWithLockedAccounts.transfer("1", "0", 10000);
        bankWithLockedAccounts.transfer("2", "3", 5000);
        long[] arrayOfMoney = new long[4];
        long[] arrayExpected = new long[4];
        for (int i = 0; i < arrayOfMoney.length; i++) {
            arrayOfMoney[i] = bankWithLockedAccounts.getBalance(String.valueOf(i));
            arrayExpected[i] = 500000;
        }
        Assert.assertArrayEquals(arrayOfMoney, arrayExpected);
    }

    @Test
    public void transferMoreThanFiftyThousandUsingMultithreading() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    bank.transfer("0", "1", 55000);
                    System.out.println(String.format("Balance of account #0 is %s and #1 is %s", bank.getBalance("0"),
                            bank.getBalance("1")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads)
            thread.join();
    }

    @Test
    public void testTransferBetweenNullAccounts() throws InterruptedException {
        bank.transfer("lkj", "1", 30000);
        bank.transfer("5", "kjhkjh", 20000);
        Assert.assertEquals(bank.getBalance("1"), 500000);
        Assert.assertEquals(bank.getBalance("5"), 500000);
    }

    @Test
    public void testDeadLock() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 100000; j++) {
                        bank.transfer("0", "1", 300);
                        bank.transfer("1", "0", 500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
