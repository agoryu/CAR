package ressource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/dir")
public class DirRessource {

	private String name;
	private List<String> content;
	
	public DirRessource() {
		this.name = "";
		this.content = new ArrayList<String>();
	}
	
	public void newList() {
		this.content = new ArrayList<String>();
	}
	
	public void addFile(final String file) {
		this.content.add(file);
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	@GET
	@Produces("text/html")
	public String getContent() {
		final StringBuffer result = new StringBuffer();
		result.append("<h1>FILE : " + name + "</h1>");
		
		for(final String tmp : this.content) {
			result.append("<p>" + tmp + "</p></br>");
		}
		
		return new String(result);
	}
}
