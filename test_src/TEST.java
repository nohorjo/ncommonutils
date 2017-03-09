import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nohorjo.http.HttpOperation;

public class TEST {
	private static final HttpOperation httpOperation = new HttpOperation();

	public static void main(String[] args) throws IOException {
		System.out.println(distance(new double[] {50.812827, -0.409000}, new double[] {50.813125, -0.404107}));
	}

	static double distance(double[] newLatLong, double[] oldLatLong) {
		double lat1 = oldLatLong[0];
		double lat2 = newLatLong[0];
		double a = Math.pow(Math.sin(Math.toRadians(lat2 - lat1) / 2), 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
						* Math.pow(Math.sin(Math.toRadians(newLatLong[1] - oldLatLong[1]) / 2), 2);
		return 6371e3 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}
}
