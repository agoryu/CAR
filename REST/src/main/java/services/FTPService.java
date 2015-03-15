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
import org.springframework.stereotype.Service;

import exceptions.AuthentificationException;
import exceptions.CommandException;
import exceptions.ConnectionException;
import exceptions.MFileNotFoundException;

@Service
public class FTPService {

	private static final String ERROR_CLOSE_CONNECTION = "Erreur lors de la fermeture de la connection";
	private static final String STOR = "STOR";
	private static final String RETR = "RETR";
	
	private final ConcurrentMap<String, FTPClient> persons = new ConcurrentHashMap<String, FTPClient>();

	private static final String HOST = "localhost";
	private static final int PORT = 21;

	public void disconnect(final FTPClient ftp) {
		try {
			ftp.logout();
		} catch (final IOException e) {
			System.err.println(ERROR_CLOSE_CONNECTION);
		}
	}

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

	public void stor(final String filename, final String login) {

		// TODO anonymous ne peut pas faire de stor
		if (login == null || login == "") {
			throw new ConnectionException();
		}
		
		final FTPClient ftp = connect(login, "");

		if (filename == null || filename == "") {
			throw new MFileNotFoundException();
		}

		try {
			InputStream ips = new FileInputStream(filename);
			ftp.storeFile(filename, ips);
		} catch (final IOException e) {
			throw new CommandException(STOR);
		}
	}
	
	public String retr(final String filename, final String login) {
		
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

}
