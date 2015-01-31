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
	
	private static final String END_LINE = "\r\n";
	private static final String ANONYMOUS = "anonymous";
	private static final String PROMPT = "ftp>";
	
	/* message envoyé */
	private static final String WELCOME = "\r\nBienvenue sur le serveur FTP de Elliot Vanegue et Salsabile Hakimi\r\n";
	private static final String GOODBYE = "\r\n Goodbye. \r\n";
	private static final String LOGIN_OK = "230 Login successful." + END_LINE;
	private static final String ERROR_NO_COMMAND = "202 Command not implemented, superfluous at this site." + END_LINE;
	private static final String ERROR_PARAMETER = "501 Syntax error in parameters or arguments." + END_LINE;
	private static final String SPECIFY_MDP = "331 Please specify the password." + END_LINE;
	
	/* message pour le developpeur */
	private static final String ERROR_MESSAGE = "Erreur dans l'envoie du message au client";
	private static final String ERROR_OUTPUTSTREAM = "Erreur dans l'initialisation du OutputStream";
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";
	private static final String ERROR_READ = "Erreur lors de la lecture";
	private static final String ERROR_INPUT = "Erreur sur la creation de l'InputStream";
	private static final String ERROR_CLOSE_SOCKET_CLIENT = "Erreur lors de la fermetur du socket client";
	
	/* nom de commande */
	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String RETR = "retr";
	private static final String STOR = "stor";
	private static final String LIST = "list";
	private static final String QUIT = "quit";
	
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
	
	/**
	 * Vérifie si l'utilisateur est connecté 
	 */
	private boolean isConnected;
	
	/**
	 * Login de l'utilisateur
	 */
	private String login;
	
	/**
	 * Mot de passe de l'utilisateur
	 */
	private String mdp;

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
		isConnected = false;
		
		login = "";
		mdp = "";

	}

	@Override
	public void run() {
		sendMessage(END_LINE);
		//sendMessage(WELCOME);
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
					sendMessage(ERROR_NO_COMMAND);
					continue;
				}

				/* execution de la commande */
				try {
					runCommand(instruction, parse.nextToken());
				}catch (final NoSuchElementException e) {
					runCommand(instruction, "");
				}
				
				isPromptWrite = false;
			}
		}

	}

	public void processUSER(final String parametre) {
		
		if(!checkCommand(parametre)) {
			sendMessage(ERROR_PARAMETER);
			return;
		}
		
		if(isConnected) {
			return;
		}
		
		/* TODO recherche dans un fichier ou une base */
		login = parametre;
		
		sendMessage(SPECIFY_MDP);
		
	}

	public void processPASS(final String parametre) {
		System.out.println("je passe dans pass");
		//TODO
		sendMessage(LOGIN_OK);
	}

	public void processRETR(final String parametre) {
		System.out.println("je passe dans retr");
	}

	public void processSTOR(final String parametre) {
		System.out.println("je passe dans stor");
	}

	public void processLIST(final String parametre) {
		System.out.println("je passe dans list");
	}

	public void processQUIT() {
		try {
			socket.close();
		} catch (final IOException e) {
			System.err.println(ERROR_CLOSE_SOCKET_CLIENT);
		}
		
		isFinish = true;
		sendMessage(GOODBYE);

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
			return null;
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
			sendMessage(ERROR_NO_COMMAND);
			return;
		}
		
		String instructionFormat = instruction.trim();
		instructionFormat = instructionFormat.toLowerCase();
		instructionFormat = instructionFormat.replaceAll("[\r\n]+", "");
		
		System.out.println(instruction + " " + parametre);
		
		switch(instructionFormat) {
		case USER : 
			processUSER(parametre);
			break;
		case PASS : 
			processPASS(parametre);
			break;
		case RETR : 
			processRETR(parametre);
			break;
		case STOR : 
			processSTOR(parametre);
			break;
		case LIST : 
			processLIST(parametre);
			break;
		case QUIT : 
			processQUIT();
			break;
		default :
			sendMessage(ERROR_NO_COMMAND);
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