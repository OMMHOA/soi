package WebSocket_KH30VP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import coders.MessageDecoder;
import coders.MessageEncoder;
import entity.Seat;
import messages.ErrorMessage;
import messages.LockIdMessage;
import messages.Message;
import messages.RowColumnMessage;

@ServerEndpoint(
		value = "/cinema",
		decoders = {MessageDecoder.class},
		encoders = {MessageEncoder.class})
public class CinemaEndpoint {
	private static List<Session> sessions = new ArrayList<>();
	private static List<List<Seat>> seats = new ArrayList<>();
	private static Map<String, Seat> locks = new HashMap<>();
	
	@OnOpen
	public void open(Session session) {
		sessions.add(session);
		System.out.println("WebSocket opened: " + session.getId());
	}
	@OnClose
	public void close(Session session) { 
		for (Session s : sessions) {
			if (s.getId().equals(session.getId())) {
				session = s;
				break;
			}
		}
		sessions.remove(session);
		System.out.println("WebSocket closed: " + session.getId());
	}
	@OnError
	public void error(Throwable t) {
		System.out.println("WebSocket error: " + t.getMessage());
	}
	@OnMessage
	public Message message(Session session, Message message) {
		switch(message.getType()) {
			case "initRoom":
				return initRoom(message);
			case "getRoomSize":
				return getRoomSize();
			case "updateSeats":
				return updateSeats(session);
			case "lockSeat":
				return lockSeat(message);
			case "unlockSeat":
				return unlockSeat(message);
			case "reserveSeat":
				return reserveSeat(message);
			default:
				System.out.println("cant figure out message type");
				return null;
		}
	}
	private Message reserveSeat(Message message) {
		LockIdMessage msg = (LockIdMessage) message;
		Seat seat = locks.remove(msg.getLockId());
		if (seat == null) {
			return new ErrorMessage("Can not find lock");
		}
		seat.setStatus("reserved");
		sendSeatStatusToAllSessions(seat);
		return null;
	}
	private void sendSeatStatusToAllSessions(Seat seat) {
		for (Session session : sessions) {
			try {
				session.getBasicRemote().sendText(seat.getStatusMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private Message unlockSeat(Message message) {
		LockIdMessage msg = (LockIdMessage) message;
		Seat seat = locks.remove(msg.getLockId());
		if (seat == null) {
			return new ErrorMessage("Can not find lock");
		}
		seat.setStatus("free");
		sendSeatStatusToAllSessions(seat);
		return null;
	}
	private Message lockSeat(Message message) {
		RowColumnMessage msg = (RowColumnMessage) message;
		Seat seat = seats.get(msg.getRows() - 1).get(msg.getColumns() - 1);
		if (seat != null && !seat.getStatus().equals("free")) {
			return new ErrorMessage("Seat is not free");
		}
		String lockId = "lock" + msg.getRows() + "" + msg.getColumns();
		seat.setStatus("locked");
		locks.put(lockId, seat);
		LockIdMessage answer = new LockIdMessage();
		answer.setType("lockResult");
		answer.setLockId(lockId);
		sendSeatStatusToAllSessions(seat);
		return answer;
	}
	private Message updateSeats(Session session) {
		RemoteEndpoint.Basic endpoint = session.getBasicRemote();
		for (List<Seat> row : seats) {
			for (Seat seat : row) {
				try {
					endpoint.sendText(seat.getStatusMessage());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	private Message getRoomSize() {
		RowColumnMessage msg = new RowColumnMessage();
		msg.setType("roomSize");
		msg.setRows(seats.size());
		if (seats.size() != 0) {
			msg.setColumns(seats.get(0).size());
		} else {
			msg.setColumns(0);
		}
		return msg;
	}
	private Message initRoom(Message message) {
		seats = new ArrayList<>();
		RowColumnMessage msg = (RowColumnMessage) message;
		
		if (msg.getRows() < 1 || msg.getColumns() < 1) {
			return new ErrorMessage("Not positive row/column");
		}
		
		for (int i = 1; i <= msg.getRows(); i++) {
			List<Seat> row = new ArrayList<>();
			for (int j = 1; j <= msg.getColumns(); j++) {
				row.add(new Seat("free", i, j));
			}
			seats.add(row);
		}
		return null;
	}
}
