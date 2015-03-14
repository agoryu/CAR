package ressource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/connexion")
public class ConnexionResource {

	public ConnexionResource() {
		// TODO Auto-generated constructor stub
	}

	@Produces("text/html")
	@GET
	public String getContent() {
		return "<form method=\"post\" action=\"ftp\">"
				
				+ "<p> <label for=\"pseudo\">Votre pseudo :</label>"
				+ "<input type=\"text\" name=\"name\" id=\"name\" placeholder=\"pseudo\" size=\"30\""
				+ " maxlength=\"10\" /></p>"
				
				+ "<p> <label for=\"pseudo\">Votre mot de passe :</label>"
				+ "<input type=\"password\" name=\"mdp\" id=\"mdp\" size=\"30\""
				+ " maxlength=\"10\" /></p><input type=\"submit\" value=\"Envoyer\" /></form>";
	}

}
