package ressource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/connexion")
public class ConnexionResource {

	@Produces("text/html")
	@GET
	public String authentification() {
		return "<form method=\"post\" action=\"ftp\">"
				
				+ "<p> <label for=\"pseudo\">Votre pseudo :</label>"
				+ "<input type=\"text\" name=\"name\" id=\"name\" placeholder=\"pseudo\" size=\"30\""
				+ " maxlength=\"20\" /></p>"
				
				+ "<p> <label for=\"pseudo\">Votre mot de passe :</label>"
				+ "<input type=\"password\" name=\"mdp\" id=\"mdp\" placeholder=\"password\" size=\"30\""
				+ " maxlength=\"20\" /></p><input type=\"submit\" value=\"Connection\" /></form>";
	}

}
