package command;

import java.io.IOException;
import java.net.Socket;

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

		Socket socket = null;

		try {
			socket = new Socket(ipDownload, portDownload);
		} catch (final IOException e) {
			info.getMessageMan().sendMessage(ERROR_CONNECTION);
			return;
		}

		info.getMessageMan().sendMessage(BEGIN_CONNECTION_DATA);

		final MessageManager mm = new MessageManager(socket);
		mm.sendMessageByte(data);

		if (socket != null) {
			mm.closeConnection();
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
