package entity;

public class Seat {
	private String status;
	private int row;
	private int column;
	
	public Seat(String status, int row, int column) {
		this.status = status;
		this.row = row;
		this.column = column;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getStatusMessage() {
		return "{\"type\":\"seatStatus\",\"row\":" + row + "," +
				"\"column\":" + column + "," +
				"\"status\":\"" + status + "\"}";
	}
}
