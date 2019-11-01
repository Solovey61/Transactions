public class Account {
	private volatile long money;
	private volatile STATE state;
	private String accNumber;

	public Account(long money, String accNumber) {
		this.money = money;
		this.accNumber = accNumber;
		this.state = STATE.NORMAL;
	}

	public long getMoney() {
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

	public boolean isBusy() {
		return this.state == STATE.BUSY;
	}

	public boolean isLocked() {
		return this.state == STATE.LOCKED;
	}

	public boolean isNormal() {
		return state == STATE.NORMAL;
	}

	public void setBusy() {
		this.state = STATE.BUSY;
	}

	public void setLocked() {
		this.state = STATE.LOCKED;
	}

	public void setNormal() {
		this.state = STATE.NORMAL;
	}

	enum STATE {
		LOCKED, BUSY, NORMAL
	}
}
