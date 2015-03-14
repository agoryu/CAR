package ressource;

import java.io.IOException;
import java.net.SocketException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import services.FTPService;
import exceptions.AuthentificationException;
import exceptions.ConnectionException;
import exceptions.MFileNotFoundException;

@Path("/dir")
public class DirResource {
	
	private static final String FILE_PATH_LINK = "<a href=\"http://localhost:8080/rest/api/file/";
	private static final String DIR_PATH_LINK = "<a href=\"http://localhost:8080/rest/api/dir/";
	@Inject private FTPService ftpService;
	
	@GET
	@Produces({ MediaType.TEXT_HTML })
	@Path( "/{dir}" )
	public String getDirContent(@PathParam( "dir" ) final String dir) {
		
		String host = "ftp.univ-lille1.fr";
		String user = "anonymous";
		String password = "link";
		int port = 21;
		
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host, port);
		} catch (final SocketException e1) {
			throw new ConnectionException();
		} catch (final IOException e1) {
			throw new ConnectionException();
		}
		
		try {
			if(!ftp.login(user, password)) {
				throw new AuthentificationException(ftp);
			}
		} catch (final IOException e1) {
			throw new AuthentificationException(ftp);
		}
		
		ftp.enterLocalPassiveMode();
		
		FTPFile[] result = null;
		
		try {
			ftp.cwd(dir);
			result = ftp.listFiles();
		} catch (final IOException e) {
			ftpService.disconnect(ftp);
			throw new MFileNotFoundException();
		}
		
		StringBuilder response = new StringBuilder();
		
		if(result.length != 0) {
			for(final FTPFile file : result) {
				final String name = file.getName();
				//TODO gestion .. et .
				//TODO revoir la mise en page
				if(file.isDirectory()) {
					response.append(DIR_PATH_LINK+name+"\">D "+name+"</a></br>");
				} else {
					response.append(FILE_PATH_LINK+name+"\">F "+name+"</a></br>");
				}
			}
		}
		
		ftpService.disconnect(ftp);
		
		return new String(response);
	}
}
