package ftp;

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
	private static final String WELCOME = "220 Service ready for new user.";
	private static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";

	/**
	 * Information concernant la connection
	 */
	private InfoConnection info;

	/**
	 * Enregistre les informaiton de connexion
	 * 
	 * @param info
	 */
	public FtpRequest(final InfoConnection info) {

		if (info == null) {
			throw new NullPointerException(ERROR_ARGUMENT);
		}

		this.info = info;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		info.getMessageMan().sendMessage(WELCOME);
		processRequest();

	}

	/**
	 * Méthode qui attend des messages du client
	 */
	public void processRequest() {

		while (!info.isFinish()) {

			/* gestion de la requete par un thread */
			String commande = null;
			commande = info.getMessageMan().receiveMessage();

			if (commande == null) {
				continue;
			}
			if (commande != "") {
				// Recupérer le nom de la commande
				final StringTokenizer parse = new StringTokenizer(commande, " ");
				final String instruction = parse.nextToken();

				if (instruction == null) {
					continue;
				}

				if (instruction.compareTo("") == 0) {
					info.getMessageMan().sendMessage(ERROR_NO_COMMAND);
					continue;
				}

				/* execution de la commande */
				try {
					FtpUser.getInstance().execute(instruction,
							parse.nextToken(), info);
				} catch (final NoSuchElementException e) {
					FtpUser.getInstance().execute(instruction, "", info);
				}
			}
		}

	}

}