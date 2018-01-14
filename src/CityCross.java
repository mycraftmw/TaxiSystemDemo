/**
 * Overview:
 * The {@code CityCross} class implements the abstract encapsulation for each crossing.
 * <br>
 * 表示对象：{@code int x, int y; }分别表示x坐标和y坐标。
 * <br>
 * 抽象函数：{@code AF(c) = (x,y); }表示路口坐标。
 * <br>
 * 不定式：{@code !(x < 0 || x >= 80 || y < 0 || y >= 80);}
 */
public class CityCross {
	private int x;
	private int y;

	/**
	 * Requires:x，y大于等于0。<br>
	 * Modifies:无。<br>
	 * Effects:初始化私有属性x和y。<br>
	 *
	 * @param x 路口位置x坐标。
	 * @param y 路口位置y坐标。
	 */
	public CityCross(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回私有属性x的值。<br>
	 *
	 * @return 路口位置x坐标。
	 */
	public int getX() {
		return x;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回私有属性y的值。<br>
	 *
	 * @return 路口位置y坐标。
	 */
	public int getY() {
		return y;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:如果x和y都在[0,79]中的时候返回true，否则返回false。<br>
	 *
	 * @return 路口是否合法。
	 */
	public boolean repOk() {
		return !(x < 0 || x >= 80 || y < 0 || y >= 80);
	}

	/**
	 * Requires:一个Direction值。<br>
	 * Modifies:无。<br>
	 * Effects:返回当前路口向传入方向移动之后的到达的路口。<br>
	 *
	 * @param direction 移动方向。
	 *
	 * @return 移动后的路口。
	 */
	public CityCross moveOn(Direction direction) {
		return direction == null ? this : new CityCross(x + direction.getX(), y + direction.getY());
	}

	/**
	 * Requires:一个对象。<br>
	 * Modifies:无。<br>
	 * Effects:如果传入对象是路口并且和当前路口为同一个路口返回true。否则返回false。<br>
	 *
	 * @param o 将要比较的对象。
	 *
	 * @return 是否相同。
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CityCross)) return false;
		CityCross cityCross = (CityCross) o;
		return x == cityCross.x && y == cityCross.y;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回路口的详细信息的字符串。<br>
	 *
	 * @return 返回路口的详细信息的字符串。
	 */
	@Override
	public String toString() {
		return "CityCross{" +
				       "x=" + x +
				       ", y=" + y +
				       '}';
	}
}
