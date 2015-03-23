Création d'une API Rest
Vanegue Elliot et Hakimi Salsabile
17/03/2015

*** Remarques /


*** 0/ README

	Pour exécuter le projet :

	lancer un serveur ftp en local.

	lancer la plateforme REST via eclipse

	aller sur l'url : http://localhost:8080/rest/api/connexion

	pour ajouter un fichier sur le serveur :
		- mettre le fichier dans le repertoire du projet
		- cliquer sur add à l'endroit où vous souhaitez déposer le fichier
		- entrez le nom du fichier à ajouter

	Commande delete
	test du delete : curl -X "DELETE" http://localhost:8080/rest/api/file/<nom fichier>/<login>
	il faut etre connecté au préalable
	

	Dans un autre terminal:
	


*** 1/ Introduction

Ce programme crée un serveur REST afin de communiquer avec un serveur ftp.

*** 2/ Architecture

	Packages:
			
			application :
					Starter
										
			config :
					AppConfig
					
			exceptions :
					AuthentificationException
					CommandException
					ConnectionException
					MFileNotFoundException
					
			ressource :
					ConnexionResource
					DirResource
					FileResource
					
			rs :
					FTPRestService
					JaxRsApiApplication
					
			services :
					FTPService
			
			
	Design patterns:
        Singleton
		
	Gestion d'erreurs :
	
		Catch : 
		
			catch (IOException e):
					- FTPRestService/public Response connexion(@Context final UriInfo uriInfo,@FormParam("name") final String name,@FormParam("mdp") final String mdp,@Context HttpServletResponse servletResponse)
					- FTPService / public FTPClient connect(final String login, final String password) (2 fois)
					- FTPService / public void stor(final String filename, final String login)
					- FTPService / public String retr(final String filename, final String login)
					- FTPService / public String list(final String dir, final String login)
			catch (final SocketException e1) :
					- FTPService / public FTPClient connect(final String login, final String password) (2 fois)
					-
	Throw : 
			
			 AuthentificationException():
					- FTPService / public FTPClient connect(final String login, final String password)
					
			ConnectionException() :
					- FTPService / public void stor(final String filename, final String login)
					- FTPService / public String retr(final String filename, final String login)
					
			MFileNotFoundException() :
					- FTPService / public void stor(final String filename, final String login)
					- FTPService / public String list(final String dir, final String login)
					
			CommandException("command") :
					- FTPService / public void stor(final String filename, final String login)
					- FTPService / public String retr(final String filename, final String login)
					
	throws : 
			Exception : 
					- Starter / public static void main( final String[] args )
					
*** 3/ Parcours du code (code samples)
	
	Exemple de code utilisant le framework fournit

	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("{login}/{file: .*}")
	public String getFile(@PathParam("file") final String file,
			@PathParam("login") final String login,
			@Context HttpServletResponse servletResponse) {

		final String result = ftpService.retr(file, login);

		return result;
	}



	Exemple d'utilisation du FTPClient

	public String retr(final String filename, final String login) {

		if (login == null || login == "") {
			throw new ConnectionException();
		}
		
		if (filename == null || filename == "") {
			throw new MFileNotFoundException();
		}
		
		FTPClient ftp = connect(login, "");

		String response = "";

		InputStream in = null;
		try {
			in = ftp.retrieveFileStream("/" + filename);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader buff = new BufferedReader(isr);

			String tmp;
			while ((tmp = buff.readLine()) != null) {
				response += tmp;
			}

		} catch (final IOException e2) {
			throw new CommandException(RETR);
		}
		
		return response;
	}
	
*** 4/ Compatibilité



*** 5/ Remarques
	
	