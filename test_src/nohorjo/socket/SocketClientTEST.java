package nohorjo.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import nohorjo.delegation.Action;

public class SocketClientTEST {
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Started client");
		String address = "80.3.190.65";
		//address = "localhost";
		int port = 9003;
		port = 5533;
		SocketClient client = new SocketClient(address, port);
		Action onReceive = new Action() {
			String buffer = "";

			@Override
			public Object run(Object... args) {
				buffer += new String(new byte[] { (byte) args[0] });
				if (buffer.endsWith("\n")) {
					System.out.print("CLIENT RECEIVED: " + buffer);
					buffer = "";
				}
				return null;
			}
		};
		Action onDisconnect = new Action() {

			@Override
			public Object run(Object... args) {
				System.out.println("Disconnected from server " + ((Socket) args[0]).getRemoteSocketAddress());
				return null;
			}
		};
		client.setActions(onReceive, onDisconnect);
		client.connect();
		try (Scanner sc = new Scanner(System.in)) {
			while (sc.hasNextLine())
				client.send((sc.nextLine() + "\n").getBytes());
		}
		client.close();
	}

}
