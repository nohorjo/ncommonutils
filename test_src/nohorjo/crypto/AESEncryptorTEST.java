package nohorjo.crypto;

public class AESEncryptorTEST {
	public static void main(String[] args) throws Exception {
		AESEncryptor aes = new AESEncryptor();
		System.out.println(aes.decrypt("7449271314",
				"£CG1/ADUy14sNa8QrTdkXZIaQQEMUYr7WRpVBDUA0Mv8TwE74JkuiLDutyCQ=£".replace("£", "")));
	}
}
