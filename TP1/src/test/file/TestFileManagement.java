package test.file;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import file.FileManagement;

public class TestFileManagement {

	@Test
	public void testReadFile() {
		FileManagement fm = new FileManagement();
		byte[] data = fm.readFile("testClient.txt");
		assertEquals("test lecture de fichier", new String(data), "bonjour le serveur");
		
		byte[] data2 = fm.readFile("testCli.txt");
		assertEquals("test lecture de fichier inexistant", data2, null);
		
		byte[] data3 = fm.readFile(null);
		assertEquals("test lecture de fichier inexistant", data3, null);
	}
	
	@Test
	public void testWriteFile() {
		FileManagement fm = new FileManagement();
		String test = "salut toto";
		int size = test.length();
		byte[] data = new byte[size];
		
		boolean result = false;
		try {
			result = fm.writeFile(test.getBytes("UTF-8"), "dossier_test/test2.txt");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertTrue("test ecriture de fichier fonctionnel", result);
		
		result = fm.writeFile(data, null);
		assertFalse("test ecriture de fichier avec parametre manquant", result);
		
		result = fm.writeFile(data, "");
		assertFalse("test ecriture de fichier avec nom fichier vide", result);
	}

}
