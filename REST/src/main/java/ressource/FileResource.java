package ressource;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPClient;

import services.FTPService;
import exceptions.AuthentificationException;
import exceptions.CommandException;
import exceptions.ConnectionException;
import exceptions.MFileNotFoundException;

/**
 * Classe représentant un fichier sous forme de ressource
 * 
 * @author elliot et salsabile
 *
 */
@Path("/file")
public class FileResource {

	@Inject
	private FTPService ftpService;
	private static final String RETR = "RETR";
	private static final String STOR = "STOR";

	/**
	 * Méthode qui effectue la commande RETR
	 * 
	 * @param file
	 *            fichier à télécharger
	 * @return le contenu du fichier
	 */
	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("/{file}")
	public String getFile(@PathParam("file") final String file) {

		String host = "ftp.univ-lille1.fr";
		String user = "anonymous";
		String password = "link";
		int port = 21;

		final FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host, port);
		} catch (final SocketException e1) {
			throw new ConnectionException();
		} catch (final IOException e1) {
			throw new ConnectionException();
		}

		try {
			if (!ftp.login(user, password)) {
				throw new AuthentificationException(ftp);
			}
		} catch (final IOException e1) {
			throw new AuthentificationException(ftp);
		}

		ftp.enterLocalPassiveMode();

		byte b[] = new byte[6400];

		InputStream in = null;
		try {
			in = ftp.retrieveFileStream(file);
			in.read(b);
		} catch (final IOException e2) {
			ftpService.disconnect(ftp);
			throw new CommandException(RETR);
		}

		final String response = new String(b);

		ftpService.disconnect(ftp);

		return new String(response);
	}

	/**
	 * Méthode qui supprime un fichier sur le serveur ftp
	 * 
	 * @param file
	 *            fichier à supprimer
	 * @return une réponse à la suppression du fichier
	 */
	@Path("/{file}")
	@DELETE
	public Response deletePerson(@PathParam("file") final String file) {
		String host = "ftp.univ-lille1.fr";
		String user = "anonymous";
		String password = "link";
		int port = 21;
		boolean response = false;

		final FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host, port);
		} catch (final SocketException e1) {
			throw new ConnectionException();
		} catch (final IOException e1) {
			throw new ConnectionException();
		}

		try {
			if (!ftp.login(user, password)) {
				throw new AuthentificationException(ftp);
			}
		} catch (final IOException e1) {
			throw new AuthentificationException(ftp);
		}

		ftp.enterLocalPassiveMode();

		try {
			response = ftp.deleteFile(file);
		} catch (IOException e) {
			throw new MFileNotFoundException();
		}

		if (response) {
			return Response.ok().build();
		}

		return Response.notModified().build();
	}

	/**
	 * Méthode qui effectue la commande STOR
	 * 
	 * @param file
	 *            fichier à envoyer
	 * @return réponse sur la réussite de l'envoie
	 */
	@PUT
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("/{file}")
	public String putFile(@PathParam("file") final String file) {

		String host = "ftp.univ-lille1.fr";
		String user = "anonymous";
		String password = "link";
		int port = 21;

		final FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host, port);
		} catch (final SocketException e1) {
			throw new ConnectionException();
		} catch (final IOException e1) {
			throw new ConnectionException();
		}

		try {
			if (!ftp.login(user, password)) {
				throw new AuthentificationException(ftp);
			}
		} catch (final IOException e1) {
			throw new AuthentificationException(ftp);
		}

		ftp.enterLocalPassiveMode();

		try {
			ftp.stor(file);
		} catch (final IOException e) {
			ftpService.disconnect(ftp);
			throw new CommandException(STOR);
		}

		return file;

	}

}
