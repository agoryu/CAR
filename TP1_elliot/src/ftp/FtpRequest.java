package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class FtpRequest implements Runnable {

	private static final String ERROR_NO_COMMAND = "Commande non implémenté";
	private static final String ERROR_INSTRUCTION_EMPTY = "Erreur l'instruction est vide";
	private static final String ERROR_MESSAGE = "Erreur dans l'envoie du message au client";
	private static final String ERROR_OUTPUTSTREAM = "Erreur dans l'initialisation du OutputStream";
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";
	private static final String ERROR_READ = "Erreur lors de la lectur";
	private static final String ERROR_INPUT = "Erreur sur la creation de l'InputStream";
	private static final String WELCOME = "\r\nBienvenue sur le serveur FTP de Elliot Vanegue et Salsabile Hakimi\r\n";
	private static final String ERROR_CLOSE_SOCKET_CLIENT = "Erreur lors de la fermetur du socket client";
	private static final String PROMPT = "ftp>";
	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String RETR = "retr";
	private static final String STOR = "stor";
	private static final String LIST = "list";
	private static final String QUIT = "quit";
	private static final String END_LINE = "\r\n";

	
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
	
	/**
	 * Repertoir à la disposition du client
	 */
	private String direcory;
	
	/**
	 * Variable permettant la fermeture de la connection
	 */
	private boolean isFinish;

	public FtpRequest(final Socket socket, final String directory) {

		if (socket == null || directory == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		this.socket = socket;
		this.direcory = directory;

		writer = getNewWriter(socket);
		if (writer == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}
		reader = getNewReader(socket);
		if (reader == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}
		
		isFinish = false;

	}

	@Override
	public void run() {

		this.sendMessage(WELCOME);
		processRequest();

	}

	/**
	 * Attend des commandes de la part du client courant
	 */
	public void processRequest() {
		
		boolean isPromptWrite = false;

		while (!isFinish) {
			
			/* affichage du prompt */
			if(!isPromptWrite) {
				isPromptWrite = true;
				sendMessage(PROMPT);
			}

			/* gestion de la requete par un thread */
			String commande = null;
			commande = getCommand(reader);

			if (checkCommand(commande)) {
				final StringTokenizer parse = new StringTokenizer(commande, " ");
				final String instruction = parse.nextToken();

				if (instruction == null) {
					continue;
				}

				if (instruction.compareTo("") == 0) {
					System.err.println(ERROR_INSTRUCTION_EMPTY);
					continue;
				}

				try {
					runCommand(instruction, parse.nextToken());
				}catch (final NoSuchElementException e) {
					runCommand(instruction, "");
				}
			}
		}

	}

	public void processUSER() {
		System.out.println("je passe dans user");
	}

	public void processPASS() {
		System.out.println("je passe dans pass");
	}

	public void processRETR() {
		System.out.println("je passe dans retr");
	}

	public void processSTOR() {
		System.out.println("je passe dans stor");
	}

	public void processLIST() {
		System.out.println("je passe dans list");
	}

	public void processQUIT() {
		System.out.println("je passe dans quit");
		try {
			socket.close();
		} catch (final IOException e) {
			System.err.println(ERROR_CLOSE_SOCKET_CLIENT);
		}
		
		isFinish = true;

	}

	/**
	 * Vérifie qu'une chaine de caractere n'est pas null ou vide
	 * 
	 * @param commande
	 *            Chaine de caractère à vérifier
	 * @return True si la chaine contient quelque chose, false sinon
	 */
	private boolean checkCommand(final String commande) {

		if (commande == null) {
			return false;
		}

		if (commande.compareTo("") == 0) {
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
	private String getCommand(final BufferedReader ibr) {

		String commande = null;

		try {
			commande = ibr.readLine();
		} catch (final IOException e1) {
			System.err.println(ERROR_READ);
		}
		return commande;
	}

	/**
	 * Méthode qui sélectionne l'action a effectuer en fonction de l'instruction
	 * envoyé par le client
	 * 
	 * @param instruction
	 *            Instruction à lancer
	 * @param parametre
	 *            Paramètre de la commande
	 */
	private void runCommand(final String instruction, final String parametre) {
		
		if(instruction == null) {
			//TODO message erreur
			return;
		}
		
		String instructionFormat = instruction.trim();
		instructionFormat = instructionFormat.toLowerCase();
		instructionFormat = instructionFormat.replaceAll("[\r\n]+", "");
		
		switch(instructionFormat) {
		case USER : 
			processUSER();
			break;
		case PASS : 
			processPASS();
			break;
		case RETR : 
			processRETR();
			break;
		case STOR : 
			processSTOR();
			break;
		case LIST : 
			processLIST();
			break;
		case QUIT : 
			processQUIT();
			break;
		default :
			System.err.println(ERROR_NO_COMMAND);
			break;
		}
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
	 * Retourne Un objet permettant de lire sur la connection courant
	 * 
	 * @param socket
	 *            Connection avec l'utilisateur
	 * @return Objet permettant la lecture de message
	 */
	private BufferedReader getNewReader(final Socket socket) {

		InputStream in = null;
		
		if(socket == null) {
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
	 * Envoie d'un message au client courant
	 * 
	 * @param message
	 *            Message à envoyer
	 * @return Vrai si l'action s'est bien passé, faux sinon
	 */
	private boolean sendMessage(final String message) {

		try {
			writer.writeBytes(message + END_LINE);
		} catch (final IOException e) {
			System.err.println(ERROR_MESSAGE);
			return false;
		}

		return true;
	}

}