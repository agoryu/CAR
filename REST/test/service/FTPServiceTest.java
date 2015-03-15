package service;

import static org.junit.Assert.assertTrue;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import services.FTPService;
import exceptions.AuthentificationException;
import exceptions.CommandException;
import exceptions.MFileNotFoundException;
import exceptions.NoAutorisationException;

public class FTPServiceTest {
	
	FTPService service = new FTPService();

	@Test
	public void connect() {
		
		FTPClient ftp = service.connect("anonymous", "");
		assertTrue(ftp.isConnected());
		
	}
	
	@Test(expected=AuthentificationException.class)
	public void connectException() {
		service.connect("toto", "");
	}
	
	@Test(expected=AuthentificationException.class)
	public void storConnectionException() {
		service.stor("readme.txt", "toto");
	}
	
	@Test(expected=NoAutorisationException.class)
	public void storAutorisationException() {
		service.stor("readme.txt", "anonymous");
	}
	
	@Test(expected=MFileNotFoundException.class)
	public void storFileException() {
		service.stor(null, "anonymous");
		service.stor("", "anonymous");
	}
	
	@Test(expected=CommandException.class)
	public void retr() {
		service.retr("toto", "anonymous");
		
	}

}
