package rs;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import services.FTPService;

@Path("/ftp")
public class FTPRestService {

	@Inject private FTPService ftpService;

	@Produces({ MediaType.APPLICATION_JSON })
	@POST
	public Response connexion(@Context final UriInfo uriInfo,
			@FormParam("name") final String name,
			@FormParam("mdp") final String mdp) {

		String response = "toto";
		
		return Response.created( uriInfo.getRequestUriBuilder().path( response ).build() ).build();
	}

}
