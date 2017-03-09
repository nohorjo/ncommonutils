package nohorjo.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Utility class for common operations
 * 
 * @author muhammed
 *
 */
public class CommonUtils {
	/**
	 * Compares two collections
	 * 
	 * @param c1
	 *            a {@link Collection}
	 * @param c2
	 *            a {@link Collection}
	 * @return true if both Collection objects have the same elements
	 */
	public static <T> boolean collectionsEqual(Collection<T> c1, Collection<T> c2) {
		if (c1 == null || c2 == null) {
			return false;
		}
		for (T t : c2) {
			if (!c1.contains(t)) {
				return false;
			}
		}
		for (T t : c1) {
			if (!c2.contains(t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Recursively deletes a folder
	 * 
	 * @param file
	 *            file or folder to delete
	 * @throws IOException
	 *             on failed delete
	 */
	public static void deleteRecursively(File file) throws IOException {
		boolean successful = true;
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				deleteRecursively(sub);
			}
			successful = file.delete();
		} else if (file.isFile()) {
			successful = file.delete();
		}
		if (!successful) {
			throw new IOException("Could not delete:" + file.getName());
		}
	}

	/**
	 * Compares two maps
	 * 
	 * @param m1
	 *            a {@link Map}
	 * @param m2
	 *            a {@link Map}
	 * @return true if both Maps contain the same key:value pairs
	 */
	public static <K, V> boolean mapEquals(Map<K, V> m1, Map<K, V> m2) {
		if (m1 == null || m2 == null) {
			return false;
		}
		for (K key : m1.keySet()) {
			V v = m2.get(key);
			if (!m1.get(key).equals(v)) {
				return false;
			}
		}
		for (K key : m2.keySet()) {
			V v = m1.get(key);
			if (!m2.get(key).equals(v)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if two objects are null and of the same class<br>
	 * <strong> still need to check a==b </strong>
	 * 
	 * @param a
	 *            object to compare
	 * @param b
	 *            object to compare
	 * @return true if both are not null and of the same class, otherwise false
	 */
	public static boolean preEq(Object a, Object b) {
		return a != null && b != null && a.getClass().equals(b.getClass());
	}

	/**
	 * Concatenates {@link String} arrays
	 * 
	 * @param arrays
	 *            the arrays to concatenate
	 * @return a single array
	 */
	public static String[] arrayConcat(String[]... arrays) {
		int length = 0;
		for (String[] array : arrays) {
			length += array.length;
		}
		String[] rtn = new String[length];
		int i = 0;
		for (String[] array : arrays) {
			for (String element : array) {
				rtn[i++] = element;
			}
		}
		return rtn;
	}

	/**
	 * Converts a {@link Byte} array to a primitive version
	 * 
	 * @param array
	 *            the array to convert
	 * @return a primitive version of the {@link Byte} array
	 */
	public static byte[] toPrimitiveByteArray(Byte[] array) {
		byte[] rtn = new byte[array.length];
		for (int i = 0; i < rtn.length; i++) {
			rtn[i] = array[i];
		}
		return rtn;
	}

	/**
	 * Converts a {@link Serializable} object to a {@link Byte} array
	 * 
	 * @param t
	 *            object to convert
	 * @return a serialization of the object
	 */
	public static <T extends Serializable> byte[] serialize(T t) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(t);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	/**
	 * Converts a byte list into an object
	 * 
	 * @param data
	 *            the byte list to convert
	 * @return the object
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Serializable deserialize(List<Byte> data) throws ClassNotFoundException, IOException {
		byte[] bs = new byte[data.size()];
		for (int i = 0; i < bs.length; i++) {
			bs[i] = data.get(i);
		}
		return deserialize(bs);
	}

	/**
	 * Converts a byte array into an object
	 * 
	 * @param <T>
	 * 
	 * @param data
	 *            the byte array to convert
	 * @return the object
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Serializable deserialize(byte[] data) throws ClassNotFoundException, IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream ois;
		ois = new ObjectInputStream(bis);
		return (Serializable) ois.readObject();
	}

	/**
	 * Checks if any of the Objects supplied is null
	 * 
	 * @param o
	 *            objects to test
	 * @throws InvalidParameterException
	 *             if any object is null
	 * 
	 */
	public static void confirmNotNull(Object... o) throws InvalidParameterException {
		for (Object object : o) {
			if (object == null) {
				throw new InvalidParameterException("Invalid parameters");
			}
		}
	}
}
