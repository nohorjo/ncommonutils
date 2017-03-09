package nohorjo.socket;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import nohorjo.delegation.Action;

/**
 * This class provides functionality to connect and transmit data through TCP
 * sockets
 * 
 * @author muhammed
 *
 */
public class SocketClient implements Closeable, Runnable {
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean alive;
	private Action onReceive;
	private Action onDisconnect;

	/**
	 * Creates a client that connects to a socket
	 * 
	 * @param address
	 *            the IP address of the target
	 * @param port
	 *            the port to connect to
	 * @throws UnknownHostException
	 *             if the address cannot be resolved
	 * @throws IOException
	 *             on socket errors
	 */
	public SocketClient(String address, int port) throws UnknownHostException, IOException {
		this(new Socket(address, port));
	}

	/**
	 * Creates a client based on a socket connection
	 * 
	 * @param socket
	 *            the connection
	 */
	public SocketClient(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Sets the custom {@link Action}s. Must be called before {@link #connect()}
	 * 
	 * @param onReceive
	 *            takes arguments ({@link Byte} = data received, {@link String}
	 *            = socket address), defines the action on receiving data from
	 *            the socket
	 * @param onDisconnect
	 *            takes arguments ({@link Socket} = the socket that it
	 *            disconnected from), defines the action on disconnecting from
	 *            the socket
	 */
	public void setActions(Action onReceive, Action onDisconnect) {
		this.onReceive = onReceive;
		this.onDisconnect = onDisconnect;
	}

	/**
	 * Initialized the IO and listeners for the socket. Must call
	 * {@link #setActions(Action, Action)} before this
	 * 
	 * @throws IOException
	 *             if an I/O error occurs when creating the input or output
	 *             stream, the socket is closed, the socket is not connected, or
	 *             the socket input has been shutdown using shutdownInput()
	 */
	public void connect() throws IOException {
		String nulls = "Must define through setActions(): ";
		if (onDisconnect == null) {
			nulls += "onDisconnect, ";
		}
		if (onReceive == null) {
			nulls += "onReceive, ";
		}
		if (nulls.length() != 34) {
			throw new NullPointerException(nulls.replaceAll(", $", ""));
		}

		alive = true;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		new Thread(this).start();
	}

	/**
	 * Send the byte array down the output stream
	 * 
	 * @param data
	 *            the byte array to send
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void send(byte... data) throws IOException {
		dos.write(data);
		dos.flush();
	}

	/**
	 * While the socket is alive, will listen for new data
	 */
	@Override
	public void run() {
		while (alive) {
			try {
				while (!socket.isClosed() && socket.isBound() && socket.isConnected()) {
					onReceive.run(dis.readByte(), socket.getRemoteSocketAddress().toString());
				}
			} catch (SocketException | EOFException e) {
				try {
					close();
				} catch (Exception e2) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the socket, input and output streams, and runs
	 * {@link #onDisconnect}
	 */
	@Override
	public void close() throws IOException {
		if (alive) {
			alive = false;
			onDisconnect.run(socket);
			dis.close();
			dos.close();
			socket.close();
		}
	}

	/**
	 * Check if the sokect is alive
	 * 
	 * @return true if still alive
	 */
	public boolean isAlive() {
		return alive;
	}
}
