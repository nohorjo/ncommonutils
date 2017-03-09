package nohorjo.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import nohorjo.delegation.Action;

public class SocketServerTEST {
	public static void main(String[] args) throws IOException {
		System.out.println("Started server");
		final SocketServer server = new SocketServer(9003);
		Action onReceive = new Action() {
			String buffer = "";

			@Override
			public Object run(Object... args) {
				buffer += new String(new byte[] { (byte) args[0] });
				if (buffer.endsWith("\n")) {
					System.out.print(args[1] + " SAYS: " + buffer);
//					String a = new Random().nextInt()+"";
//					System.out.println(a);
//					try {
//						server.send((String)args[1], (a+"\n").getBytes());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
					buffer = "";
				}
				return null;
			}
		};
		Action onNewConnection = new Action() {

			@Override
			public Object run(Object... args) {
				System.out.println("Connected to " + ((Socket) args[0]).getRemoteSocketAddress());
				return null;
			}
		};
		Action onDisconnect = new Action() {

			@Override
			public Object run(Object... args) {
				System.out.println("Disconnected from " + ((Socket) args[0]).getRemoteSocketAddress());
				return null;

			}
		};

		server.setActions(onReceive, onNewConnection, onDisconnect);
		server.start();

		try (Scanner sc = new Scanner(System.in)) {
			while (sc.hasNextLine()) {
				server.sendAll((sc.nextLine() + "\n").getBytes());
			}
		}
		server.close();
	}

}
