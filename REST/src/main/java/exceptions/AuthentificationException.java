package exceptions;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.net.ftp.FTPClient;

/**
 * Login ou mot de passe incorrecte
 * 
 * @author elliot et salsabile
 *
 */
public class AuthentificationException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3254354767935768159L;

	public AuthentificationException(final FTPClient ftp) {
		super(Response.status(Status.NOT_FOUND)
				.entity(" Error in login or password ").build());
		try {
			ftp.logout();
		} catch (IOException e) {
			System.err.println("Erreur durant la fermeture de la connection");
		}
	}

}
