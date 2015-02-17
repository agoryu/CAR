package ftp;

import java.util.Map;

public class InfoConnection {

	/**
	 * Gestionnaire de message
	 */
	protected MessageManager messageMan;

	/**
	 * Base de données contenant les noms et mots de passe des utilisateur
	 */
	protected Map<String, String> bdd;

	/**
	 * Port spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	protected Integer portDownload;

	/**
	 * Adresse spécifié par l'utilisateur pour le telechargement d'un fichier
	 */
	protected String ipDownload;

	/**
	 * Repertoire à la disposition du client
	 */
	protected String directory;

	/**
	 * Premier repertoir visible par le client
	 */
	protected String beginDirectory;

	/**
	 * Login de l'utilisateur
	 */
	protected String login;

	/**
	 * Variable permettant de savoir si le client peut mettre des fichiers sur
	 * le serveur
	 */
	protected boolean havePrivilege;

	/**
	 * Vérifie si l'utilisateur est connecté
	 */
	protected boolean isConnected;
	
	/**
	 * Variable permettant la fermeture de la connection
	 */
	private boolean isFinish;

	public InfoConnection(final Map<String, String> bdd,
			final String directory, MessageManager messageManager) {

		this.messageMan = messageManager;
		this.bdd = bdd;
		portDownload = 0;
		ipDownload = "";
		this.directory = directory;
		this.beginDirectory = directory;
		this.login = "";
		havePrivilege = false;
		isConnected = false;
		isFinish = false;
	}

	public MessageManager getMessageMan() {
		return messageMan;
	}

	public void setMessageMan(MessageManager messageMan) {
		this.messageMan = messageMan;
	}

	public Map<String, String> getBdd() {
		return bdd;
	}

	public void setBdd(Map<String, String> bdd) {
		this.bdd = bdd;
	}

	public Integer getPortDownload() {
		return portDownload;
	}

	public void setPortDownload(Integer portDownload) {
		this.portDownload = portDownload;
	}

	public String getIpDownload() {
		return ipDownload;
	}

	public void setIpDownload(String ipDownload) {
		this.ipDownload = ipDownload;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getBeginDirectory() {
		return beginDirectory;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isHavePrivilege() {
		return havePrivilege;
	}

	public void setHavePrivilege(boolean havePrivilege) {
		this.havePrivilege = havePrivilege;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
}
