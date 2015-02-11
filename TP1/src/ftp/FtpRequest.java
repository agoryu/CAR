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
	
	private static final String ERROR_NO_COMMAND = "202 Command not implemented, superfluous at this site.";

	/* message pour le developpeur */
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";

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
	 * Executeur de commande
	 */
	private FtpCommand runCommand;
	
	/**
	 * Gestionnaire de message
	 */
	private MessageManager messageMan;

	public FtpRequest(final Socket socket, final String directory,
			final Map<String, String> bdd) {

		if (socket == null || directory == null || bdd == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		if (bdd.isEmpty()) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		isFinish = false;
		isConnected = false;

		messageMan = new MessageManager(socket);
		runCommand = new FtpCommand(directory, bdd, socket, messageMan);

		login = "";

	}

	@Override
	public void run() {
		runCommand.connection();
		processRequest();

	}

	/**
	 * Attend des commandes de la part du client courant
	 */
	public void processRequest() {

		while (!isFinish) {

			/* gestion de la requete par un thread */
			String commande = null;
			commande = messageMan.receiveMessage();

			if (runCommand.checkCommand(commande)) {
				// Recupérer le nom de la commande
				final StringTokenizer parse = new StringTokenizer(commande, " ");
				final String instruction = parse.nextToken();

				if (instruction == null) {
					continue;
				}

				if (instruction.compareTo("") == 0) {
					messageMan.sendMessage(ERROR_NO_COMMAND);
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
			messageMan.sendMessage(ERROR_NO_COMMAND);
			return;
		}

		String instructionFormat = instruction.trim();
		// instructionFormat = instructionFormat.toLowerCase();
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
			if(!isConnected)
				login = runCommand.processUSER(parametreFormat);
			break;
		case PASS:
			if(!isConnected)
				isConnected = runCommand.processPASS(parametreFormat, login);
			break;
		case RETR:
			if(isConnected)
				runCommand.processRETR(parametreFormat);
			break;
		case STOR:
			if(isConnected)
				runCommand.processSTOR(parametreFormat);
			break;
		case LIST:
			if(isConnected)
				runCommand.processLIST(parametreFormat);
			break;
		case QUIT:
			runCommand.processQUIT();
			break;
		case PORT:
			if(isConnected)
				runCommand.processPORT(parametreFormat);
			break;
		case PWD:
			if(isConnected)
				runCommand.processPWD(parametreFormat);
			break;
		default:
			messageMan.sendMessage(ERROR_NO_COMMAND);
			break;
		}
	}

}