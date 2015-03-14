package services;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import exceptions.AuthentificationException;
import exceptions.ConnectionException;

@Service
public class FTPService {
	
	private static final String ERROR_CLOSE_CONNECTION = "Erreur lors de la fermeture de la connection";
	private final ConcurrentMap< String, Socket > persons = new ConcurrentHashMap< String, Socket >(); 
	
	private static final String HOST = "localhost";
	private static final int PORT = 21;
	
	public void disconnect(final FTPClient ftp) {
		try {
			ftp.logout();
		} catch (final IOException e) {
			System.err.println(ERROR_CLOSE_CONNECTION);
		}
	}
	
	public void connect(final String login, final String password) {
		
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
	}
	
}
