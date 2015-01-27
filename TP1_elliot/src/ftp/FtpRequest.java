package ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class FtpRequest implements Runnable {
	
	private static final String ERROR_MSG_EMPTY = "Erreur de lecture, la chaine est vide";
	private static final String ERROR_MSG_NULL = "La commande est null";
	private static final String ERROR_INPUT_NULL = "Erreur Input null";
	private static final String ERROR_READ = "Erreur lors de la lectur";
	private static final String ERROR_INPUT = "Erreur sur la creation de l'InputStream";

	private Socket socket;
	//private String direcory;
	
	public FtpRequest(final Socket socket, final String directory) {
		this.socket = socket;
		//this.direcory = directory;
	}
	
	@Override
	public void run() {
		
		processRequest();

	}

	public void processRequest() {

		while (true) {
			//flux entrant
			InputStream in = null;
			if((in = getInput(socket)) == null) {
				System.err.println(ERROR_INPUT_NULL);
			}
			final BufferedReader ibr = new BufferedReader(new InputStreamReader(in));
			
			/* gestion de la requete par un thread */
			String commande = null;
			commande = getCommande(ibr);
			
			if(checkCommande(commande)) {
				final StringTokenizer parse = new StringTokenizer(commande, " ");
				final String instruction = parse.nextToken();
				
				if(instruction == null) {
					//TODO message d'erreur
					continue;
				}
				
				if(instruction.compareTo("") == 0) {
					//TODO message d'erreur
					continue;
				}
				
				lanceCommande(instruction, parse.nextToken());
			}
		}
		
	}

	public void processUSER() {

	}

	public void processPASS() {

	}

	public void processRETR() {

	}

	public void processSTOR() {

	}

	public void processLIST() {

	}

	public void processQUIT() {

	}
	
	private boolean checkCommande(final String commande) {
		
		if(commande == null) {
			System.err.println(ERROR_MSG_NULL);
			return false;
		}
		
		if(commande.compareTo("") == 0) {
			System.err.println(ERROR_MSG_EMPTY);
			return false;
		}
		
		return true;
	}
	
	private String getCommande(final BufferedReader ibr) {
		
		String commande = null;
		
		try {
			commande = ibr.readLine();
		} catch (final IOException e1) {
			System.err.println(ERROR_READ);
		}
		return commande;
	}
	
	private InputStream getInput(final Socket socket) {
		
		InputStream in = null;
		try {
			in = socket.getInputStream();
		}catch(final IOException e) {
			System.err.println(ERROR_INPUT);
		}
		return in;
	}
	
	public void lanceCommande(final String instruction, final String parametre) {
		
		
	}

}