public class Account {
    private long money;
    private STATE state;
	private String accNumber;

	public Account(long money, String accNumber) {
		this.money = money;
		this.accNumber = accNumber;
		this.state = STATE.NORMAL;
	}

    public synchronized long getMoney() {
		return money;
	}

	public synchronized void raise(long amount) {
		if (money > amount) {
			money -= amount;
		} else {
			System.out.println("Can't finish operation: not enough money");
		}
	}

	public synchronized void put(long amount) {
		money += amount;
	}

    public synchronized boolean isBusy() {
		return this.state == STATE.BUSY;
	}

    public synchronized boolean isLocked() {
		return this.state == STATE.LOCKED;
	}

    public synchronized boolean isNormal() {
		return state == STATE.NORMAL;
	}

    public synchronized void setBusy() {
		this.state = STATE.BUSY;
	}

    public synchronized void setLocked() {
		this.state = STATE.LOCKED;
	}

    public synchronized void setNormal() {
		this.state = STATE.NORMAL;
	}

	enum STATE {
		LOCKED, BUSY, NORMAL
	}
}
