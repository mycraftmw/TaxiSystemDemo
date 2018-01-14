import java.util.Vector;

/**
 * Overview:
 * The {@code CityMap} class implements the encapsulation of the entire city map.
 * <br>
 * 表现对象：{@code int[][] crosses, int[][] flags, int[][] rightFlow, int[][] downFlow; }
 * 分别表示路口信息，路口备份信息，东西向道路流量信息，南北向道路流量信息。
 * <br>
 * 抽象对象：{@code AF(c) = (crosses,flow); }
 * 表示路口路况和车流信息。
 * <br>
 * 不定式：{@code crosses != null && flags != null && rightFlow != null && downFlow != null;}
 */
public class CityMap {
	private final int[][] crosses;
	private final int[][] originalCrosses;
	private int[][] flags;
	private int[][] rightFlow;
	private int[][] downFlow;

	/**
	 * Requires:一个表示城市路口状态的int数组。<br>
	 * Modifies:修改自身属性。<br>
	 * Effects:根据传入城市状态初始化城市地图相关信息。<br>
	 *
	 * @param crosses 城市路口路况信息数组。
	 */
	public CityMap(int[][] crosses) {
		this.crosses = crosses;
		this.originalCrosses = crosses;
		this.flags = new int[crosses.length][crosses[0].length];
		this.rightFlow = new int[crosses.length][crosses[0].length];
		this.downFlow = new int[crosses.length][crosses[0].length];
		clearFlow();
	}

	/**
	 * Requires:传入3个参数，前两个为不为空的路口，一个起始地，一个目的地。后一个为出租车是否为VipTaxi。<br>
	 * Modifies:无。<br>
	 * Effects:返回起始地到目的地的当前最短距离且第一个方向流量最小的路径。<br>
	 *
	 * @param start    起始地口
	 * @param end      终点路口
	 * @param ifVip    是否为VipTaxi
	 *
	 * @return 起始路口到终点路口的最短路。
	 */
	public synchronized Vector<Direction> getTheShortestWay(CityCross start, CityCross end, boolean ifVip) {
		Vector<Direction> ways = new Vector<>();
		Vector<Direction> ans = new Vector<>();
		if (start.equals(end)) return ways;
		CityCross[] queue = new CityCross[6500];
		int[] from = new int[6500];
		int[] step = new int[6500];
		boolean[][] visited = new boolean[85][85];
		Direction[] directions = new Direction[6500];
		int head = 0, tail = 1;
		queue[head] = start;
		visited[start.getX()][start.getY()] = true;
		while (head < tail) {
			if (ans.size() > 0 && step[head] > ans.size()) return ans;
			CityCross now = queue[head];
			for (Direction direction : Direction.values()) {
				CityCross nextCityCross = now.moveOn(direction);
				if (!nextCityCross.repOk()) continue;
				if (visited[nextCityCross.getX()][nextCityCross.getY()]) continue;
				if ((!ifVip && !canMove(now, nextCityCross)) || (ifVip && !canVipMove(now, nextCityCross))) continue;
				if (nextCityCross.equals(end)) {
					ways.clear();
					ways.add(direction);
					for (int i = head; i > 0; i = from[i]) ways.add(0, directions[i]);
					if (ans.size() == 0) ans = ways;
					else if (getFlow(start, ans.get(0)) > getFlow(start, ways.get(0)))
						ans = ways;
				} else {
					visited[nextCityCross.getX()][nextCityCross.getY()] = true;
					queue[tail] = nextCityCross;
					from[tail] = head;
					step[tail] = step[head] + 1;
					directions[tail] = direction;
					tail++;
				}
			}
			head++;
		}
		return ans;
	}



	/**
	 * Requires:无。<br>
	 * Modifies:初始化两个Flow数组。<br>
	 * Effects:清除车流信息。<br>
	 */
	public void clearFlow() {
		for (int i = 0; i < crosses.length; i++)
			for (int j = 0; j < crosses[0].length; j++) {
				downFlow[i][j] = rightFlow[i][j] = Integer.MAX_VALUE;
				if (crosses[i][j] == 1) rightFlow[i][j] = 0;
				else if (crosses[i][j] == 2) downFlow[i][j] = 0;
				else if (crosses[i][j] == 3) rightFlow[i][j] = downFlow[i][j] = 0;
			}
	}

	/**
	 * Requires:传入一个经过方向移动后的不为空的路口和一个方向。<br>
	 * Modifies:添加Flow数组的相应值。<br>
	 * Effects:增加车流信息。<br>
	 *
	 * @param nowCityCross 当前所在的路口。
	 * @param direction    走到路口所走的方向。
	 */
	public void addFlow(CityCross nowCityCross, Direction direction) {
		if (direction == null) return;
		switch (direction) {
			case UP:
				downFlow[nowCityCross.getX()][nowCityCross.getY()]++;
				break;
			case DOWN:
				if (nowCityCross.getX() > 0)
					downFlow[nowCityCross.getX() - 1][nowCityCross.getY()]++;
				break;
			case LEFT:
				rightFlow[nowCityCross.getX()][nowCityCross.getY()]++;
				break;
			case RIGHT:
				if (nowCityCross.getY() > 0)
					rightFlow[nowCityCross.getX()][nowCityCross.getY() - 1]++;
		}
	}

	/**
	 * Requires:传入一个不为空的路口和一个方向。<br>
	 * Modifies:无。<br>
	 * Effects:返回这个路口在这个方向上的车流信息。<br>
	 *
	 * @param cityCross 待查的路口。
	 * @param direction 待查道路在待查路口的方向。
	 *
	 * @return 待查道路的车流大小。
	 */
	public int getFlow(CityCross cityCross, Direction direction) {
		switch (direction) {
			case UP:
				if (cityCross.getX() > 0)
					return downFlow[cityCross.getX() - 1][cityCross.getY()];
				return Integer.MAX_VALUE;
			case DOWN:
				return downFlow[cityCross.getX()][cityCross.getY()];
			case LEFT:
				if (cityCross.getY() > 0)
					return rightFlow[cityCross.getX()][cityCross.getY() - 1];
				return Integer.MAX_VALUE;
			case RIGHT:
				return rightFlow[cityCross.getX()][cityCross.getY()];
		}
		return 0;
	}

	/**
	 * Requires:传入两个相邻的路口。<br>
	 * Modifies:无。<br>
	 * Effects:如果两个路口相通，返回true，否则返回false。<br>
	 *
	 * @param now           当前的路口。
	 * @param nextCityCross 准备检查的路口。
	 *
	 * @return 两个路口是否想通。
	 */
	public boolean canMove(CityCross now, CityCross nextCityCross) {
		if (nextCityCross.getY() > now.getY())
			return crosses[now.getX()][now.getY()] == 3 || crosses[now.getX()][now.getY()] == 1;
		if (nextCityCross.getX() > now.getX())
			return crosses[now.getX()][now.getY()] == 3 || crosses[now.getX()][now.getY()] == 2;
		if (nextCityCross.getY() < now.getY())
			return crosses[nextCityCross.getX()][nextCityCross.getY()] == 3 || crosses[nextCityCross.getX()][nextCityCross.getY()] == 1;
		return crosses[nextCityCross.getX()][nextCityCross.getY()] == 3 || crosses[nextCityCross.getX()][nextCityCross.getY()] == 2;
	}

	private boolean canVipMove(CityCross now, CityCross nextCityCross) {
		if (nextCityCross.getY() > now.getY())
			return originalCrosses[now.getX()][now.getY()] == 3 || originalCrosses[now.getX()][now.getY()] == 1;
		if (nextCityCross.getX() > now.getX())
			return originalCrosses[now.getX()][now.getY()] == 3 || originalCrosses[now.getX()][now.getY()] == 2;
		if (nextCityCross.getY() < now.getY())
			return originalCrosses[nextCityCross.getX()][nextCityCross.getY()] == 3 || originalCrosses[nextCityCross.getX()][nextCityCross.getY()] == 1;
		return originalCrosses[nextCityCross.getX()][nextCityCross.getY()] == 3 || originalCrosses[nextCityCross.getX()][nextCityCross.getY()] == 2;
	}
	/**
	 * Requires:传入一个路口和希望变成的道路信息。<br>
	 * Modifies:修改了路口当前道路信息和城市备份道路信息。<br>
	 * Effects:如果修改成功返回true，失败则返回false。<br>
	 *
	 * @param cityCross 待修改路口。
	 * @param road      希望改成的路况。
	 *
	 * @return 是否修改成功。
	 */
	public synchronized boolean markRoad(CityCross cityCross, int road) {
		if (road < 0 || road > 3) return false;
		if (!cityCross.repOk()) return false;
		int origin = flags[cityCross.getX()][cityCross.getY()] != 0 ? flags[cityCross.getX()][cityCross.getY()] : crosses[cityCross.getX()][cityCross.getY()];
		switch (origin) {
			case 0:
				return false;
			case 1:
				if (road > 1) return false;
				crosses[cityCross.getX()][cityCross.getY()] = road;
				flags[cityCross.getX()][cityCross.getY()] = road ^ 1;
				return true;
			case 2:
				if (road != 0 && road != 2) return false;
				crosses[cityCross.getX()][cityCross.getY()] = road;
				flags[cityCross.getX()][cityCross.getY()] = road ^ 2;
				return true;
			case 3:
				crosses[cityCross.getX()][cityCross.getY()] = road;
				flags[cityCross.getX()][cityCross.getY()] = road == 3 ? 0 : 3;
				return true;
		}
		return false;
	}

	public boolean repOk() {
		return crosses != null && flags != null && rightFlow != null && downFlow != null;
	}
}
