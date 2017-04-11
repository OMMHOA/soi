package exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MovieNotFoundException extends WebApplicationException {
	public MovieNotFoundException(String message) {
		super(Response.status(404).entity("Not found").type(MediaType.TEXT_PLAIN).build());
	}
}
