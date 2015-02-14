package ftp;

import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author elliot et salsabile
 *
 */
public class FtpRequest implements Runnable {
	
	private static final String ERROR_NOT_CONNECTED = "530 Not logged in.";
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
	private static final String PASV = "PASV";
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
	}

	@Override
	public void run() {
		runCommand.connection();
		processRequest();

	}

	/**
	 * Méthode qui attend des messages du client
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

		String instructionFormat = instruction.replaceAll("[\r\n]+", "");

		//enléve les retours chariots
		String parametreFormat = parametre.replaceAll("[\r\n]+", "");
		
		System.out.println(instructionFormat + " " + parametreFormat);

		if(instruction.compareTo(USER) == 0) {
			if(!isConnected)
				runCommand.processUSER(parametreFormat);
		} else if(instruction.compareTo(PASS) == 0) {
			if(!isConnected)
				isConnected = runCommand.processPASS(parametreFormat);
		} else if(instruction.compareTo(RETR) == 0) {
			if(isConnected)
				runCommand.processRETR(parametreFormat);
			else
				messageMan.sendMessage(ERROR_NOT_CONNECTED);
		} else if(instruction.compareTo(STOR) == 0) {
			if(isConnected)
				runCommand.processSTOR(parametreFormat);
			else
				messageMan.sendMessage(ERROR_NOT_CONNECTED);
		} else if(instruction.compareTo(LIST) == 0) {
			if(isConnected)
				runCommand.processLIST(parametreFormat);
			else
				messageMan.sendMessage(ERROR_NOT_CONNECTED);
		} else if(instruction.compareTo(QUIT) == 0) {
			runCommand.processQUIT();
			isFinish = true;
			isConnected = false;
		} else if(instruction.compareTo(PORT) == 0) {
			if(isConnected)
				runCommand.processPORT(parametreFormat);
			else
				messageMan.sendMessage(ERROR_NOT_CONNECTED);
		} else if(instruction.compareTo(PASV) == 0) {
			if(isConnected)
				runCommand.processPASV();
			else
				messageMan.sendMessage(ERROR_NOT_CONNECTED);
		} else if(instruction.compareTo(PWD) == 0) {
			if(isConnected)
				runCommand.processPWD();
			else
				messageMan.sendMessage(ERROR_NOT_CONNECTED);
		} else {
			messageMan.sendMessage(ERROR_NO_COMMAND);
		}
	}

}