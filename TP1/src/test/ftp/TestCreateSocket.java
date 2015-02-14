package test.ftp;

import static org.junit.Assert.*;

import java.net.ServerSocket;

import org.junit.Test;

import ftp.CreateSocket;

public class TestCreateSocket {

	@Test
	public void testGetSocket() {
		CreateSocket cs = new CreateSocket();
		ServerSocket ss = cs.getServerSocket(1024);

		assertNotEquals("test creation serverSocket", ss, null);

		ss = cs.getServerSocket(21);
		assertEquals("test creation serverSocket", ss, null);

	}

	@Test
	public void testCloseSocket() {

		CreateSocket cs = new CreateSocket();
		assertFalse("erreur fermeture socket", cs.closeServerSocket(null));
		
		ServerSocket ss = cs.getServerSocket(1024);
		assertTrue("erreur fermeture socket", cs.closeServerSocket(ss));
	}
}
