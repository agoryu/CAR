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

import org.apache.commons.net.ftp.FTPClient;

import services.FTPService;

@Path("/ftp")
public class FTPRestService {

	private static final String CURRENT_DIRECTORY = "http://localhost:8080/rest/api/dir/here";
	@Inject private FTPService ftpService;

	@Produces({ MediaType.APPLICATION_JSON })
	@POST
	public Response connexion(@Context final UriInfo uriInfo,
			@FormParam("name") final String name,
			@FormParam("mdp") final String mdp,
			@Context HttpServletResponse servletResponse) {

		FTPClient ftp = ftpService.connect(name, mdp);
		String response;
		if(ftp.isConnected()) {
			response = "ok";
		} else {
			response = "ko";
		}
		
		try {
			servletResponse.sendRedirect(CURRENT_DIRECTORY);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.created( uriInfo.getRequestUriBuilder().path( response ).build() ).build();
	}

}
