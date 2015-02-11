package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import file.FileMagnagement;

/** 
 * @author agoryu
 *
 */
public class FtpRequest implements Runnable {

	private static final String END_LINE = "\r\n";
	private static final String ANONYMOUS = "anonymous";

	/* message envoyé */
	private static final String WELCOME = "220 Service ready for new user.";
	private static final String GOODBYE = "221 Goodbye."; // TODO
	private static final String LOGIN_OK = "230 Login successful.";
	private static final String ERROR_NO_COMMAND = "202 Command not implemented, superfluous at this site.";
	private static final String ERROR_PARAMETER = "501 Syntax error in parameters or arguments.";
	private static final String SPECIFY_MDP = "331 Please specify the password.";
	private static final String ERROR_IDENTIFICATION = "430 Invalid username or password.";
	private static final String ERROR_SEND_FILE = "Erreur lors de la lecture du fichier"; //TODO voir le message

	/* message pour le developpeur */
	private static final String ERROR_MESSAGE = "Erreur dans l'envoie du message au client";
	private static final String ERROR_OUTPUTSTREAM = "Erreur dans l'initialisation du OutputStream";
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";
	private static final String ERROR_READ = "Erreur lors de la lecture";
	private static final String ERROR_INPUT = "Erreur sur la creation de l'InputStream";
	private static final String ERROR_CLOSE_SOCKET_CLIENT = "Erreur lors de la fermetur du socket client";
	private static final String ERROR_SOCKET = "Erreur lors de la creation du socket serveur";


	/* nom de commande */
	private static final String USER = "USER";
	private static final String PASS = "PASS";
	private static final String RETR = "RETR";
	private static final String STOR = "STOR";
	private static final String LIST = "LIST";
	private static final String QUIT = "QUIT";
	private static final String PORT = "PORT";
	private static final String PWD = "PWD";

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
	 * Repertoire à la disposition du client
	 */
	private String direcory; // chemin du fichier

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
	 * Base de données contenant les noms et mots de passe des utilisateur
	 */
	private Map<String,String> bdd;
	
	/**
	 * Port spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	private String portDownload;
	
	/**
	 * Adresse spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	private String ipDownload;

	public FtpRequest(final Socket socket, final String directory, final Map<String, String> bdd) {

		if (socket == null || directory == null || bdd == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}
		
		if (bdd.isEmpty()) {
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

		this.bdd = bdd;
		
		login = "";

	}

	@Override
	public void run() {
		sendMessage(WELCOME);
		processRequest();

	}

	/**
	 * Attend des commandes de la part du client courant
	 */
	public void processRequest() {

		while (!isFinish) {

			/* gestion de la requete par un thread */
			String commande = null;
			commande = getCommand(reader);

			if (checkCommand(commande)) {
				// Recupérer le nom de la commande
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
				} catch (final NoSuchElementException e) {
					runCommand(instruction, "");
				}
			}
		}

	}

	public void processUSER(final String login) {

		if (!checkCommand(login)) {
			sendMessage(ERROR_PARAMETER);
			return;
		}

		if (isConnected) {
			return;
		}

		/* TODO recherche dans un fichier ou une base */
		if(bdd.containsKey(login)) {
			this.login = login;
			sendMessage(SPECIFY_MDP);
		} else {
			sendMessage(ERROR_IDENTIFICATION);
		}
		

	}

	public void processPASS(final String mdp) {
		
		if (mdp == null) {
			sendMessage(ERROR_PARAMETER);
			return;
		}

		if (isConnected) {
			return;
		}
		
		if(!checkCommand(login)) {
			sendMessage(ERROR_IDENTIFICATION);
		}
		
		if(login.compareTo(ANONYMOUS) == 0) {
			isConnected = true;
			sendMessage(LOGIN_OK);
		}
		
		if(bdd.get(login).compareTo(mdp) == 0) {
			isConnected = true;
			sendMessage(LOGIN_OK);
		} else {
			sendMessage(ERROR_IDENTIFICATION);
		}
		
	}

	public void processPORT(final String connectionInfo) {
		
		if(!checkCommand(connectionInfo)) {
			sendMessage(ERROR_PARAMETER);
			return;
		}
		
		final StringTokenizer parseParameter = new StringTokenizer(connectionInfo, " ");
		
		final String port = parseParameter.nextToken();
		final String ip = parseParameter.nextToken();
		
		if(!checkCommand(port) || !checkCommand(ip)) {
			sendMessage(ERROR_PARAMETER);
			return;
		}
		
		portDownload = port;
		ipDownload = ip;
		
	}
	
	public void processRETR(final String filename) {
		
		if(!checkCommand(filename)) {
			sendMessage(ERROR_PARAMETER);
			return;
		}
		
		/* voir le message a mettre */
		if(!checkCommand(portDownload) || !checkCommand(ipDownload)) {
			sendMessage(ERROR_PARAMETER);
			return;
		}
		
		/* récupere le fichier dont le nom est dans parametre */
		//pas oublier la verif
		final FileMagnagement management = new FileMagnagement();
		final byte[] data = management.lire(filename);
		
		Integer iport = null;
		
		try {
			iport = Integer.parseInt(portDownload);
		} catch(final NumberFormatException e) {
			sendMessage(ERROR_PARAMETER);
		}
		
		Socket socket = null;
		
		try {
			socket = new Socket(ipDownload, iport);
		} catch (final IOException e) {
			System.err.println(ERROR_SOCKET);
		}
		
		final DataOutputStream writerFile = getNewWriter(socket);
		try {
			writerFile.write(data);
		} catch (final IOException e) {
			System.err.println(ERROR_SEND_FILE);
		} finally {
            try {
                if (socket != null) {
                	socket.close();
                }
            } catch (final IOException e) {
                System.err.println(ERROR_CLOSE_SOCKET_CLIENT);
            }
        }
	}

	//TODO recevoir , lire la socket et écrire le contenue dans le ftp
	public void processSTOR(final String parametre) {
		System.out.println("je passe dans stor");
	}

	public void processLIST(final String parametre) {
		System.out.println("je passe dans list");
	}
	
	public void processPWD(final String parametre) {
		sendMessage(direcory);
	}

	public void processQUIT() {
		try {
			sendMessage(GOODBYE);
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

		if (instruction == null) {
			sendMessage(ERROR_NO_COMMAND);
			return;
		}

		String instructionFormat = instruction.trim();
		//instructionFormat = instructionFormat.toLowerCase();
		instructionFormat = instructionFormat.replaceAll("[\r\n]+", "");

		String parametreFormat = parametre.trim();// enleve les espaces
		parametreFormat = parametreFormat.toLowerCase();// met en minuscule
		parametreFormat = parametreFormat.replaceAll("[\r\n]+", "");// enléve
																	// les
																	// retours
																	// chariots

		System.out.println(instruction + " " + parametre);

		switch (instructionFormat) {
		case USER:
			processUSER(parametreFormat);
			break;
		case PASS:
			processPASS(parametreFormat);
			break;
		case RETR:
			processRETR(parametreFormat);
			break;
		case STOR:
			processSTOR(parametreFormat);
			break;
		case LIST:
			processLIST(parametreFormat);
			break;
		case QUIT:
			processQUIT();
			break;
		case PORT:
			processPORT(parametreFormat);
			break;
		case PWD:
			processPWD(parametreFormat);
			break;
		default:
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
			e.printStackTrace();
			return false;
		}

		return true;
	}

}