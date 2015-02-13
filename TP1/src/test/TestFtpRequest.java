package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class TestFtpRequest {

	private static final String END_LINE = "\r\n";
	private static final String WELCOME = "220 Service ready for new user.";
	private static final String GOODBYE = "221 Goodbye.";
	private static final String LOGIN_OK = "230 Login successful.";
	private static final String SPECIFY_MDP = "331 Please specify the password.";
	private static final String PORT_SUCCESSFUL = "200 PORT command successful.";
	private static final String BEGIN_TRANSFERT = "150 Opening ASCII mode data connection for file list.";
	private static final String END_TRANSFERT = "226 Transfer complete.";
	private static final String ERROR_NO_COMMAND = "202 Command not implemented, superfluous at this site.";
	private static final String ERROR_PARAMETER = "501 Syntax error in parameters or arguments";
	private static final String ERROR_IDENTIFICATION = "430 Invalid username or password.";
	private static final String BEGIN_CONNECTION_DATA = "150 open data connection";
	private static final String END_CONNECTION_DATA = "226 Requested file action successful";
	private static final String PASV_MESSAGE = "227 127,0,0,1,4,2";
	
	private static final String BAD_FORMAT = " : bad format -> num,num,num,num,num,num";
	private static final String NO_PARAMETER = " : no parameter";

	@Test
	public void TestConnection() throws UnknownHostException, IOException {

		String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));

		answer = reader.readLine();
		assertEquals("connexion au serveur", answer, WELCOME);
	}

	@Test
	public void TestAuthentification() throws UnknownHostException, IOException {

		String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		DataOutputStream writer = new DataOutputStream(s.getOutputStream());
		answer = reader.readLine();// le welcome

		// //////////////// USER ///////////////////////////////
		writer.writeBytes("USER toto" + END_LINE);
		answer = reader.readLine();
		assertEquals("test du user non reconnu", answer, ERROR_IDENTIFICATION);
		writer.writeBytes("USER elliot" + END_LINE);
		answer = reader.readLine();
		assertEquals("test du user elliot", answer, SPECIFY_MDP);

		// ///////////////// PASS //////////////////////////////
		writer.writeBytes("PASS toto" + END_LINE);
		answer = reader.readLine();
		assertEquals("test du pass elliot erroné", answer, ERROR_IDENTIFICATION);
		writer.writeBytes("PASS link" + END_LINE);
		answer = reader.readLine();
		assertEquals("test du pass elliot correcte", answer, LOGIN_OK);

	}

	@Test
	public void TestDeconnexion() throws UnknownHostException, IOException {

		String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		DataOutputStream writer = new DataOutputStream(s.getOutputStream());
		answer = reader.readLine();// le welcome

		writer.writeBytes("QUIT" + END_LINE);
		answer = reader.readLine();
		assertEquals("test de la réponse de fin d'envoie de donné", answer,
				GOODBYE);
	}

	/*@Test
	public void TestPORT() throws UnknownHostException, IOException {
		String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		DataOutputStream writer = new DataOutputStream(s.getOutputStream());
		answer = reader.readLine();// le welcome

		writer.writeBytes("USER elliot" + END_LINE);
		answer = reader.readLine();
		writer.writeBytes("PASS link" + END_LINE);
		answer = reader.readLine();

		writer.writeBytes("PORT 127,0,0,1," + END_LINE);
		answer = reader.readLine();
		assertEquals("test de la commande port: mauvais format", answer, ERROR_PARAMETER + BAD_FORMAT);
		
		writer.writeBytes("PORT" + END_LINE);
		answer = reader.readLine();
		assertEquals("test de la commande port : pas de parametre", answer, ERROR_PARAMETER + NO_PARAMETER);

		writer.writeBytes("PORT 127,0,0,1,4,1" + END_LINE);
		answer = reader.readLine();
		assertEquals("test de la commande port", answer, PORT_SUCCESSFUL);
	}

	@Test
	public void TestRETR() throws UnknownHostException, IOException {

		String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		DataOutputStream writer = new DataOutputStream(s.getOutputStream());
		answer = reader.readLine();// le welcome

		writer.writeBytes("USER elliot" + END_LINE);
		answer = reader.readLine();
		writer.writeBytes("PASS link" + END_LINE);
		answer = reader.readLine();
		writer.writeBytes("PORT 127,0,0,1,4,1" + END_LINE);
		answer = reader.readLine();

		ServerSocket ss = new ServerSocket(1025);
		Socket s2 = null;
		writer.writeBytes("RETR test.txt" + END_LINE);
		answer = reader.readLine();
		assertEquals("test debut connection retr", answer, BEGIN_CONNECTION_DATA);
		s2 = ss.accept();

		DataInputStream input = new DataInputStream(s2.getInputStream());
		byte[] data = new byte[input.available()];
		input.read(data);
		
		answer = new String(data);
		answer = answer.substring(0, answer.length()-1);
		assertEquals("test de la commande retr", answer, "hello world");

		answer = reader.readLine();
		assertEquals("test fin connection retr", answer, END_CONNECTION_DATA);
		
		if (s2 != null)
			s2.close();
		ss.close();

	}
	
	@Test
	public void TestSTOR() throws UnknownHostException, IOException {

		/*String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		DataOutputStream writer = new DataOutputStream(s.getOutputStream());
		answer = reader.readLine();// le welcome

		writer.writeBytes("USER elliot" + END_LINE);
		answer = reader.readLine();
		writer.writeBytes("PASS link" + END_LINE);
		answer = reader.readLine();
		writer.writeBytes("PORT 127,0,0,1,4,1" + END_LINE);
		answer = reader.readLine();

		Socket s2 = new Socket("127.0.0.1", 1025);
		writer.writeBytes("STOR Readme.txt" + END_LINE);

		DataInputStream input = new DataInputStream(s2.getInputStream());
		byte[] data = new byte[input.available()];
		input.read(data);
		
		answer = new String(data);
		answer = answer.substring(0, answer.length()-1);
		assertEquals("test de la commande retr", answer, "bonjour le client");

		if (s2 != null)
			s2.close();

	}*/
	
	@Test
	public void TestLIST() throws UnknownHostException, IOException {
		
	}
	
	@Test
	public void TestPASV() throws UnknownHostException, IOException {
		/*String answer;
		Socket s = new Socket("localhost", 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		DataOutputStream writer = new DataOutputStream(s.getOutputStream());
		answer = reader.readLine();// le welcome

		writer.writeBytes("USER salsabile" + END_LINE);
		answer = reader.readLine();
		writer.writeBytes("PASS ok" + END_LINE);
		answer = reader.readLine();

		writer.writeBytes("PASV" + END_LINE);
		answer = reader.readLine();
		assertEquals("test de la commande port: mauvais format", answer, PASV_MESSAGE);*/
	}

}
