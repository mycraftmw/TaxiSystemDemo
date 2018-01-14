import java.util.Vector;

/**
 * Overview:
 * The {@code TaxiSchedulingSystem} class implements the encapsulation of the taxiSchedulingSystem object.
 * <br>
 * 表现对象：{@code CityMap cityMap, LightsMap lightsMap, Vector<Taxi> taxis, Vector<Request> requestArrayList; }
 * 表明了城市路况地图，城市红绿灯信息图，出租车列表，请求队列。
 * <br>
 * 抽象函数：{@code AF(c) = (TaxiSchedulingSystem); }
 * 表明了出租车调度系统。
 * <br>
 * 不定式：{@code cityMap != null && lightsMap != null && taxis != null && requestArrayList != null; }
 */
public class TaxiSchedulingSystem implements Runnable {
	private CityMap cityMap;
	private LightsMap lightsMap;
	private Vector<Taxi> taxis;
	private Vector<Request> requestArrayList;

	/**
	 * Requires:传入一张城市地图，一张红绿灯信息图，一个出租车列表，一个请求队列，
	 * Modifies:修改自身属性中的城市地图，出租车列表和请求队列。
	 * Effects:初始化该对象。
	 *
	 * @param cityMap          城市路口地图。
	 * @param lightsMap        红绿灯信息图。
	 * @param taxis            出租车列表。
	 * @param requestArrayList 请求队列。
	 */
	public TaxiSchedulingSystem(CityMap cityMap, LightsMap lightsMap, Vector<Taxi> taxis, Vector<Request> requestArrayList) {
		this.lightsMap = lightsMap;
		this.cityMap = cityMap;
		this.taxis = taxis;
		this.requestArrayList = requestArrayList;
	}

	@Override
	public void run() {
		while (true) synchronized (requestArrayList) {
			synchronized (taxis) {
				synchronized (cityMap) {
					MyTimer.getMyTimer().count();
					if (MyTimer.getMyTimer().getTime() % 300 == 0) lightsMap.changeLights();
					for (int i = 0; i < requestArrayList.size(); i++) {
						Request now = requestArrayList.get(i);
						if (MyTimer.getMyTimer().getTime() - now.getStartTime() < 3000) continue;
						Taxi theTaxi = now.chooseTheTaxi();
						if (theTaxi == null) System.out.println(now + " can not be finished!");
						else theTaxi.carryRequest(now);
						requestArrayList.remove(i--);
					}
					for (Request request : requestArrayList)
						for (Taxi taxi : taxis)
							if (request.canRegister(taxi)) {
								request.register(taxi);
								System.out.println("taxi No." + taxi.getId() + " register the " + request + " at " + MyTimer.getMyTimer());
							}
					for (Taxi taxi : taxis) taxi.run();
					cityMap.clearFlow();
					for (Taxi taxi : taxis) {
						if (!(taxi instanceof VipTaxi))
							cityMap.addFlow(taxi.getStayCityCross(), taxi.getCurDirection());
					}
				}
			}
		}
	}

	public boolean repOk() {
		return cityMap != null && lightsMap != null && taxis != null && requestArrayList != null;
	}
}
