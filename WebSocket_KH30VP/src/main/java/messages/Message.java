package messages;

public class Message {
	protected String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String encode() {
		return "{" + "\"type\":\"" + type + "\"}";
	}
}
