package ressource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import services.FTPService;
import exceptions.MFileNotFoundException;

@Path("/dir")
public class DirResource {
	
	private static final String HERE = "here";
	private static final String STOR_FORM = "</br><a href=\"http://localhost:8080/rest/api/formStor\"> Add </a>";
	private static final String FILE_PATH_LINK = "<a href=\"http://localhost:8080/rest/api/file/";
	private static final String DIR_PATH_LINK = "<a href=\"http://localhost:8080/rest/api/dir/";
	@Inject private FTPService ftpService;
	
	@GET
	@Produces({ MediaType.TEXT_HTML })
	@Path( "/{dir}" )
	public String getDirContent(@PathParam( "dir" ) final String dir) {
		
		FTPClient ftp = ftpService.connect("agoryu", "");
		
		FTPFile[] result = null;
		
		try {
			if(!HERE.equals(dir)) {
				final String directory = ftp.printWorkingDirectory() + "/" + dir;
				ftp.changeWorkingDirectory(directory);
			}
			
			result = ftp.listFiles();
		} catch (final IOException e) {
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
		
		response.append(STOR_FORM);
		/*var client = new XMLHttpRequest();

client.open("PUT", url, false);*/
		
		return new String(response);
	}
}
