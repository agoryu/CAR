package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Un fichier est introuvable
 * 
 * @author elliot et salsabile
 *
 */
public class MFileNotFoundException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7875081039777223037L;

	public MFileNotFoundException() {
		super(Response.status(Status.NOT_FOUND)
				.entity(" Error file not found ").build());
	}

}
