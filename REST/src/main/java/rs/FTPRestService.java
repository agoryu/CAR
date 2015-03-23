package rs;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.net.ftp.FTPClient;

import services.FTPService;

@Path("/ftp")
public class FTPRestService {

	@Inject private FTPService ftpService;

	@Produces({ MediaType.APPLICATION_JSON })
	@POST
	public Response connexion(@Context final UriInfo uriInfo,
			@FormParam("name") final String name,
			@FormParam("mdp") final String mdp,
			@Context HttpServletResponse servletResponse) {

		FTPClient ftp = ftpService.connect(name, mdp);
		Response response = null;
		if(ftp.isConnected()) {
			response = Response.ok().build();
		} else {
			response = Response.status(Status.FORBIDDEN)
					.entity("Impossible to connect you")
					.build();
		}
		
		try {
			servletResponse.sendRedirect("http://localhost:8080/rest/api/dir/"+ name + ftp.printWorkingDirectory());
		} catch (final IOException e) {
			response = Response.status(Status.FORBIDDEN)
					.entity("Error redirect")
					.build();
		}
		
		return response;
	}

}
