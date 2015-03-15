package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Erreur lors de l'execution d'une commande
 * 
 * @author elliot et salsabile
 *
 */
public class CommandException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1294814118811130997L;

	public CommandException(final String command) {
		super(Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(" Error in execution of command : " + command).build());
	}

}
