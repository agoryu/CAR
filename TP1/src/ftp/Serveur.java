package ftp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe gérant les connextions au serveur ftp
 * 
 * @author elliot et salsabile
 *
 */
public class Serveur {

	/* nc -v ftp.univ-lille1.fr 21 */
	/* fin de ligne \r\n */

	/* message d'erreur */

	private static final String ERROR_SOCKET_NULL = "Erreur socket null";
	private static final String ERROR_SERVER_NULL = "Erreur serveur socket null";

	private static final String ERROR_ARGUMENT = "Pas assez d'arguemnt pour lancer le serveur";

	/**
	 * Map contenant les identifiant des clients.
	 */
	private static Map<String, String> bdd;

	/**
	 * Port du serveur
	 */
	public static final int port = 1024;

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println(ERROR_ARGUMENT);
			return;
		}

		final String directory = args[0];
		ServerSocket ss = null;
		CreateSocket cs = new CreateSocket ();

		/* creation du socket du serveur */
		if ((ss = cs.getServerSocket(port)) == null) {
			System.err.println(ERROR_SERVER_NULL);
			return;
		}
		Socket socket = null;

		bdd = new HashMap<>();
		bdd.put("anonymous", "");
		bdd.put("elliot", "link");
		bdd.put("salsabile", "ok");
		
		while (true) {

			/* reception du socket client */
			if ((socket = cs.getSocket(ss)) == null) {
				System.err.println(ERROR_SOCKET_NULL);
			}

			createThread(socket, directory);

		}
	}	

	/**
	 * Création d'un thread qui va interagir avec un client connecté
	 * 
	 * @param socket
	 *            Socket de connection
	 * @param directory
	 *            Répertoir à la disposition d'un client
	 */
	private static void createThread(final Socket socket, final String directory) {
		
		if(socket == null) {
			return;
		}
		
		final InfoConnection tmp = new InfoConnection(bdd, directory, new MessageManager(socket));
		final FtpRequest ft = new FtpRequest(tmp);
		final Thread t = new Thread(ft);
		t.start();
	}

}
