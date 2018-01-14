/**
 * Overview:
 * The {@code MyTimer} class implements the encapsulation of the timer.
 * <br>
 * 表现对象：{@code long time, MyTimer myTimer; }
 * 表现了计时器的计时，和计时器单例。
 * <br>
 * 抽象函数：{@code AF(x) = (Timer); }
 * 表现了计时器实例。
 * <br>
 * 不定式：{@code myTimer != null;}
 */
public class MyTimer {
	private static long time = 0;
	private static MyTimer myTimer;

	private MyTimer() {
	}

	/**
	 * Requires:无。<br>
	 * Modifies:如果自身单例不存在，新建自身单例，若存在则无修改。<br>
	 * Effects:返回计时器单例。<br>
	 *
	 * @return 计时器单例。
	 */
	public synchronized static MyTimer getMyTimer() {
		if (myTimer == null) myTimer = new MyTimer();
		return myTimer;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:修改time加100。<br>
	 * Effects:计时器加100ms。<br>
	 */
	public synchronized void count() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		time += 100;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回时间，单位为ms。<br>
	 *
	 * @return 计时器当前时间。
	 */
	public synchronized long getTime() {
		return time;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回包含计时器信息的字符串。<br>
	 *
	 * @return 包含计时器信息的字符串。
	 */
	@Override
	public synchronized String toString() {
		return time / 1000.0 + "s";
	}

	public boolean repOk() {
		return myTimer != null;
	}
}
