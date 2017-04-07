package nohorjo.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Handles AES encryption
 * 
 * @author muhammed
 *
 */
public class AESEncryptor {

	/**
	 * Encodes into a Base64 String
	 * 
	 * @param input
	 *            to encode
	 * @return Base64 String
	 */
	protected String encodeB64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

	/**
	 * Decodes a Base64 byte array
	 * 
	 * @param input
	 *            Base64 encoded data
	 * @return decoded data
	 */
	protected byte[] decodeB64(byte[] input) {
		return DatatypeConverter.parseBase64Binary(new String(input));
	}

	/**
	 * Encrypts a message
	 * 
	 * @param key
	 *            the key to encrypt with
	 * @param initVector
	 *            the initialisation vector
	 * @param message
	 *            the message to encrypt
	 * @return the encrypted message
	 * @throws EncryptionException
	 *             on errors
	 */
	public String encrypt(String key, String initVector, String message) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(hashKey(key).getBytes("UTF-8"), "AES"),
					new IvParameterSpec(initVector.getBytes("UTF-8")));
			return encodeB64(cipher.doFinal(message.getBytes()));
		} catch (Exception e) {
			throw new EncryptionException(e);
		}
	}

	/**
	 * Encrypts a message, generating a random initialisation vector
	 * 
	 * @param key
	 *            the key to encrypt with
	 * @param message
	 *            the message to encrypt
	 * @return the initialisation vector + the encrpted message
	 * @throws EncryptionException
	 *             on errors
	 */
	public String encrypt(String key, String message) throws EncryptionException {
		String iv = genIV();
		return iv + encrypt(key, iv, message);
	}

	/**
	 * Decrypts a message
	 * 
	 * @param key
	 *            the key to decrypt with
	 * @param initVector
	 *            the initialisation vector
	 * @param encrypted
	 *            the encrypted message
	 * @return the original message
	 * @throws EncryptionException
	 *             on failed decryption
	 */
	public String decrypt(String key, String initVector, String encrypted) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashKey(key).getBytes("UTF-8"), "AES"),
					new IvParameterSpec(initVector.getBytes("UTF-8")));
			return new String(cipher.doFinal(decodeB64(encrypted.getBytes())));
		} catch (Exception e) {
			throw new EncryptionException(e);
		}
	}

	/**
	 * Decrypts a message
	 * 
	 * @param key
	 *            the key to decrypt with
	 * @param ivencrypted
	 *            where the first 16 characters is the initialisation vector and
	 *            the rest is the encrypted message
	 * @return the original message
	 * @throws EncryptionException
	 *             on failed decryption
	 */
	public String decrypt(String key, String ivencrypted) throws EncryptionException {
		return decrypt(key, ivencrypted.substring(0, 16), ivencrypted.substring(16));
	}

	/**
	 * Generates a random initialisation vector
	 * 
	 * @return a random initialisation vector
	 */
	public String genIV() {
		String iv = "";
		while (iv.length() < 16) {
			iv += "QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789+/"
					.charAt(new SecureRandom().nextInt(64));
		}
		return iv;
	}

	/**
	 * Hashes the key using SHA1 and gets the first 16 characters
	 * 
	 * @param key
	 *            the key to hash
	 * @return a key of length 16
	 */
	private String hashKey(String key) {
		try {
			key = encodeB64(MessageDigest.getInstance("SHA1").digest(key.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return key.substring(0, 16);
	}
}