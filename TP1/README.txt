Implementation d'un serveur FTP en Java
Vanegue Elliot et Hakimi Salsabile
17/02/2015

S'il vous plait ne touchez pas à dossier_test/ ni au fichier testClient.txt pour le bon
fonctionnement des tests unitaire.

*** 0/ README

	Pour exécuter le projet :
	java -jar dossier_test/ Serveur.jar

	Dans un autre terminal:
	ftp -4 localhost 1024


*** 1/ Introduction

Ce programme crée un serveur FTP utilisant le port 1024 (pour le serveur) et 1025 (pour le transfert en mode passif ) afin de communiquer avec des clients,
le serveur gère les requêtes USER, PASS, QUIT, LIST , RETR, STOR, PASV, PWD, CWD, PORT et CDUP.

*** 2/ Architecture

	Packages:
			
			command :
					FtpCdup
					FtpCommand
					FtpCwd
					FtpList
					FtpPass
					FtpPasv
					FtpPort
					FtpPwd
					FtpQuit
					FtpRetr
					FtpStor
					FtpUser
					
			file :
					FileManagement
					
			ftp :
					CreateSocket
					FtpRequest
					InfoConnection
					MessageManger
					Serveur
					
			test :
					ServeurTest
					TestFtpRequest
					
			test.file :
			
					TestFileManagement
					
			test.ftp :
			
					TestCreateSocket
					TestFtpRequest
			
			
	Design patterns:
        Singleton
        Chaîne de responsabilitée
		
	Gestion d'erreurs :
	
		Catch : 
		
			catch (IOException e):
					- FtpList / public void action(final String argument) (2 fois)
					- FtpRetr / public void action(final String argument)
					- FtpStor / public void action(final String argument)
					- FileManagement / public boolean writeFile(final byte[] data, final String fichier) (2 fois)
					- FileManagement / public byte[] readFile(final String f) (2 fois)
					- CreateSocket / public Socket getSocket(final ServerSocket ss)
					- CreateSocket / public ServerSocket getServerSocket(final int port)
					- CreateSocket / public boolean closeServerSocket(final ServerSocket ss)
					- MessageManger / private BufferedReader getNewReader(final Socket socket)
					- MessageManger / private DataOutputStream getNewWriter(final Socket socket)
					- MessageManger / public boolean sendMessage(final String message)
					- MessageManger /  public boolean sendMessageByte(final byte[] message)
					- MessageManger / public String receiveMessage()
					- MessageManger / public byte[] receiveMessageByte() (2 fois)
					
			catch (UnknownHostException e) :
					- FtpList / public void action(final String argument)
					- FtpStor / public void action(final String argument)
					
			catch (NullPointerException e) :
					- FtpList / public void action(final String argument)
					
			catch (NumberFormatException e) :
					- FtpPort / public void action(final String argument)
					
			catch ( FileNotFoundException e) :
					- FileManagement / public boolean writeFile(final byte[] data, final String fichier)
					- FileManagement / public byte[] readFile(final String f)
			
			catch (final NoSuchElementException e) :
					-FtpRequest / public void processRequest()
			
			catch (UnsupportedEncodingException e) :
					- TestFileManagement / public void testWriteFile()
					
			catch (InterruptedException e) :µ
					TestFtpRequest / public void TestSTOR()
					
	Throw : 
			
			 NullPointerException :
					- FtpCommand / public FtpCommand(final InfoConnection info)
					- FtpRequest / public FtpRequest(final InfoConnection info)
					- MessageManger / public MessageManager(final Socket socket) (2 fois)
					
	throws : 
			
			UnknownHostException :
					- TestFtpRequest / public void TestAuthentification()
					- TestFtpRequest / public void TestConnection()
			IOException :
					- TestFtpRequest / public void TestAuthentification()
					- TestFtpRequest / public void TestConnection()
					
*** 3/ Parcours du code (code samples)
	
	Liste des classes utilisant le Design pattern Chaîne de responsabilitée :
		
			- FtpCdup
			- FtpCwd
			- FtpList
			- FtpPass
			- FtpPasv
			- FtpPort
			- FtpPwd
			- FtpQuit
			- FtpRetr
			- FtpStor
			- FtpUser

	code :
	public void execute(final String name, final String argument, final InfoConnection info) {

		this.info = info;
		if (name.compareTo(USER) == 0) {
			this.action(argument);
		} else {
			this.successor(name, argument);
		}

	}
			
	Design pattern Singleton : 
	 
			- FtpCdup
			- FtpCwd
			- FtpList
			- FtpPass
			- FtpPasv
			- FtpPort
			- FtpPwd
			- FtpQuit
			- FtpRetr
			- FtpStor
			- FtpUser
	
	code : 	
	public class FtpUser extends FtpCommand {

	private static final String USER = "USER";
	private static FtpUser command;

	private FtpUser() {
	}

	public static FtpCommand getInstance() {

		if (command == null) {
			command = new FtpUser();
		}
		return command;
	}

}

	Classe abstraite : FtpCommand
	
	Nous avons utilisons une classe abstraite car chaque commande à la même structure
	et certaines commandes possèdent des messages en commun.
	
*** 4/ Compatibilité

Nous avons tester notre serveur seulement avec la commande ftp. Il n'est à priori pas compatible
avec filezilla.	

*** 5/ Remarques
	
	- Les test unitaires de list et de stor ne fonctionne pas mais les commandes marche avec la commande ftp.