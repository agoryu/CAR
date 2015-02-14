package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CreateSocket {

	/* message d'erreur */
	private static final String ERROR_SOCKET_CLIENT = "Erreur lors de la reception du socket client";
	private static final String ERROR_SERVER_SOCKET = "Erreur lors de l'initialisation du serveur socket";
	private static final String ERROR_CLOSE_SOCKET = "Erreur lors de la fermeture du serveur socket";

	/**
	 * Création d'un socket la connexion d'un client
	 * 
	 * @param ss
	 *            Serveur qui doit créer la connexion
	 * @return Socket correspondant à la connexion d'un client
	 */
	public Socket getSocket(final ServerSocket ss) {

		Socket socket = null;

		try {
			socket = ss.accept();
		} catch (final IOException e) {
			System.err.println(ERROR_SOCKET_CLIENT);
		}
		return socket;
	}

	/**
	 * Création d'un socket serveur
	 * 
	 * @return socket serveur
	 */
	public ServerSocket getServerSocket(final int port) {

		ServerSocket ss = null;

		try {
			ss = new ServerSocket(port);
		} catch (final IOException e) {
			System.err.println(ERROR_SERVER_SOCKET);
		}
		return ss;
	}
	
	/** Ferme un server socket
	 * @param ss Server socket à fermer 
	 */
	public boolean closeServerSocket(final ServerSocket ss) {

		if(ss == null) {
			return false;
		}
		try {
			ss.close();
			return true;
		} catch (final IOException e) {
			System.err.println(ERROR_CLOSE_SOCKET);
		}
		
		return false;
		
	}

}
