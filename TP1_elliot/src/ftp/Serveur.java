package ftp;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Serveur {
	
	/* nc -v ftp.univ-lille1.fr 21 */
	/* fin de ligne \r\n */

	/* message d'erreur */
	
	private static final String ERROR_SOCKET_NULL = "Erreur socket null";
	private static final String ERROR_SERVER_NULL = "Erreur serveur socket null";
	private static final String ERROR_CLOSE_SOCKET_CLIENT = "Erreur lors de la fermetur du socket client";
	private static final String ERROR_SOCKET_CLIENT = "Erreur lors de la reception du socket client";
	private static final String ERROR_SERVER_SOCKET = "Erreur lors de l'initialisation du serveur socket";
	private static final String ERROR_ARGUMENT = "Pas assez d'arguemnt pour lancer le serveur";
	
	
	public static final int port = 1024;
	
	public static void main(String[] args) {
		
		/*if(args.length < 1) {
			System.out.println(ERROR_ARGUMENT);
			return;
		}*/
		
		final String directory = " ";//args[1];
		ServerSocket ss = null;
		
		/* creation du socket du serveur */
		if((ss = getServerSocket()) == null) {
			System.err.println(ERROR_SERVER_NULL);
			return;
		}
		Socket socket = null;
		
		while(true){
			
			/* reception du socket client */
			if((socket = getSocket(ss)) == null) {
				System.err.println(ERROR_SOCKET_NULL);
			} else {
				OutputStream os = null;
				try {
					os = socket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				DataOutputStream dos = new DataOutputStream(os);
				try {
					dos.writeBytes("hello world\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			createThread(socket, directory);
			
			try {
				socket.close();
			} catch (final IOException e) {
				System.err.println(ERROR_CLOSE_SOCKET_CLIENT);
			}
	    }
	}

	private static Socket getSocket(final ServerSocket ss) {
		
		Socket socket = null;
		
		try {
			socket = ss.accept();
		} catch (final IOException e) {
			System.err.println(ERROR_SOCKET_CLIENT);
		}
		return socket;
	}

	private static ServerSocket getServerSocket() {
		
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(port);
		} catch (final IOException e) {
			System.err.println(ERROR_SERVER_SOCKET);
		}
		return ss;
	}
	
	private static void createThread(final Socket socket, final String directory) {
		final FtpRequest ft = new FtpRequest(socket, directory);
		final Thread t = new Thread(ft);
		t.start();	
	}

}
