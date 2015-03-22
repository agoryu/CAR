package ressource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/formStor")
public class StorFormRessource {

	@Produces("text/html")
	@GET
	public String storFile() {
		return "<form method=\"post\" action=\"file\"\">"
				+ "<p> <label for=\"file\">fichier :</label>"
				+ "<input type=\"text\" name=\"file\" id=\"file\" placeholder=\"your file\" size=\"30\""
				+ " maxlength=\"30\" /></p>"
				+ "<input type=\"submit\" value=\"Ajouter\" /></form>";
	}

}
