package ressource;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPClient;

import services.FTPService;
import exceptions.MFileNotFoundException;

/**
 * Classe représentant un fichier sous forme de ressource
 * 
 * @author elliot et salsabile
 *
 */
@Path("/file")
public class FileResource {

	private static final String LOCAL_ADRESS = "http://localhost:8080/rest/api/dir/here";
	@Inject
	private FTPService ftpService;

	/**
	 * Méthode qui effectue la commande STOR
	 * 
	 * @param file
	 *            fichier à envoyer
	 * @return réponse sur la réussite de l'envoie
	 */
	@Produces({ MediaType.APPLICATION_JSON })
	@POST
	public String putFile(@FormParam("file") final String file,
			@Context HttpServletResponse servletResponse) {

		ftpService.stor(file, "");

		return file;

	}

	/**
	 * Méthode qui effectue la commande RETR
	 * 
	 * @param file
	 *            fichier à télécharger
	 * @return le contenu du fichier
	 */
	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("{login}/{file}")
	public String getFile(@PathParam("file") final String file,
			@PathParam("login") final String login,
			@Context HttpServletResponse servletResponse) {

		System.out.println("debut");
		final String result = ftpService.retr(file, login);

		System.out.println("fin");
		return result;
	}

	/**
	 * Méthode qui supprime un fichier sur le serveur ftp
	 * 
	 * @param file
	 *            fichier à supprimer
	 * @return une réponse à la suppression du fichier
	 */
	@Path("{file}/{login}")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteFile(@PathParam("file") final String file,
			@PathParam("login") final String login) {

		FTPClient ftp = ftpService.connect(login, "");
		boolean response = false;

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
}
