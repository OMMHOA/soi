package coders;

import java.io.StringReader;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import messages.RowColumnMessage;
import messages.LockIdMessage;
import messages.Message;

public class MessageDecoder implements Decoder.Text<Message>{

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
	}

	@Override
	public Message decode(String arg0) throws DecodeException {
		JsonParser parser = Json.createParser(new StringReader(arg0));
		parser.next(); //start_obj
		parser.next(); // key_name
		parser.next(); // value_string
		String type = parser.getString();
		Message msg;
		RowColumnMessage rcmessage;
		LockIdMessage limessage;
		switch (type) {
			case "initRoom":
				rcmessage = getRCMessage(parser);
				msg = rcmessage;
				break;
			case "lockSeat":
				rcmessage = getRCMessage(parser);
				msg = rcmessage;
				break;
			case "unlockSeat":
				limessage = getLIMessage(parser);
				msg = limessage;
				break;
			case "reserveSeat":
				limessage = getLIMessage(parser);
				msg = limessage;
				break;
			case "getRoomSize":
				msg = new Message();
				break;
			case "updateSeats":
				msg = new Message();
				break;
			default:
				msg = new Message();
		}
		msg.setType(type);
		return msg;
	}
	
	private RowColumnMessage getRCMessage(JsonParser parser) {
		RowColumnMessage message = new RowColumnMessage();
		parser.next(); // key_name
		parser.next(); // row
		message.setRows(parser.getInt());
		parser.next();
		parser.next();
		message.setColumns(parser.getInt());
		return message;
	}
	
	private LockIdMessage getLIMessage(JsonParser parser) {
		LockIdMessage message = new LockIdMessage();
		parser.next();
		parser.next();
		message.setLockId(parser.getString());
		return message;
	}
	
	@Override
	public boolean willDecode(String arg0) {
		return arg0 != null && arg0.startsWith("{");
	}

}
