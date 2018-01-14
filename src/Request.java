import java.util.Collections;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Overview:
 * The {@code Request} class implements the encapsulation of passenger requests.
 * <br>
 * 表现对象：{@code CityCross start, CityCross end, long startTime, Vector<Taxi> taxis; }
 * 表现了请求的发出地和目的地还有请求的发出时间和候选出租车队列。
 * <br>
 * 抽象函数：{@code AF(c) = (start,end,startTime); }
 * 表现了请求的发出地和目的地和发出时间。
 * <br>
 * 不定式：{@code !(start.getX() == end.getX() && start.getY() == end.getY()) && !(start.getX() < 0 || start.getY() < 0 || end.getX() < 0 || end.getY() < 0 || start.getX() > 79 || start.getY() > 79 || end.getX() > 79 || end.getY() > 79);}
 */
public class Request {
	private CityCross start, end;
	private long startTime;
	private Vector<Taxi> taxis = new Vector<>();

	/**
	 * Requires:四个整数，前两个表示起始点的坐标，后两个表示目的地的坐标。<br>
	 * Modifies:修改属性中的起始地和目的地，记录请求发出时间。<br>
	 * Effects:初始化该对象。<br>
	 *
	 * @param sx 起始地x坐标。
	 * @param sy 起始地y坐标。
	 * @param ex 目的地x坐标。
	 * @param ey 目的地y坐标。
	 *
	 * @throws Exception 异常。
	 */
	public Request(int sx, int sy, int ex, int ey) throws Exception {
		if (sx == ex && sy == ey) throw new Exception("wrong request");
		if (sx < 0 || sy < 0 || ex < 0 || ey < 0 || sx > 79 || sy > 79 || ex > 79 || ey > 79)
			throw new Exception("wrong request");
		this.start = new CityCross(sx, sy);
		this.end = new CityCross(ex, ey);
		this.startTime = MyTimer.getMyTimer().getTime();
	}

	/**
	 * Requires:传入一个出租车对象。<br>
	 * Modifies:无。<br>
	 * Effects:如果该出租车能抢单则返回true。否则返回false。<br>
	 *
	 * @param taxi 待判断出租车。
	 *
	 * @return 是否能抢单。
	 */
	public boolean canRegister(Taxi taxi) {
		return !taxis.contains(taxi) && !(Math.abs(taxi.getStayCityCross().getX() - start.getX()) > 2 || Math.abs(taxi.getStayCityCross().getY() - start.getY()) > 2);
	}

	/**
	 * Requires:传入一个出租车对象。<br>
	 * Modifies:将出租车添加进候选列表。<br>
	 * Effects:实现出租车抢单。<br>
	 *
	 * @param taxi 抢单的出租车。
	 */
	public void register(Taxi taxi) {
		taxis.add(taxi);
		taxi.addCredit(1);
	}

	/**
	 * Requires:无。<br>
	 * Modifies:将候选列表里的不合格的出租车清除。<br>
	 * Effects:如果没有可选出租车，则返回null，如果存在可选出租车则返回离请求发出点最近的一辆出租车。若存在多辆距离最近的车，则随机返回一辆。<br>
	 *
	 * @return 被选中的出租车。
	 */
	public Taxi chooseTheTaxi() {
		taxis.removeAll(taxis.stream().filter(taxi -> !taxi.isWaiting()).collect(Collectors.toList()));
		if (taxis.isEmpty()) return null;
		Collections.sort(taxis, (o1, o2) -> o2.getCredit() - o1.getCredit() != 0 ? o2.getCredit() - o1.getCredit() : o1.howFar(start) - o2.howFar(start));
		return taxis.get(0);
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回请求发出时间。<br>
	 *
	 * @return 请求发出时间。
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回请求发出地。<br>
	 *
	 * @return 请求发出的路口。
	 */
	public CityCross getStart() {
		return start;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回请求目的地。<br>
	 *
	 * @return 请求目的路口。
	 */
	public CityCross getEnd() {
		return end;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回包含部分请求信息的字符串。<br>
	 *
	 * @return 包含部分请求信息的字符串。
	 */
	@Override
	public String toString() {
		return "Request{" +
				       "start=" + start +
				       ", end=" + end +
				       ", startTime=" + (startTime / 1000.0) + "s" +
				       '}';
	}

	public boolean repOk() {
		return !(start.getX() == end.getX() && start.getY() == end.getY()) && !(start.getX() < 0 || start.getY() < 0 || end.getX() < 0 || end.getY() < 0 || start.getX() > 79 || start.getY() > 79 || end.getX() > 79 || end.getY() > 79);
	}
}
