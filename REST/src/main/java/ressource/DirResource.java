package ressource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import services.FTPService;

/**
 * Classe représentant un dossier sous forme de ressource
 * 
 * @author elliot et salsabile
 *
 */
@Path("/dir")
public class DirResource {

	@Inject
	private FTPService ftpService;

	/**
	 * Méthode affichant le contenu d'un dossier
	 * 
	 * @param dir
	 *            Dossier affiché
	 * @return Contenu du dossier
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	@Path("{login}/{dir: .*}")
	public String getDirContent(@PathParam("dir") final String dir, @PathParam("login") final String login) {

		return ftpService.list(dir, login);
	}
}
