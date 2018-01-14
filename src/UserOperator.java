import java.util.ListIterator;
import java.util.Vector;

/**
 * Overview:
 * The {@code UserOperator} class implements the thread safety the encapsulation of the user action.
 * <br>
 * 表现对象：{@code Vector<Request> requestArrayList, Vector<Taxi> taxis, CityMap cityMap; }
 * 表明了请求队列，出租车队列，和城市路口路况地图。
 * <br>
 * 抽象函数：{@code AF(x) = (UserOperator); }
 * 表明了一个用户操作类。
 * <br>
 * 不定式：{@code requestArrayList != null && taxis != null && cityMap != null;}
 */
public class UserOperator {
	private Vector<Request> requestArrayList;
	private Vector<Taxi> taxis;
	private CityMap cityMap;

	/**
	 * Requires:传入一个出租车列表，一个请求队列，一张城市地图。<br>
	 * Modifies:修改自身的出租车列表，请求队列和城市地图。<br>
	 * Effects:初始化该对象。
	 *
	 * @param taxis            出租车列表。
	 * @param requestArrayList 请求队列。
	 * @param cityMap          城市地图。
	 */
	public UserOperator(Vector<Taxi> taxis, Vector<Request> requestArrayList, CityMap cityMap) {
		this.taxis = taxis;
		this.requestArrayList = requestArrayList;
		this.cityMap = cityMap;
	}

	/**
	 * Requires:传入四个正整数，前两个表示请求发出地的坐标，后两个表示请求目的地的坐标。<br>
	 * Modifies:修改请求队列。<br>
	 * Effects:向请求队列中添加新请求，并输出出租车抢单的相应信息。
	 *
	 * @param sx 出发地x坐标。
	 * @param sy 出发地y坐标。
	 * @param ex 目的地x坐标。
	 * @param ey 目的地y坐标。
	 */
	public void addRequest(int sx, int sy, int ex, int ey) {
		synchronized (requestArrayList) {
			try {
				Request request = new Request(sx, sy, ex, ey);
				taxis.stream().filter(request::canRegister).forEach(taxi -> {
					request.register(taxi);
					System.out.println("taxi No." + taxi.getId() + " taxi register the " + request + " at " + MyTimer.getMyTimer());
				});
				requestArrayList.add(request);
			} catch (Throwable ignored) {
			}
		}
	}

	/**
	 * Requires:传入一个正整数表示出租车id<br>
	 * Modifies:无。<br>
	 * Effects:打印相应id的出租车信息，若无相应id的出租车则没有操作。<br>
	 *
	 * @param i 出租车的id。
	 */
	public void showTaxi(int i) {
		synchronized (taxis) {
			try {
				for (Taxi taxi : taxis)
					if (taxi.getId() == i) {
						System.out.println(taxi);
						return;
					}
			} catch (Throwable ignored) {
			}
		}
	}

	/**
	 * Requires:传入一个正整数表示出租车id。<br>
	 * Modifies:修改相应id的出租车的追踪标记位。<br>
	 * Effects:将相应id的出租车标记为被追踪，或不被追踪。若flag为true则追踪，否则不追踪。<br>
	 *
	 * @param i    出租车的id。
	 * @param flag 是否追踪的标记。
	 */
	public void setTraceTaxi(int i, boolean flag) {
		synchronized (taxis) {
			try {
				for (Taxi taxi : taxis)
					if (taxi.getId() == i) {
						taxi.setTrace(flag);
						return;
					}
			} catch (Throwable ignored) {
			}

		}
	}

	/**
	 * Requires:传入3个整数，前两个表示路口的坐标，最后一个表示想要修改成为的道路信息。<br>
	 * Modifies:修改相应路口的道路信息。<br>
	 * Effects:将相应路口的道路信息更新。<br>
	 *
	 * @param x      路口位置的x坐标。
	 * @param y      路口位置的y坐标。
	 * @param status 路口的右方和下方的路况。
	 */
	public void setRoad(int x, int y, int status) {
		synchronized (cityMap) {
			try {
				if (cityMap.markRoad(new CityCross(x, y), status))
					System.out.println("The city (" + x + "," + y + ") is set to " + status);
			} catch (Throwable ignored) {
			}
		}
	}

	/**
	 * Requires:传入1个整数，表示想要查询已完成服务次数的出租车的id<br>
	 * Modifies:无。<br>
	 * Effects:返回相应出租车的已完成服务次数。若没有对应id的出租车抛出无出租车异常，若该出租车不是VipTaxi抛出非VipTaxi异常。<br>
	 * @param taxiId    出租车的id。
	 * @return 相应出租车的服务次数。
	 * @throws Exception 无出租车异常或非VipTaxi异常。
	 */
	public int getTaxiServiceTime(int taxiId) throws Exception {
		if (taxiId < 1 || taxiId > 100) throw new Exception("No Such Taxi!");
		VipTaxi nowTaxi = null;
		for (Taxi taxi : taxis)
			if (taxi.getId() == taxiId) {
				if (taxi instanceof VipTaxi)
					nowTaxi = (VipTaxi) taxi;
				else throw new Exception("Not a VipTaxi!");
				break;
			}
		return nowTaxi != null ? nowTaxi.getServiceTime() : 0;
	}

	/**
	 * Requires:传入2个整数，第一个表示想要查询已完成服务次数的出租车的id，第二个表示想要查询第几次已完成服务。<br>
	 * Modifies:无。<br>
	 * Effects:返回相应出租车的具体的对应已完成服务的双向迭代器。若没有对应id的出租车抛出无出租车异常，若该出租车不是VipTaxi抛出非VipTaxi异常。
	 * 若没有相应服务抛出无相应服务异常。<br>
	 * @param taxiId       出租车的id。
	 * @param serviceId    对应已完成服务的id。
	 * @return 相应出租车的具体的对应已完成服务的双向迭代器。
	 * @throws Exception 无出租车异常或非VipTaxi异常或无相应服务异常。
	 */
	public ListIterator getTaxiService(int taxiId, int serviceId) throws Exception {
		if (taxiId < 1 || taxiId > 100) throw new Exception("No Such Taxi!");
		VipTaxi nowTaxi = null;
		for (Taxi taxi : taxis)
			if (taxi.getId() == taxiId) {
				if (taxi instanceof VipTaxi)
					nowTaxi = (VipTaxi) taxi;
				else throw new Exception("Not a VipTaxi!");
				break;
			}
		return nowTaxi != null ? nowTaxi.getService(serviceId) : null;
	}

	public boolean repOk() {
		return requestArrayList != null && taxis != null && cityMap != null;
	}
}
