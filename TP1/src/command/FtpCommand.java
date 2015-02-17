package command;

import ftp.InfoConnection;

public abstract class FtpCommand {

	protected static final String NOT_DIRECTORY = " not directory";
	protected static final String REGEX_PORT = "\\d{1,3}(,\\d{1,3}){5}";
	protected static final String FORGOT_PORT = " : you forgot the command PORT or PASV";
	protected static final String NOT_A_NUMBER = " : parameter is not a number";
	protected static final String BAD_FORMAT = " : bad format -> num,num,num,num,num,num";
	protected static final String NO_PARAMETER = " : no parameter";
	protected static final String ERROR_ARGUMENT = "Erreur dans les arguments de ftpRequest";
	protected static final String END_LINE = "\r\n";

	protected static final String ANONYMOUS = "anonymous";
	protected static final String GOODBYE = "221 Goodbye.";
	protected static final String LOGIN_OK = "230 Login successful.";
	protected static final String WELCOME = "220 Service ready for new user.";
	protected static final String ERROR_PARAMETER = "501 Syntax error in parameters or arguments";
	protected static final String SPECIFY_MDP = "331 Please specify the password.";
	protected static final String ERROR_IDENTIFICATION = "430 Invalid username or password.";
	protected static final String PORT_SUCCESSFUL = "200 PORT command successful.";
	protected static final String ERROR_PERMISSION = "550 Permission denied.";
	protected static final String PWD_MSG = "257 ";
	protected static final String PASV_MESSAGE = "227 127,0,0,1,4,2";
	protected static final String END_CONNECTION_DATA = "226 Requested file action successful";
	protected static final String BEGIN_CONNECTION_DATA = "150 open data connection";
	protected static final String ERROR_CONNECTION = "425 Can't open data connection.";
	protected static final String BEGIN_LIST = "212- ";
	protected static final String CHANGE_DIRECTORY = "250 Directory successfully changed.";
	protected static final String ERROR_DIRECTORY_PATH = "450 Requested file action not taken.";
	protected static final String ERROR_NOT_CONNECTED = "530 Not logged in.";
	protected static final String ERROR_NO_COMMAND = "202 Command not implemented, superfluous at this site.";

	protected InfoConnection info;
	
	/**
	 * Vérifie qu'une chaine de caractère n'est pas null ou vide
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

	/**
	 * Vérifie si la commande est bien la commande name
	 * 
	 * @param name
	 *            Nom de la commande
	 * @param argument
	 *            Arguments de la commande
	 */
	public abstract void execute(final String name, final String argument, final InfoConnection info);

	/**
	 * Execution de la commande
	 * 
	 * @param argument
	 *            Arguments de la commande
	 */
	protected abstract void action(final String argument);

	/**
	 * Si la commande demandé n'est pas la bonne on passe à la commande suivante
	 * 
	 * @param name
	 *            Nom de la commande
	 * @param argument
	 *            Arguments de la commande
	 */
	protected abstract void successor(final String name, final String argument);

}
