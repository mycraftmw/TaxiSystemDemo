/**
 * Overview:
 * The {@code Simulation} class implements a thread of the encapsulation of the test.
 * <br>
 * 表现对象：{@code UserOperator userOperator; }
 * 表现用户操作类。
 * <br>
 * 抽象函数：{@code AF(x) = (Simulation); }
 * 表现为模拟器。
 * <br>
 * 不定式：{@code userOperator != null;}
 */
public class Simulation implements Runnable {
	private UserOperator userOperator;

	/**
	 * Requires:传入一个用户操作安全对象。
	 * Modifies:修改自身属性的用户修改安全操作类。
	 * Effects:初始化该对象。
	 *
	 * @param userOperator 传入用户操作类。
	 */
	public Simulation(UserOperator userOperator) {
		this.userOperator = userOperator;
	}

	@Override
	public void run() {
//		sample1:

//		for (int i = 0; i < 100; i++) userOperator.setTraceTaxi(i + 1, true);
//		userOperator.addRequest(33, 33, 3, 3);
//		userOperator.showTaxi(2);
//		userOperator.setTraceTaxi(33,true);
//		userOperator.setRoad(2, 2, 0);

//		sample2:

//		Random random = new Random();
//		for (int i = 0; i < 500; i++)
//			userOperator.addRequest(random.nextInt(80), random.nextInt(80), random.nextInt(80), random.nextInt(80));
//		try {
//			sleep(120000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		for (int i = 1; i < 101; i++) {
//			try {
//				ListIterator myIterator = userOperator.getTaxiService(i, 0);
//				while (myIterator.hasNext()) {
//					System.out.println(myIterator.next());
//				}
//				while (myIterator.hasPrevious()) {
//					System.out.println(myIterator.previous());
//				}
//			} catch (Exception ignored) {
//				System.out.println(ignored.getMessage());
//				System.out.println("shit " + i);
//			}
//		}

//		start your code

	}

	public boolean repOk() {
		return userOperator != null;
	}
}
