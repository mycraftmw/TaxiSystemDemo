import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Vector;

/**
 * Overview:
 * The {@code Main} class implements the taxi management system initialization process.
 */
public class Main {
	/**
	 * Requires:无。<br>
	 * Modifies:新建初始化所有需要的实例包括一个计时器，一个城市路口地图，一个红绿灯信息图，一个请求队列，一个出租车列表，一个用户操作安全类，启动相应线程。<br>
	 * Effects:实现本次作业的模拟要求，无返回值。<br>
	 *
	 * @param args 传入的命令行参数。
	 */
	public static void main(String[] args) {
		try {
			// read map.txt
			int[][] ways = myReadFile("map.txt", 80);
			for (int[] line : ways) for (int each : line) if (each < 0 || each > 3) System.exit(0);
			// read road.txt
			int[][] lights = myReadFile("light.txt", 80);
			for (int[] line : lights) for (int each : line) if (each != 0 && each != 1) System.exit(0);
			// read end
			// init start
			MyTimer.getMyTimer();
			CityMap cityMap = new CityMap(ways);
			LightsMap lightsMap = new LightsMap(lights);
			Vector<Request> requestArrayList = new Vector<>();
			Vector<Taxi> taxis = new Vector<>();
			UserOperator userOperator = new UserOperator(taxis, requestArrayList, cityMap);
			for (int i = 0; i < 30; i++) {
				Random random = new Random();
				taxis.add(new VipTaxi(new CityCross(random.nextInt(80), random.nextInt(80)), cityMap, lightsMap));
			}
			for (int i = 30; i < 100; i++) {
				Random random = new Random();
				taxis.add(new Taxi(new CityCross(random.nextInt(80), random.nextInt(80)), cityMap, lightsMap));
			}
			// init end
			// start run
			new Thread(new TaxiSchedulingSystem(cityMap, lightsMap, taxis, requestArrayList)).start();
			new Thread(new Simulation(userOperator)).start();
			new Thread(lightsMap).start();
			System.in.read();
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.out.println("No Map!");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("error!");
			System.exit(0);
		} catch (Throwable throwable) {
			System.exit(0);
		}
	}

	/**
	 * Requires:传入一个文件存在的文件名，一个正数的地图大小。<br>
	 * Modifies:无。<br>
	 * Effects:返回相应的文件内容。<br>
	 *
	 * @param fileName 要读入信息的文件名。
	 * @param mapCap   地图大小。
	 *
	 * @return 文件内容，用数组表示。
	 *
	 * @throws Throwable 异常。
	 */
	private static int[][] myReadFile(String fileName, int mapCap) throws Throwable {
		if (mapCap <= 0) throw new Exception();
		int tmp[][] = new int[mapCap][mapCap];
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		for (int i = 0; i < mapCap; i++) {
			String line = bufferedReader.readLine().trim();
			if (line.length() != mapCap) System.exit(0);
			for (int j = 0; j < line.length(); j++) tmp[i][j] = line.charAt(j) - '0';
		}
		bufferedReader.close();
		return tmp;
	}
}
