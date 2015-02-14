package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import file.FileMagnagement;

/**
 * Classe qui permet de traiter les commandes FTP
 * 
 * @author elliot et salsabile
 *
 */
public class FtpCommand {

	private static final String PASV_MESSAGE = "227 127,0,0,1,4,2";
	private static final String ERROR_CONNECTION = "425 Can't open data connection.";
	private static final String FORGOT_PORT = " : you forgot the command PORT or PASV";
	private static final String NOT_A_NUMBER = " : parameter is not a number";
	private static final String BAD_FORMAT = " : bad format -> num,num,num,num,num,num";
	private static final String NO_PARAMETER = " : no parameter";
	private static final String END_CONNECTION_DATA = "226 Requested file action successful";
	private static final String BEGIN_CONNECTION_DATA = "150 open data connection";
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";

	private static final String ANONYMOUS = "anonymous";
	private static final String GOODBYE = "221 Goodbye."; // TODO
	private static final String LOGIN_OK = "230 Login successful.";
	private static final String WELCOME = "220 Service ready for new user.";
	private static final String ERROR_PARAMETER = "501 Syntax error in parameters or arguments";
	private static final String SPECIFY_MDP = "331 Please specify the password.";
	private static final String ERROR_IDENTIFICATION = "430 Invalid username or password.";
	private static final String PORT_SUCCESSFUL = "200 PORT command successful.";
	
	private static final String ERROR_SOCKET_NULL = "Erreur socket null";

	// TODO
	//private static final String ERROR_SEND_FILE = "Erreur lors de la lecture du fichier";

	/**
	 * Gestionnaire de message
	 */
	private MessageManager messageMan;

	/**
	 * Base de données contenant les noms et mots de passe des utilisateur
	 */
	private Map<String, String> bdd;

	/**
	 * Port spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	private Integer portDownload;

	/**
	 * Adresse spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	private String ipDownload;

	/**
	 * Repertoire à la disposition du client
	 */
	private String directory;

	/**
	 * Login de l'utilisateur
	 */
	private String login;
	
	/**
	 * Initialise la classe avec l'ensemble des parametre de connection
	 * 
	 * @param directory
	 *            Dossier visible par le client
	 * @param bdd
	 *            BDD contenant les comptes client
	 * @param socket
	 *            Connection avec le client
	 * @param messageManager
	 *            Objet permettant l'envoie et la reception de message du client
	 */
	public FtpCommand(final String directory, final Map<String, String> bdd,
			final Socket socket, final MessageManager messageManager) {

		if (socket == null || directory == null || bdd == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		if (bdd.isEmpty()) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		this.directory = directory;
		this.bdd = bdd;

		this.messageMan = messageManager;
	}

	/**
	 * Message envoyé lors de la connection avec un client
	 */
	public void connection() {
		messageMan.sendMessage(WELCOME);
	}

	/**
	 * Permet de récupérer le login de l'utilisateur
	 * 
	 * @param login
	 *            Login donné par l'utilisateur
	 * @return Le login si il est valide, null sinon
	 */
	public synchronized void processUSER(final String login) {

		if (!checkCommand(login)) {
			messageMan.sendMessage(ERROR_PARAMETER+"");
			return;
		}

		if (bdd.containsKey(login)) {
			messageMan.sendMessage(SPECIFY_MDP);
			this.login = login;
		} else {
			messageMan.sendMessage(ERROR_IDENTIFICATION);
			return;
		}
		 

	}

	/**
	 * Vérifie l'identité de l'utilisateur avec son mot de passe
	 * 
	 * @param mdp
	 *            Mot de passe fournit par l'utilisateur
	 * @param login
	 *            Login de l'utilisateur
	 * @return True si le client est connecté, false sinon
	 */
	public synchronized boolean processPASS(final String mdp) {

		if (mdp == null) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return false;
		}

		if (!checkCommand(this.login)) {
			messageMan.sendMessage(ERROR_IDENTIFICATION);
			return false;
		}

		if (login.compareTo(ANONYMOUS) == 0) {
			messageMan.sendMessage(LOGIN_OK);
			return true;
		}

		if (bdd.get(login).compareTo(mdp) == 0) {
			messageMan.sendMessage(LOGIN_OK);
			return true;
		} else {
			messageMan.sendMessage(ERROR_IDENTIFICATION);
			return false;
		}

	}

	/**
	 * Récupère les informations de connection pour l'envoie de fichier
	 * 
	 * @param connectionInfo
	 *            Information permettant de se connecter avec le client
	 */
	public synchronized void processPORT(final String connectionInfo) {

		if (!checkCommand(connectionInfo)) {
			messageMan.sendMessage(ERROR_PARAMETER + NO_PARAMETER);
			return;
		}
		
		final Pattern patt = Pattern.compile("\\d{1,3}(,\\d{1,3}){5}");
		final Matcher mat = patt.matcher(connectionInfo);
		
		if(!mat.matches()) {
			messageMan.sendMessage(ERROR_PARAMETER + BAD_FORMAT);
			return;
		}

		final StringTokenizer parseParameter = new StringTokenizer(
				connectionInfo, ",");
		
		String ip = "";
		Integer port = 0;
		
		for(int i=0; i<4; i++) {
			ip += parseParameter.nextToken() + ".";
		}
		
		ip = ip.substring(0, ip.length() - 1);
		
		try {
			final Integer multiplicateur = Integer.parseInt(parseParameter.nextToken());
			port = 256 * multiplicateur;
			final Integer additionneur = Integer.parseInt(parseParameter.nextToken());
			port += additionneur;
		} catch(final NumberFormatException e) {
			messageMan.sendMessage(ERROR_PARAMETER + NOT_A_NUMBER);
			return;
		}

		if (!checkCommand(ip) || port == 0) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return;
		}

		portDownload = port;
		ipDownload = ip;
		
		messageMan.sendMessage(PORT_SUCCESSFUL);
	}

	/**
	 * Envoie les données contenu dans un fichier du serveur au client
	 * 
	 * @param filename
	 *            Nom de fichier à envoyer
	 */
	public synchronized void processRETR(final String filename) {

		if (!checkCommand(filename)) {
			messageMan.sendMessage(ERROR_PARAMETER + NO_PARAMETER);
			return;
		}

		/* voir le message a mettre */
		if (!checkCommand(ipDownload) || portDownload== 0) {
			messageMan.sendMessage(ERROR_PARAMETER + FORGOT_PORT);
			return;
		}

		final FileMagnagement management = new FileMagnagement();
		final byte[] data = management.lire(directory + filename);

		Socket socket = null;
		
		try {
			socket = new Socket(ipDownload, portDownload);
		} catch (final IOException e) {
			// TODO
			messageMan.sendMessage(ERROR_CONNECTION);
			return;
		}
		
		messageMan.sendMessage(BEGIN_CONNECTION_DATA);

		final MessageManager mm = new MessageManager(socket);
		mm.sendMessageByte(data);
		if (socket != null) {
			mm.closeConnection();
		}
		
		messageMan.sendMessage(END_CONNECTION_DATA);
		
		ipDownload = "";
		portDownload = 0;
		
	}

	/**
	 * Reçoit les données à sauvegarder dans un fichier sur le serveur
	 * 
	 * @param filename Nom de fichier à recevoir 
	 *            TODO
	 */
	public synchronized void processSTOR(final String filename) {
		
		if (!checkCommand(filename)) {
			messageMan.sendMessage(ERROR_PARAMETER + NO_PARAMETER);
			return;
		}

		/* voir le message a mettre */
		if (!checkCommand(ipDownload) || portDownload== 0) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return;
		}
		
		ServerSocket serveurSocket = null;
		final CreateSocket cs = new CreateSocket();
		
		/* Creation du serveurSocket */
		if ((serveurSocket = cs.getServerSocket(portDownload)) == null) {
			//TODO un message doit etre envoyé à l'utilisateur
			System.err.println(ERROR_SOCKET_NULL);
			return;
		}
		
		Socket socket = null;
		
		if ((socket = cs.getSocket(serveurSocket)) == null) {
			//TODO un message doit etre envoyé à l'utilisateur
			System.err.println(ERROR_SOCKET_NULL);
		}

		messageMan.sendMessage(BEGIN_CONNECTION_DATA);
		
		/* Recevoir le fichier du répertoire local*/
		final MessageManager mm = new MessageManager (socket);
		byte[] data = mm.receiveMessageByte();
		System.out.println("test");
		
		/* Ecrire le ficher dans le répertoire distant*/ 
		final FileMagnagement management = new FileMagnagement();
		management.ecrire(data, directory+filename);
		
		if(socket != null) {
			mm.closeConnection();
		}
		
		messageMan.sendMessage(END_CONNECTION_DATA);
		
		cs.closeServerSocket(serveurSocket);
		
		ipDownload = "";
		portDownload = 0;
	}

	/**
	 * Méthode permettant de lister les fichier dans un dossier
	 * 
	 * @param parametre
	 *            Une chaine représentant un dossier ou rien si il faut lister
	 *            les fichiers du dossier courant
	 */
	public void processLIST(final String parametre) {
		System.out.println("je passe dans list");
	}

	/**
	 * Affiche le dossier courant
	 */
	public void processPWD() {
		messageMan.sendMessage(directory);
	}

	/**
	 * Envoit un message et ferme la connection
	 */
	public synchronized void processQUIT() {

		messageMan.sendMessage(GOODBYE);
		messageMan.closeConnection();

	}
	
	public synchronized void processPASV() {
		
		messageMan.sendMessage(PASV_MESSAGE);
		ipDownload = "127.0.0.1";
		portDownload = 1026;
	}

	/**
	 * Vérifie qu'une chaine de caractere n'est pas null ou vide
	 * 
	 * @param commande
	 *            Chaine de caractère à vérifier
	 * @return True si la chaine contient quelque chose, false sinon
	 */
	public boolean checkCommand(final String commande) {

		if (commande == null) {
			return false;
		}

		if (commande.compareTo("") == 0) {
			return false;
		}

		return true;
	}
}
