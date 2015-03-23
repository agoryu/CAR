package ressource;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import services.FTPService;

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
	@Path("{login}/{file: .*}")
	public String getFile(@PathParam("file") final String file,
			@PathParam("login") final String login,
			@Context HttpServletResponse servletResponse) {

		final String result = ftpService.retr(file, login);

		return result;
	}

	// TODO
	@DELETE
	@Path("{login}/{folder: .*}")
	public Response deleteFileOrDirectory(
			@PathParam("folder") final String name,
			@PathParam("login") final String authorization) {
		
		Response response = null;
		if (ftpService.delete("/"+name, authorization)) {
			System.out.printf("Deletion of %s successfull\n", name);

			response = Response.ok().build();

		} else {
			response = Response.status(Status.FORBIDDEN)
					.entity("Impossible to delete the given directory/file")
					.build();
		}
		return response;
	}
}
