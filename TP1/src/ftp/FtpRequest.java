package ftp;

import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import command.FtpUser;

/**
 * Thread qui gère un client.
 * 
 * @author elliot et salsabile
 *
 */
public class FtpRequest implements Runnable {

	private static final String ERROR_NO_COMMAND = "202 Command not implemented, superfluous at this site.";

	/* message pour le developpeur */
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";

	/**
	 * Executeur de commande
	 */
	private FtpCommand runCommand;

	/**
	 * Gestionnaire de message
	 */
	private MessageManager messageMan;
	
	/**
	 * Information concernant la connection
	 */
	private InfoConnection info;

	public FtpRequest(final Socket socket, final String directory,
			final Map<String, String> bdd) {

		if (socket == null || directory == null || bdd == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		if (bdd.isEmpty()) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		messageMan = new MessageManager(socket);
		runCommand = new FtpCommand(directory, bdd, messageMan);
		
		info = new InfoConnection(bdd, directory, messageMan);
		
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		runCommand.connection();
		processRequest();

	}

	/**
	 * Méthode qui attend des messages du client
	 */
	public void processRequest() {

		while (!info.isFinish()) {

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
					FtpUser.getInstance(info).execute(instruction, parse.nextToken());
				} catch (final NoSuchElementException e) {
					FtpUser.getInstance(info).execute(instruction, "");
				}
			}
		}

	}	

}