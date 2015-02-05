package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

public class ServeurTest {
	
	private static final String END_LINE = "\r\n";
	private static final String SPECIFY_MDP = "331 Please specify the password." + END_LINE;

	@Test
	public void test() throws IOException {

		Socket socket = new Socket("localhost", 1024);
		
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		OutputStream os = socket.getOutputStream();
		DataOutputStream writer = new DataOutputStream(os);
		
		writer.writeBytes("USER anonymous");
		
		assertEquals(reader.readLine(), SPECIFY_MDP);

		fail("Not yet implemented");
	}

}
