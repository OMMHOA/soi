package messages;

public class ErrorMessage extends Message{
	String message;
	
	public ErrorMessage(String m) {
		message = m;
		type = "error";
	}
	
	public String encode() {
		return "{" + "\"type\":\"" + type + "\"," + 
				"\"message\":\"" + message + "\"}";
	}
}
