import static java.lang.Thread.sleep;

/**
 * Overview:
 * The {@code LightsMap} class implements the encapsulation of city's traffic information.
 * <br>
 * 表现对象：{@code int[][] lights; }
 * 表现城市中的红绿灯信息。
 * <br>
 * 抽象函数：{@code AF(x) = (lights); }
 * 表现城市中的红绿灯信息。
 * <br>
 * 不定式：{@code lights != null;}
 */
public class LightsMap implements Runnable {
	private final int[][] lights;

	/**
	 * Requires:无。<br>
	 * Modifies:红绿灯信息数组。<br>
	 * Effects:初始化当前红绿灯信息图。<br>
	 *
	 * @param lights 红绿灯信息图。
	 */
	public LightsMap(int[][] lights) {
		this.lights = lights;
	}

	@Override
	public void run() {
		while (true) {
			try {
				sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Requires:无。<br>
	 * Modifies:红绿灯信息数组。<br>
	 * Effects:将城市中的所有红绿灯反转。<br>
	 */
	public void changeLights() {
		for (int[] line : lights) for (int i = 0; i < line.length; i++) line[i] = (~line[i]) + 1;
	}

	/**
	 * Requires:当前路口，车的来向和车的去向。<br>
	 * Modifies:无。<br>
	 * Effects:根据相应路口红绿灯信息判断该来向的车是否可以去往将要去的去向。<br>
	 *
	 * @param stayCityCross 当前经过的路口。
	 * @param curDirection  待判断线路的来向。
	 * @param tempDirection 待判断线路的去向。
	 *
	 * @return 该线路是否可以通过当前路口的红绿灯。
	 */
	public boolean checkLights(CityCross stayCityCross, Direction curDirection, Direction tempDirection) {
		int situation = lights[stayCityCross.getX()][stayCityCross.getY()];
		if (situation == 0) return true;
		if (situation > 0)
			return curDirection == Direction.UP || curDirection == Direction.DOWN || curDirection == Direction.RIGHT && tempDirection == Direction.DOWN || curDirection == Direction.LEFT && tempDirection == Direction.UP;
		return curDirection == Direction.LEFT || curDirection == Direction.RIGHT || curDirection == Direction.UP && tempDirection == Direction.RIGHT || curDirection == Direction.DOWN && tempDirection == Direction.LEFT;
	}

	public boolean repOk() {
		return lights != null;
	}
}
