package nohorjo.http;

public enum ContentType {
	FORM("application/x-www-form-urlencoded"), TEXT("text/plain");
	private final String type;

	private ContentType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.type;
	}
}
