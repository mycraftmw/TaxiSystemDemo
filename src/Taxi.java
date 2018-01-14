import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Overview:
 * The {@code Taxi} class implements the encapsulation of the taxi object.
 * <br>
 * 表现对象：{@code int TIMEUNIT, int WAITUPPERTIME, int STOPUPPERTIME, int num, int id, int credit,
 * int waitTime, Vector<Direction> directions, Direction curDirection, TaxiStatus status,
 * CityCross stayCityCross, CityMap cityMap, LightsMap lightsMap, Request aim, boolean trace; }
 * 表明了单位时间，最高等待时间，最高停止时间，总编号，id，信誉度，等待时间，当前最短路径，最后一次运行方向，运行状态，
 * 当前路口，城市路口地图，城市红绿灯信息图，当前请求，追踪标记。
 * <br>
 * 不定式：
 * 抽象函数：{@code AF(x) = (id, credit, curDirection, status, stayCityCross, aim); }
 * 表明了出租车的id，信誉度，最后一次运行方向，当前状态，当前停留路口，当前请求。
 * <br>
 * 不定式：{@code id > 0 && id <= 100 && credit >= 0 && stayCityCross != null && cityMap != null && lightsMap != null;}
 */
public class Taxi {
	protected static final int TIMEUNIT = 100;
	protected static final int WAITUPPERTIME = 20000;
	protected static final int STOPUPPERTIME = 1000;
	protected static int num = 0;
	protected int id = ++num;
	protected int credit = 0;
	protected int waitTime = 0;
	protected Vector<Direction> directions = new Vector<>();
	protected Direction curDirection;
	protected TaxiStatus status = TaxiStatus.WAIT;
	protected CityCross stayCityCross;
	protected CityMap cityMap;
	protected LightsMap lightsMap;
	protected Request aim;
	protected boolean trace;

	/**
	 * Requires:传入一个出租车现在的位置，一张城市地图和一幅城市红绿灯信息图。<br>
	 * Modifies:修改自身属性的位置和城市地图。<br>
	 * Effects:初始化该对象。<br>
	 *
	 * @param stayCityCross 出租车当前位置。
	 * @param cityMap       当前城市地图信息。
	 * @param lightsMap     当前城市红绿灯信息。
	 */
	public Taxi(CityCross stayCityCross, CityMap cityMap, LightsMap lightsMap) {
		this.stayCityCross = stayCityCross;
		this.cityMap = cityMap;
		this.lightsMap = lightsMap;
		this.curDirection = Direction.UP;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:修改状态和相关的状态信息。<br>
	 * Effects:模拟出租车运行的过程。<br>
	 */
	public void run() {
		Direction tempDirection;
		CityCross nextCityCross;
		switch (status) {
			case WAIT:
				waitTime += TIMEUNIT;
				int minFlow = cityMap.getFlow(stayCityCross, Direction.UP);
				for (Direction direction : Direction.values())
					minFlow = minFlow > cityMap.getFlow(stayCityCross, direction) ? cityMap.getFlow(stayCityCross, direction) : minFlow;
				ArrayList<Direction> temp = new ArrayList<>();
				for (Direction direction : Direction.values())
					if (cityMap.getFlow(stayCityCross, direction) == minFlow) temp.add(direction);
				do {
					tempDirection = temp.get(new Random().nextInt(temp.size()));
					nextCityCross = stayCityCross.moveOn(tempDirection);
				} while (!nextCityCross.repOk() || !cityMap.canMove(stayCityCross, nextCityCross));
				if (!lightsMap.checkLights(stayCityCross, curDirection, tempDirection)) return;
				curDirection = tempDirection;
				stayCityCross = nextCityCross;
				if (waitTime == WAITUPPERTIME) {
					waitTime = 0;
					status = TaxiStatus.STOP;
				}
				break;
			case READY:
				directions = cityMap.getTheShortestWay(stayCityCross, aim.getStart(),false);
				tempDirection = directions.remove(0);
				if (!lightsMap.checkLights(stayCityCross, curDirection, tempDirection)) return;
				curDirection = tempDirection;
				stayCityCross = stayCityCross.moveOn(curDirection);
				if (directions.isEmpty()) {
					directions = cityMap.getTheShortestWay(aim.getStart(), aim.getEnd(),false);
					status = TaxiStatus.STOP;
				}
				break;
			case WORK:
				directions = cityMap.getTheShortestWay(stayCityCross, aim.getEnd(),false);
				tempDirection = directions.remove(0);
				if (!lightsMap.checkLights(stayCityCross, curDirection, tempDirection)) return;
				curDirection = tempDirection;
				stayCityCross = stayCityCross.moveOn(curDirection);
				if (directions.isEmpty()) {
					credit += 3;
					System.out.println("taxi No." + id + " finished the " + aim + " at " + MyTimer.getMyTimer());
					System.out.println("now taxi No." + id + " credit: " + credit);
					aim = null;
					status = TaxiStatus.STOP;
				}
				break;
			case STOP:
				waitTime += TIMEUNIT;
				if (waitTime == STOPUPPERTIME) {
					waitTime = 0;
					if (aim == null) status = TaxiStatus.WAIT;
					else status = TaxiStatus.WORK;
				}
				break;
		}
		if (trace) System.out.println(this);
	}

	/**
	 * Requires:传入一个不为空的请求。<br>
	 * Modifies:修改出租车的当前需要完成的单子，并进行相应的状态转移和修改相关状态信息，并输出相关信息。<br>
	 * Effects:模拟出租车抢单成功。<br>
	 *
	 * @param request 待完成请求。
	 */
	public void carryRequest(Request request) {
		System.out.println("taxi No." + id + " take the " + request + "at " + MyTimer.getMyTimer());
		aim = request;
		waitTime = 0;
		if (stayCityCross.equals(aim.getStart())) status = TaxiStatus.STOP;
		else status = TaxiStatus.READY;
	}

	/**
	 * Requires:传入一个正整数。<br>
	 * Modifies:修改信誉度。<br>
	 * Effects:将信誉度增加num。<br>
	 *
	 * @param num 信誉度增量。
	 */
	public void addCredit(int num) {
		credit += num;
	}

	/**
	 * Requires:传入一个可达的路口。<br>
	 * Modifies:无。<br>
	 * Effects:返回出租车距离这个路口的距离，若无法到达则返回0。<br>
	 *
	 * @param cityCross 目标路口。
	 *
	 * @return 出租车当前位置到目标路口的距离。
	 */
	public int howFar(CityCross cityCross) {
		return cityMap.getTheShortestWay(stayCityCross, cityCross, false).size();
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回出租车的信誉度。<br>
	 *
	 * @return 出租车的信誉度。
	 */
	public int getCredit() {
		return credit;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回出租车的id。<br>
	 *
	 * @return 出租车的id。
	 */
	public int getId() {
		return id;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回出租车的位置。<br>
	 *
	 * @return 当前所在路口。
	 */
	public CityCross getStayCityCross() {
		return stayCityCross;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回最近一次出租车运动的方向。<br>
	 *
	 * @return 最近一次出租车运动的方向。
	 */
	public Direction getCurDirection() {
		return curDirection;
	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:判断出租车是否为等待服务状态，若是，返回true，不是，返回false。<br>
	 *
	 * @return 是否处于等待服务状态。
	 */
	public boolean isWaiting() {
		return status == TaxiStatus.WAIT;
	}

	/**
	 * Requires:传入一个布尔值。<br>
	 * Modifies:修改当前出租车是否被追踪。<br>
	 * Effects:修改出租车的是否被追踪状态。<br>
	 *
	 * @param trace 是否被追踪。
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	/**
	 * Requires:传入一个对象。<br>
	 * Modifies:无。<br>
	 * Effects:如果传入的不是出租车，返回false，如果是出租车并且id相同，返回false。<br>
	 *
	 * @param obj 将要比较的对象。
	 *
	 * @return 当前对象是否与待比较对象相同。
	 */
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Taxi && id == ((Taxi) obj).id;

	}

	/**
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回包含该出租车部分信息的字符串。<br>
	 *
	 * @return 包含该出租车部分信息的字符串。
	 */
	@Override
	public String toString() {
		return "Taxi{" +
				       "id=" + id +
				       ", status=" + status +
				       ", now=" + MyTimer.getMyTimer() +
				       ", stayCityCross=" + stayCityCross +
				       ", credit=" + credit +
				       '}';
	}

	public boolean repOk() {
		return id > 0 && id <= 100 && credit >= 0 && stayCityCross != null && cityMap != null && lightsMap != null;
	}

}
