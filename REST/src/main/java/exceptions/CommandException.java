package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CommandException  extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1294814118811130997L;

	public CommandException(final String command) {
		super(
				Response
					.status( Status.NOT_FOUND )
					.entity( " Error in execution of command : " + command )
					.build()
			);
	}

}
