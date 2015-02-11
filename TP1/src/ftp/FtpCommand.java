package ftp;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;

import file.FileMagnagement;

public class FtpCommand {

	private static final String ANONYMOUS = "anonymous";
	private static final String GOODBYE = "221 Goodbye."; // TODO
	private static final String LOGIN_OK = "230 Login successful.";
	private static final String WELCOME = "220 Service ready for new user.";
	private static final String ERROR_PARAMETER = "501 Syntax error in parameters or arguments.";
	private static final String SPECIFY_MDP = "331 Please specify the password.";
	private static final String ERROR_IDENTIFICATION = "430 Invalid username or password.";
	private static final String ERROR_SEND_FILE = "Erreur lors de la lecture du fichier"; // TODO
																							// voir
																							// le
																							// message

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
	private String portDownload;

	/**
	 * Adresse spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	private String ipDownload;

	/**
	 * Repertoire à la disposition du client
	 */
	private String direcory;

	public FtpCommand(final String directory, final Map<String, String> bdd,
			final Socket socket, final MessageManager messageManager) {

		this.direcory = directory;
		this.bdd = bdd;

		this.messageMan = messageManager;
	}

	public void connection() {
		messageMan.sendMessage(WELCOME);
	}

	public String processUSER(final String login) {

		if (!checkCommand(login)) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return null;
		}

		if (bdd.containsKey(login)) {
			messageMan.sendMessage(SPECIFY_MDP);
			return login;
		} else {
			messageMan.sendMessage(ERROR_IDENTIFICATION);
			return null;
		}

	}

	public boolean processPASS(final String mdp, final String login) {

		if (mdp == null) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return false;
		}

		if (!checkCommand(login)) {
			messageMan.sendMessage(ERROR_IDENTIFICATION);
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

	public void processPORT(final String connectionInfo) {

		if (!checkCommand(connectionInfo)) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return;
		}

		final StringTokenizer parseParameter = new StringTokenizer(
				connectionInfo, " ");

		final String port = parseParameter.nextToken();
		final String ip = parseParameter.nextToken();

		if (!checkCommand(port) || !checkCommand(ip)) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return;
		}

		portDownload = port;
		ipDownload = ip;

	}

	public void processRETR(final String filename) {

		if (!checkCommand(filename)) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return;
		}

		/* voir le message a mettre */
		if (!checkCommand(portDownload) || !checkCommand(ipDownload)) {
			messageMan.sendMessage(ERROR_PARAMETER);
			return;
		}

		/* récupere le fichier dont le nom est dans parametre */
		// pas oublier la verif
		final FileMagnagement management = new FileMagnagement();
		final byte[] data = management.lire(filename);

		Integer iport = null;

		try {
			iport = Integer.parseInt(portDownload);
		} catch (final NumberFormatException e) {
			messageMan.sendMessage(ERROR_PARAMETER);
		}

		Socket socket = null;

		try {
			socket = new Socket(ipDownload, iport);
		} catch (final IOException e) {
			// TODO
			System.err.println("530");
		}

		final MessageManager mm = new MessageManager(socket);
		mm.sendMessageByte(data);
		if (socket != null) {
			mm.closeConnection();
		}
	}

	// TODO recevoir , lire la socket et écrire le contenue dans le ftp
	public void processSTOR(final String parametre) {
		System.out.println("je passe dans stor");
	}

	public void processLIST(final String parametre) {
		System.out.println("je passe dans list");
	}

	public void processPWD(final String parametre) {
		messageMan.sendMessage(direcory);
	}

	public void processQUIT() {

		messageMan.sendMessage(GOODBYE);
		messageMan.closeConnection();

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
