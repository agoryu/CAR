package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ConnectionException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2746418119275495282L;

	public ConnectionException() {
		super(
				Response
					.status( Status.NOT_ACCEPTABLE )
					.entity( " Error in socket connection " )
					.build()
			);
	}

}
