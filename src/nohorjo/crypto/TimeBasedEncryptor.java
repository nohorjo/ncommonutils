package nohorjo.crypto;

/**
 * This class authenticates using the current time along with a secret key.
 * Requires encryption and decryption to take place within a minute of each
 * other.
 * 
 * @author muhammed
 *
 */
public class TimeBasedEncryptor {

	private AESEncryptor aes = new AESEncryptor();

	/**
	 * Encrypts the message with the current timestamp in minutes
	 * 
	 * @param key
	 *            the key to encrypt the message with
	 * @param message
	 *            the message to encrypt
	 * @return the encrypted message
	 * @throws EncryptionException
	 *             on errors
	 */
	public String encrypt(String key, String message) throws EncryptionException {
		return aes.encrypt(key + currentTimeMins(), message);
	}

	/**
	 * Decrypts the message with the current timestamp in minutes
	 * 
	 * @param key
	 *            the key to decrypt with
	 * @param encrypted
	 *            the encrypted message
	 * @return the original message
	 * @throws EncryptionException
	 *             on failed decryption
	 */
	public String decrypt(String key, String encrypted) throws EncryptionException {
		return aes.decrypt(key + currentTimeMins(), encrypted);
	}

	/**
	 * Used to override the default {@link AESEncryptor}
	 * 
	 * @param aes
	 *            to override with
	 */
	public void setAes(AESEncryptor aes) {
		this.aes = aes;
	}

	/**
	 * Gets the current timestamp in minutes
	 * 
	 * @return the current timestamp in minutes
	 */
	private long currentTimeMins() {
		return System.currentTimeMillis() / 60000;
	}
}
