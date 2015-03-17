package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Impossible de lancer une commande avec un compte anonymous
 * 
 * @author elliot et salsabile
 *
 */
public class NoAutorisationException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7423578297619199555L;

	public NoAutorisationException() {
		super(
				Response.status(Status.UNAUTHORIZED)
						.entity(" sorry, this action is not possible for account anonymous ")
						.build());
	}

}
