package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Gestionnaire de message ftp
 * 
 * @author elliot
 *
 */
public class MessageManager {

	private static final String END_LINE = "\r\n";
	private static final String ERROR_MESSAGE = "Erreur dans l'envoie du message au client";
	private static final String ERROR_OUTPUTSTREAM = "Erreur dans l'initialisation du OutputStream";
	private static final String ERROR_READ = "Erreur lors de la lecture";
	private static final String ERROR_INPUT = "Erreur sur la creation de l'InputStream";
	private static final String ERROR_CLOSE_SOCKET_CLIENT = "Erreur lors de la fermetur du socket client";

	/**
	 * Objet permettant la lecture sur la connection
	 */
	private BufferedReader reader;

	/**
	 * Objet permettant l'écriture sur la connection
	 */
	private DataOutputStream writer;
	
	/**
	 * Connection avec un client
	 */
	private Socket socket;

	public MessageManager(final Socket socket) {

		
		reader = getNewReader(socket);
		writer = getNewWriter(socket);
		this.socket = socket;
	}

	/**
	 * Retourne Un objet permettant de lire sur la connection courant
	 * 
	 * @param socket
	 *            Connection avec l'utilisateur
	 * @return Objet permettant la lecture de message
	 */
	private BufferedReader getNewReader(final Socket socket) {

		InputStream in = null;

		if (socket == null) {
			return null;
		}

		try {
			in = socket.getInputStream();
		} catch (final IOException e) {
			System.err.println(ERROR_INPUT);
			return null;
		}

		if (in == null) {
			return null;
		}

		return new BufferedReader(new InputStreamReader(in));
	}

	/**
	 * Retourne Un objet permettant d'écrire sur la connection courant
	 * 
	 * @param socket
	 *            Connection avec l'utilisateur
	 * @return Objet permettant l'envoie de message
	 */
	private DataOutputStream getNewWriter(final Socket socket) {

		OutputStream os = null;
		try {
			os = socket.getOutputStream();
		} catch (final IOException e) {
			System.err.println(ERROR_OUTPUTSTREAM);
			return null;
		}

		if (os == null) {
			return null;
		}

		return new DataOutputStream(os);
	}

	/**
	 * Envoie d'un message au client courant
	 * 
	 * @param message
	 *            Message à envoyer
	 * @return Vrai si l'action s'est bien passé, faux sinon
	 */
	public boolean sendMessage(final String message) {

		try {
			writer.writeBytes(message + END_LINE);
		} catch (final IOException e) {
			System.err.println(ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Envoie d'un message en byte au client courant
	 * 
	 * @param message
	 *            Message à envoyer
	 * @return Vrai si l'action s'est bien passé, faux sinon
	 */
	public boolean sendMessageByte(final byte[] message) {

		try {
			writer.writeBytes(message + END_LINE);
		} catch (final IOException e) {
			System.err.println(ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	/**
	 * Réceptionne un message venant d'un client
	 * 
	 * @param ibr
	 *            Objet permettant la lecture sur la connection
	 * @return La commande ou null en cas de problème
	 */
	public String receiveMessage() {

		String commande = null;

		try {
			commande = reader.readLine();
		} catch (final IOException e1) {
			System.err.println(ERROR_READ);
			return null;
		}
		return commande;
	}
	
	public void closeConnection() {
		
		try {
			socket.close();
		} catch (final IOException e) {
			System.err.println(ERROR_CLOSE_SOCKET_CLIENT);
		}
	}
}
