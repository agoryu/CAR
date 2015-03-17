package services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import exceptions.AuthentificationException;
import exceptions.CommandException;
import exceptions.ConnectionException;
import exceptions.MFileNotFoundException;
import exceptions.NoAutorisationException;

/**
 * Service de la plateforme REST
 * 
 * @author elliot et salsabile
 *
 */
@Service
public class FTPService {

	private static final String STOR = "STOR";
	private static final String RETR = "RETR";
	private static final String PARENT_LINK = "<a href=\"http://localhost:8080/rest/api/dir/p\"/>..</a></br>";
	private static final String HERE = "here";
	private static final String STOR_FORM = "</br><a href=\"http://localhost:8080/rest/api/formStor\"> Add </a>";
	private static final String FILE_PATH_LINK = "<a href=\"http://localhost:8080/rest/api/file/";
	private static final String DIR_PATH_LINK = "<a href=\"http://localhost:8080/rest/api/dir/";

	/**
	 * Map des connexions au serveur ftp
	 */
	private final ConcurrentMap<String, FTPClient> persons = new ConcurrentHashMap<String, FTPClient>();

	private static final String HOST = "localhost";
	private static final int PORT = 21;

	/**
	 * Méthode qui gère les connexion au serveur ftp
	 * 
	 * @param login
	 *            Login de l'utilisateur
	 * @param password
	 *            Mot de passe de l'utilisateur. Si l'utilisateur est déjà
	 *            authentifié alors on met une chaine vide.
	 * @return Le client connecté au serveur ftp
	 */
	public FTPClient connect(final String login, final String password) {

		if (persons.containsKey(login)) {
			return persons.get(login);
		}

		final FTPClient ftp = new FTPClient();

		try {
			ftp.connect(HOST, PORT);
		} catch (final SocketException e1) {
			throw new ConnectionException();
		} catch (final IOException e1) {
			throw new ConnectionException();
		}

		try {
			if (!ftp.login(login, password)) {
				throw new AuthentificationException(ftp);
			}
		} catch (final IOException e1) {
			throw new AuthentificationException(ftp);
		}

		ftp.enterLocalPassiveMode();
		persons.put(login, ftp);
		return ftp;
	}

	/**
	 * Charge un fichier sur le serveur
	 * 
	 * @param filename
	 *            Nom du fichier à mettre sur le serveur
	 * @param login
	 *            Nom de l'utilisateur qui utilise l'action
	 */
	public void stor(final String filename, final String login) {
		
		if (login == null || login == "") {
			throw new ConnectionException();
		}
		
		if (filename == null || filename == "") {
			throw new MFileNotFoundException();
		}
		
		if("anonymous".equals(login)) {
			throw new NoAutorisationException();
		}

		final FTPClient ftp = connect(login, "");

		try {
			InputStream ips = new FileInputStream(filename);
			ftp.storeFile(filename, ips);
		} catch (final IOException e) {
			throw new CommandException(STOR);
		}
	}

	/**
	 * Récupere un fichier sur le serveur
	 * 
	 * @param filename
	 *            Nom du fichier à télécharger
	 * @param login
	 *            Nom de l'utilisateur qui utilise l'action
	 * @return Le contenu du fichier
	 */
	public String retr(final String filename, final String login) {

		if (login == null || login == "") {
			throw new ConnectionException();
		}
		
		if (filename == null || filename == "") {
			throw new MFileNotFoundException();
		}
		
		FTPClient ftp = connect(login, "");

		String response = "";

		InputStream in = null;
		try {
			in = ftp.retrieveFileStream(filename);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader buff = new BufferedReader(isr);

			String tmp;
			while ((tmp = buff.readLine()) != null) {
				response += tmp;
			}

		} catch (final IOException e2) {
			throw new CommandException(RETR);
		}

		return response;
	}

	/**
	 * Affiche le contenu d'un dossier
	 * 
	 * @param dir
	 *            Dossier à afficher
	 * @param login
	 *            Nom de l'utilisateur qui utilise l'action
	 * @return Le contenu du dossier
	 */
	public String list(final String dir, final String login) {
		FTPClient ftp = connect(login, "");

		FTPFile[] result = null;

		try {
			System.out.println(dir);
			if ("p".equals(dir)) {
				final String directory = ftp.printWorkingDirectory() + "/..";
				ftp.changeWorkingDirectory(directory);
			}

			if (!HERE.equals(dir)) {
				final String directory = ftp.printWorkingDirectory() + "/"
						+ dir;
				ftp.changeWorkingDirectory(directory);
			}

			result = ftp.listFiles();
		} catch (final IOException e) {
			throw new MFileNotFoundException();
		}

		StringBuilder response = new StringBuilder();

		if (result.length != 0) {
			response.append(PARENT_LINK);
			for (final FTPFile file : result) {
				final String name = file.getName();
				if (file.isDirectory()) {
					response.append(DIR_PATH_LINK + login + "/" + name + "\">D " + name
							+ "</a></br>");
				} else {
					response.append(FILE_PATH_LINK + login + "/" + name + "\">F " + name
							+ "</a></br>");
				}
			}
		}

		response.append(STOR_FORM);

		return new String(response);
	}

}
