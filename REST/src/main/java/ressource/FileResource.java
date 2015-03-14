package ressource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPClient;

import services.FTPService;
import exceptions.CommandException;
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
	 * Méthode qui effectue la commande STOR
	 * 
	 * @param file
	 *            fichier à envoyer
	 * @return réponse sur la réussite de l'envoie
	 */
	@POST
	@Produces({ MediaType.TEXT_HTML })
	public String putFile(@PathParam("file") final String file) {

		//TODO anonymous ne peut pas faire de stor
		FTPClient ftp = ftpService.connect("agoryu", "");

		try {
			InputStream ips=new FileInputStream(file);
			ftp.storeFile(file, ips);
		} catch (final IOException e) {
			throw new CommandException(STOR);
		}

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
	@Path("/{file}")
	public String getFile(@PathParam("file") final String file) {

		FTPClient ftp = ftpService.connect("agoryu", "");

		String response = "";

		InputStream in = null;
		try {
			in = ftp.retrieveFileStream(file);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader buff = new BufferedReader(isr);
			
			String tmp;
			while((tmp = buff.readLine()) != null) {
				response += tmp;
			}
			
		} catch (final IOException e2) {
			throw new CommandException(RETR);
		}

		return response;
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
		

		FTPClient ftp = ftpService.connect("agoryu", "");
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
