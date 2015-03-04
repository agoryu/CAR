package ressource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/file")
public class FileRessource {

	private String name;
	private String content;
	
	public FileRessource() {
		name = "";
		content = "";
	}

	@GET
	@Produces("text/html")
	public String getContent() {
		return "<h1>FILE : " + name + "</h1>" + "<p>" + content + "</p>";
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public void setContent(final String content) {
		this.content = content;
	}
}
