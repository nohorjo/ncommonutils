package nohorjo.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for timing operations
 * 
 * @author muhammed
 *
 */
public class Stopwatch {
	private static Map<Long, Long> startTimes = new HashMap<>();

	/**
	 * Starts the stopwatch for the current thread
	 */
	public static void start() {
		startTimes.put(Thread.currentThread().getId(), System.currentTimeMillis());
	}

	/**
	 * Gets the time
	 * 
	 * @return the time in milliseconds
	 */
	public static long time() {
		return System.currentTimeMillis() - startTimes.remove(Thread.currentThread().getId());
	}

	/**
	 * Gets the time and restarts the stopwatch
	 * 
	 * @return the time in milliseconds
	 */
	public static long lap() {
		long time = time();
		start();
		return time;
	}
}
