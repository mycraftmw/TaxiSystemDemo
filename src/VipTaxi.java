import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Overview:
 * The {@code VipTaxi} class implements the encapsulation of the VipTaxi object.
 * <br>
 * 表现对象：{@code super, ArrayList historyRequestsWay, String nowWays}
 * 表明了父类和当前类的历史完成请求记录和当前完成请求记录。
 * <br>
 * 不定式：
 * 抽象函数：{@code AF(x) = (super,historyRequests); }
 * 表明了父类和当前类的历史完成请求记录。
 * <br>
 * 不定式：{@code super.repOk && historyRequestsWay != null}
 */

public class VipTaxi extends Taxi {
	private final ArrayList<String> historyRequestsWay;
	private String nowWays;

	/**
	 * Requires:传入一个出租车现在的位置，一张城市地图和一幅城市红绿灯信息图。<br>
	 * Modifies:修改自身属性的位置和城市地图。<br>
	 * Effects:初始化该对象。<br>
	 *
	 * @param stayCityCross 出租车当前位置。
	 * @param cityMap       当前城市地图信息。
	 * @param lightsMap     当前城市红绿灯信息。
	 */
	public VipTaxi(CityCross stayCityCross, CityMap cityMap, LightsMap lightsMap) {
		super(stayCityCross, cityMap, lightsMap);
		historyRequestsWay = new ArrayList<>();
	}
	/**
	 * Requires:无。<br>
	 * Modifies:修改状态和相关的状态信息。<br>
	 * Effects:模拟出租车运行的过程。<br>
	 * 满足LSP原则，对父类的方法进行改写。
	 */
	@Override
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
				directions = cityMap.getTheShortestWay(stayCityCross, aim.getStart(), true);
				tempDirection = directions.remove(0);
				if (!lightsMap.checkLights(stayCityCross, curDirection, tempDirection)) return;
				curDirection = tempDirection;
				stayCityCross = stayCityCross.moveOn(curDirection);
				if (directions.isEmpty()) {
					directions = cityMap.getTheShortestWay(aim.getStart(), aim.getEnd(), true);
					status = TaxiStatus.STOP;
				}
				break;
			case WORK:
				directions = cityMap.getTheShortestWay(stayCityCross, aim.getEnd(), true);
				tempDirection = directions.remove(0);
				if (!lightsMap.checkLights(stayCityCross, curDirection, tempDirection)) return;
				curDirection = tempDirection;
				stayCityCross = stayCityCross.moveOn(curDirection);
				nowWays += "->(" + stayCityCross.getX() + "," + stayCityCross.getY() + ")";
				if (directions.isEmpty()) {
					credit += 3;
					System.out.println("taxi No." + id + " finished the " + aim + " at " + MyTimer.getMyTimer());
					System.out.println("now taxi No." + id + " credit: " + credit);
					synchronized (historyRequestsWay) {
						historyRequestsWay.add(nowWays);
					}
					nowWays = null;
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
	 * Requires:无。<br>
	 * Modifies:无。<br>
	 * Effects:返回已完成服务次数。<br>
	 *
	 * @return 已完成服务次数。
	 */
	public int getServiceTime() {
		synchronized (historyRequestsWay) {
			return historyRequestsWay.size();
		}
	}

	/**
	 * Requires:传入1个整数，表示需要查询的已完成服务的序号。<br>
	 * Modifies:无。<br>
	 * Effects:返回相应的已完成服务的双向迭代器。<br>
	 *
	 * @param which 待查已完成服务的序号。
	 *
	 * @return 相应的已完成服务的双向迭代器。
	 *
	 * @throws Exception 无相应服务异常。
	 */
	public ListIterator getService(int which) throws Exception {
		synchronized (historyRequestsWay) {
			if (which < 0 || which >= historyRequestsWay.size()) {
				throw new Exception("No Such Service!");
			}
			return new HistoryRequestsGen(historyRequestsWay.get(which));
		}
	}

	/**
	 * Requires:传入一个不为空的请求。<br>
	 * Modifies:修改出租车的当前需要完成的单子，并进行相应的状态转移和修改相关状态信息，并输出相关信息。记录当前服务路程。<br>
	 * Effects:模拟出租车抢单成功。<br>
	 * 满足LSP原则，对父类的方法进行扩写。
	 * @param request 待完成请求。
	 */
	@Override
	public void carryRequest(Request request) {
		super.carryRequest(request);
		nowWays = "(" + request.getStart().getX() + "," + request.getStart().getY() + ")";
	}

	/**
	 * Requires:传入一个可达的路口。<br>
	 * Modifies:无。<br>
	 * Effects:返回出租车距离这个路口的距离，若无法到达则返回0。<br>
	 * 满足LSP原则，对父类的方法进行改写。
	 * @param cityCross 目标路口。
	 *
	 * @return 出租车当前位置到目标路口的距离。
	 */
	@Override
	public int howFar(CityCross cityCross) {
		return cityMap.getTheShortestWay(stayCityCross, cityCross, true).size();
	}

	@Override
	public boolean repOk() {
		return super.repOk() && historyRequestsWay != null;
	}

	/**
	 * Overview:
	 * The {@code HistoryRequestsGen} class implements the encapsulation of the HistoryRequestsGen object.
	 * <br>
	 * 表现对象：{@code String[] waysList, int curIdx;}
	 * 表明了当前迭代对象和当前迭代到的位置。
	 * <br>
	 * 不定式：
	 * 抽象函数：{@code AF(x) = (ListIterator); }
	 * 表明了当前迭代器。
	 * <br>
	 * 不定式：{@code waysList != null}
	 */

	private class HistoryRequestsGen implements ListIterator {
		private String[] waysList;
		private int curIdx;

		/**
		 * Requires:传入路径字符串。<br>
		 * Modifies:this。<br>
		 * Effects:初始化当前迭代器。<br>
		 *
		 * @param ways 路径字符串。
		 */
		public HistoryRequestsGen(String ways) {
			waysList = ways.split("->");
			curIdx = 0;
		}

		/**
		 * Requires:无。<br>
		 * Modifies:无。<br>
		 * Effects:返回迭代器是否有下一个元素。<br>
		 *
		 * @return 迭代器是否有下一个元素。
		 */
		@Override
		public boolean hasNext() {
			return curIdx < waysList.length;
		}

		/**
		 * Requires:无。<br>
		 * Modifies:无。<br>
		 * Effects:返回迭代器的下一个元素，若没有返回null。<br>
		 *
		 * @return 迭代器的下一个元素，或者null。
		 */
		@Override
		public Object next() {
			if (hasNext()) {
				return waysList[curIdx++];
			}
			return null;
		}

		/**
		 * Requires:无。<br>
		 * Modifies:无。<br>
		 * Effects:返回迭代器是否有上一个元素。<br>
		 *
		 * @return 迭代器是否有上一个元素。
		 */
		@Override
		public boolean hasPrevious() {
			return curIdx > 0;
		}

		/**
		 * Requires:无。<br>
		 * Modifies:无。<br>
		 * Effects:返回迭代器的下一个元素，若没有则返回null。<br>
		 *
		 * @return 迭代器的下一个元素，或者null。
		 */
		@Override
		public Object previous() {
			if (hasPrevious())
				return waysList[--curIdx];
			return null;
		}

		/**
		 * Requires:无。<br>
		 * Modifies:无。<br>
		 * Effects:返回迭代器下一个元素的序号，若没有下一个元素返回-1。<br>
		 *
		 * @return 迭代器下一个元素的序号，或者-1。
		 */
		@Override
		public int nextIndex() {
			if (hasNext())
				return curIdx;
			return -1;
		}

		/**
		 * Requires:无。<br>
		 * Modifies:无。<br>
		 * Effects:返回迭代器上一个元素的序号，若没有上一个元素返回-1。<br>
		 *
		 * @return 迭代器上一个元素的序号，或者-1。
		 */
		@Override
		public int previousIndex() {
			if (hasPrevious())
				return curIdx - 1;
			return -1;
		}

		@Override
		public void remove() {
		}

		@Override
		public void set(Object o) {
		}

		@Override
		public void add(Object o) {
		}
	}
}
