package ftp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Serveur {
	
	/* nc -v ftp.univ-lille1.fr 21 */
	/* fin de ligne \r\n */

	/* message d'erreur */
	
	private static final String ERROR_SOCKET_NULL = "Erreur socket null";
	private static final String ERROR_SERVER_NULL = "Erreur serveur socket null";
	private static final String ERROR_SOCKET_CLIENT = "Erreur lors de la reception du socket client";
	private static final String ERROR_SERVER_SOCKET = "Erreur lors de l'initialisation du serveur socket";
	private static final String ERROR_ARGUMENT = "Pas assez d'arguemnt pour lancer le serveur";
	
	private static Map<String,String> bdd;
	
	
	public static final int port = 1024;
	
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.out.println(ERROR_ARGUMENT);
			return;
		}
		
		final String directory = args[0];
		ServerSocket ss = null;
		
		/* creation du socket du serveur */
		if((ss = getServerSocket()) == null) {
			System.err.println(ERROR_SERVER_NULL);
			return;
		}
		Socket socket = null;
		
		bdd = new HashMap<>();
		bdd.put("anonymous", "");
		bdd.put("elliot", "link");
		bdd.put("salsabil", "ok");
		
		while(true){
			
			/* reception du socket client */
			if((socket = getSocket(ss)) == null) {
				System.err.println(ERROR_SOCKET_NULL);
			} 

			createThread(socket, directory);
			
	    }
	}

	/** Création d'un socket la connexion d'un client
	 * @param ss Serveur qui doit créer la connexion
	 * @return Socket correspondant à la connexion d'un client
	 */
	private static Socket getSocket(final ServerSocket ss) {
		
		Socket socket = null;
		
		try {
			socket = ss.accept();
		} catch (final IOException e) {
			System.err.println(ERROR_SOCKET_CLIENT);
		}
		return socket;
	}

	/** Création d'un socket serveur
	 * @return socket serveur
	 */
	private static ServerSocket getServerSocket() {
		
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(port);
		} catch (final IOException e) {
			System.err.println(ERROR_SERVER_SOCKET);
		}
		return ss;
	}
	
	/** Création d'un thread qui va interagir avec un client connecté
	 * @param socket Socket de connection
	 * @param directory Répertoir à la disposition d'un client 
	 */
	private static void createThread(final Socket socket, final String directory) {
		final FtpRequest ft = new FtpRequest(socket, directory, bdd);
		final Thread t = new Thread(ft);
		t.start();	
	}

}
