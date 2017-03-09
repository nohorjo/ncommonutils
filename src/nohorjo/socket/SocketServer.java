package nohorjo.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nohorjo.delegation.Action;

/**
 * Class that creates a TCP listener
 * 
 * @author muhammed
 *
 */
public class SocketServer implements Closeable, Runnable {
	private int port;
	private ServerSocket server;
	private Map<String, SocketClient> connections = new HashMap<>();
	private boolean alive;
	private Action onReceive;
	private Action onNewConnection;
	private Action onDisconnect;

	/**
	 * Constructs a listener on the port
	 * 
	 * @param port
	 *            the port to listen to
	 */
	public SocketServer(int port) {
		this.port = port;
	}

	/**
	 * Sets the custom {@link Action}s. Must be called before {@link #start()}
	 * 
	 * @param onReceive
	 *            takes arguments ({@link Byte} = data received, {@link String}
	 *            = socket address), defines the action on receiving data from
	 *            the socket
	 * @param onNewConnection
	 *            takes arguments ({@link Socket} = the new socket it connected
	 *            to), defines the action to run when a new connection is made
	 * @param onDisconnect
	 *            takes arguments ({@link Socket} = the socket that it
	 *            disconnected from), defines the action on disconnecting from
	 *            the socket
	 */
	public void setActions(Action onReceive, Action onNewConnection, Action onDisconnect) {
		this.onReceive = onReceive;
		this.onNewConnection = onNewConnection;
		this.onDisconnect = onDisconnect;
	}

	/**
	 * Initializes the server to start listening on the port. Must call
	 * {@link #setActions(Action, Action, Action)} before this
	 * 
	 * @throws IOException
	 *             if an I/O error occurs when opening the socket
	 */
	public void start() throws IOException {
		String nulls = "";
		if (onDisconnect == null) {
			nulls += "onDisconnect, ";
		}
		if (onReceive == null) {
			nulls += "onReceive, ";
		}
		if (onNewConnection == null) {
			nulls += "onNewConnection, ";
		}
		if (nulls.length() != 0) {
			throw new NullPointerException(nulls.replaceAll(", $", ""));
		}
		alive = true;
		server = new ServerSocket(port);
		new Thread(this).start();
	}

	/**
	 * Closes all connections and reinitializes the server
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void reset() throws IOException {
		close();
		start();
	}

	/**
	 * While alive listens for new connections and handles them. Runs
	 * {@link #onNewConnection} for each new connection
	 */
	@Override
	public void run() {
		while (alive) {
			try {
				Socket socket = server.accept();
				SocketClient connection = new SocketClient(socket);
				connection.setActions(onReceive, onDisconnect);
				connection.connect();
				connections.put(socket.getRemoteSocketAddress().toString(), connection);
				onNewConnection.run(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends data to a target connection
	 * 
	 * @param recipient
	 *            the socket address of the target
	 * @param data
	 *            the data to send
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void send(String recipient, byte... data) throws IOException {
		SocketClient connection = connections.get(recipient);
		if (connection == null) {
			throw new IOException("Connection does not exist: " + recipient);
		}
		try {
			if (connection.isAlive()) {
				connection.send(data);
			} else {
				connection.close();
				throw new SocketException("Connection is no longer alive");
			}
		} catch (IOException e) {
			connections.remove(recipient);
			throw e;
		}
	}

	/**
	 * Sends data to all alive connections
	 * 
	 * @param data
	 *            data to send
	 */
	public void sendAll(byte... data) {
		while (true) {
			try {
				for (String remoteAddress : connections.keySet()) {
					SocketClient connection = connections.get(remoteAddress);
					try {
						if (connection.isAlive()) {
							connection.send(data);
						} else {
							connection.close();
							throw new SocketException("Connection is no longer alive");
						}
					} catch (IOException e) {
						connections.remove(remoteAddress);
					}
				}
				break;
			} catch (ConcurrentModificationException e) {
				// retry
			}
		}
	}

	/**
	 * Closes all connections and the server
	 */
	@Override
	public void close() throws IOException {
		alive = false;
		for (SocketClient socketClient : connections.values()) {
			socketClient.close();
		}
		server.close();
	}

	/**
	 * Gets a set of the connection addresses
	 * 
	 * @return {@link Set} of socket address {@link String}s
	 */
	public Set<String> getConnections() {
		return connections.keySet();
	}

	/**
	 * Disconnects a connection
	 * 
	 * @param target
	 *            the socket address
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void disconnect(String target) throws IOException {
		SocketClient connection = connections.remove(target);
		if (connection == null) {
			throw new IOException("Connection does not exist: " + target);
		}
		connection.close();
	}
}
