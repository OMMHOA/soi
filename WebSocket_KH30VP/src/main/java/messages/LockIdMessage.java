package messages;

public class LockIdMessage extends Message{
	private String lockId;

	public String getLockId() {
		return lockId;
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}
	
	public String encode() {
		return "{" + "\"type\":\"" + type + "\"," +
				"\"lockId\":\"" + lockId + "\"}";
	}
}
