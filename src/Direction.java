/**
 * Overview:
 * The {@code Direction} class implements the encapsulation of direction.
 * <br>
 * 表示对象：{@code int x, int y; }
 * 表示方向的向量形式。
 * <br>
 * 抽象函数：{@code AF(x) = (x,y); }
 * 表示方向的向量形式。
 * <br>
 * 不定式：
 */
public enum Direction {
	UP(-1, 0),
	DOWN(1, 0),
	LEFT(0, -1),
	RIGHT(0, 1);

	private int x, y;

	/**
	 * Requires:两个整数表示方向向量。<br>
	 * Modifies:修改私有属性x和y。<br>
	 * Effects:初始化当前对象。<br>
	 *
	 * @param x 方向向量x维度值。
	 * @param y 方向向量y维度值。
	 */
	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回方向向量的第一维度值。<br>
	 *
	 * @return 该方向向量的y维度值。
	 */
	public int getY() {
		return y;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回方向向量的第二维度值。<br>
	 *
	 * @return 该方向向量的x维度值。
	 */
	public int getX() {
		return x;
	}
}