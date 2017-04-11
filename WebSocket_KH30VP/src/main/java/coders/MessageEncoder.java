package coders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import messages.Message;

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
	}

	@Override
	public String encode(Message arg0) throws EncodeException {
		return arg0.encode();
	}

}
