package nohorjo.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import nohorjo.common.CountInputStream;

/**
 * Handles simple HTTP operations
 * 
 * @author muhammed
 *
 */
public class HttpOperation {
	private long bytesRead;

	/**
	 * Does a GET with a cookie
	 * 
	 * @param url
	 *            url to get
	 * @param cookie
	 *            cookie to attach
	 * @return the response
	 * @throws IOException
	 *             on errors
	 */
	public String doGet(String url, String cookie) throws IOException {
		HttpURLConnection.setFollowRedirects(true);
		StringBuilder result = new StringBuilder();
		URL Url;
		try {
			Url = new URL(getFinalURL(url));
		} catch (IOException e) {
			Url = new URL(url);
		}
		HttpURLConnection conn = (HttpURLConnection) Url.openConnection();

		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		conn.setRequestMethod("GET");

		if (cookie != null) {
			conn.setRequestProperty("Cookie", cookie);
		}

		CountInputStream is = new CountInputStream(conn.getInputStream());

		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is));) {
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		}
		bytesRead = is.getBytesRead();
		return result.toString();
	}

	/**
	 * Does a GET
	 * 
	 * @param url
	 *            the url to get
	 * @return the response
	 * @throws IOException
	 *             on errors
	 */
	public String doGet(String url) throws IOException {
		return doGet(url, null);
	}

	/**
	 * Does a post
	 * 
	 * @param url
	 *            url to post to
	 * @param data
	 *            data to post where each is in the form of key=value
	 * @return the response
	 * @throws IOException
	 *             on errors
	 */
	public String doPost(String url, String... data) throws IOException {
		String dataString = "";

		for (String d : data) {
			dataString += "&" + d;
		}
		return doPost(url, dataString.replaceFirst(Pattern.quote("&"), ""));
	}

	/**
	 * Does a POST
	 * 
	 * @param url
	 *            url to post to
	 * @param data
	 *            literal {@link String} to post
	 * @param contentType
	 *            the content type
	 * @return the response
	 * @throws IOException
	 *             on errors
	 */
	public String doPost(String url, String data, ContentType contentType) throws IOException {

		byte[] postData = data.getBytes(StandardCharsets.UTF_8);
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		StringBuilder response = new StringBuilder();

		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", contentType.toString());
		conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		conn.setUseCaches(false);

		try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
			wr.write(postData);
		}
		CountInputStream is = new CountInputStream(conn.getInputStream());
		try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
		}
		bytesRead = is.getBytesRead();
		return response.toString();

	}

	/**
	 * Does a POST
	 * 
	 * @param url
	 *            url to post to
	 * @param data
	 *            literal {@link String} to post
	 * @return the response
	 * @throws IOException
	 *             on errors
	 */
	public String doPost(String url, String data) throws IOException {
		return doPost(url, data, ContentType.FORM);
	}

	/**
	 * Gets the number of bytes read from the last operation
	 * 
	 * @return number of bytes read
	 */
	public long getBytesRead() {
		return bytesRead;
	}

	/**
	 * Follows any redirections to get the final url
	 * 
	 * @param url
	 *            url to test
	 * @return the final url if redirected
	 * @throws IOException
	 *             on errors
	 */
	private String getFinalURL(String url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setInstanceFollowRedirects(false);
		con.connect();
		con.getInputStream();

		if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
				|| con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
			String redirectUrl = con.getHeaderField("Location");
			return getFinalURL(redirectUrl);
		}
		return url;
	}

}
