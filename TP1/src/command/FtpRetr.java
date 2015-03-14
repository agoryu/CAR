package command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import file.FileManagement;
import ftp.InfoConnection;
import ftp.MessageManager;

public class FtpRetr extends FtpCommand {

	private static FtpRetr command;
	private static final String RETR = "RETR";

	private FtpRetr() {

	}

	@Override
	public void execute(final String name, final String argument, final InfoConnection info) {
		
		this.info = info;
		if (name.compareTo(RETR) == 0) {
			this.action(argument);
		} else {
			this.successor(name, argument);
		}

	}

	@Override
	public void action(final String argument) {
		
		if (!info.isConnected()) {
			info.getMessageMan().sendMessage(ERROR_NOT_CONNECTED);
			return;
		}
		
		if (!checkCommand(argument)) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER + NO_PARAMETER);
			return;
		}
		
		final String ipDownload = info.getIpDownload();
		final int portDownload = info.getPortDownload();

		if (!checkCommand(ipDownload) || portDownload == 0) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER + FORGOT_PORT);
			return;
		}

		final FileManagement management = new FileManagement();
		final byte[] data = management.readFile(info.getDirectory() + argument);

		/*Socket socket = null;
		
		try {
			socket = new Socket(ipDownload, portDownload);
			System.out.println("sloupy 2");
		} catch (final IOException e) {
			System.out.println("sloupy");
			info.getMessageMan().sendMessage(ERROR_CONNECTION);
			return;
		}*/

		info.getMessageMan().sendMessage(BEGIN_CONNECTION_DATA);

		/*final MessageManager mm = new MessageManager(socket);
		mm.sendMessageByte(data);

		if (socket != null) {
			mm.closeConnection();
		}*/
		DatagramPacket dp = null;
		DatagramSocket ds = null;
		
		try {
			dp = new DatagramPacket(data, data.length, InetAddress.getByAddress(ipDownload.getBytes()), portDownload);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ds = new DatagramSocket(portDownload);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ds.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		info.getMessageMan().sendMessage(END_CONNECTION_DATA);

		info.setIpDownload("");
		info.setPortDownload(0);

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpStor.getInstance()
				.execute(name, argument, info);
	}

	public static FtpRetr getInstance() {
		if (command == null) {
			command = new FtpRetr();
		}
		return command;
	}

}
