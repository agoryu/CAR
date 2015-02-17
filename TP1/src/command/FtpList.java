package command;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ftp.InfoConnection;
import ftp.MessageManager;

public class FtpList extends FtpCommand {

	private static final String LIST = "LIST";
	private static FtpList command;

	private FtpList() {

	}

	@Override
	public void execute(final String name, final String argument, final InfoConnection info) {

		this.info = info;
		if (name.compareTo(LIST) == 0) {
			this.action(argument);
		} else {
			this.successor(name, argument);
		}

	}

	@Override
	public void action(final String argument) {
		
		if(!info.isConnected()) {
			info.getMessageMan().sendMessage(ERROR_NOT_CONNECTED);
			return;
		}
		
		if (!checkCommand(info.getIpDownload()) || info.getPortDownload() == 0) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		/* Verifier que le repertoire courant n'est pas null */
		if (!checkCommand(info.getDirectory())) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		String[] fileList = null;
		String result = BEGIN_LIST + END_LINE;

		info.getMessageMan().sendMessage(BEGIN_CONNECTION_DATA);

		Socket socket = null;
		try {
			socket = new Socket(info.getIpDownload(), info.getPortDownload());
		} catch (final UnknownHostException e) {
			info.getMessageMan().sendMessage(ERROR_CONNECTION);
		} catch (final IOException e) {
			info.getMessageMan().sendMessage(ERROR_CONNECTION);
		}

		try {
			fileList = new File(info.getDirectory()).list();
			final int size = fileList.length;

			for (int i = 0; i < size; i++) {
				result += fileList[i] + END_LINE;
			}

		} catch (final NullPointerException e) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
		}

		final MessageManager mm = new MessageManager(socket);
		mm.sendMessageByte(result.getBytes());

		info.getMessageMan().sendMessage(END_CONNECTION_DATA);

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				mm.closeConnection();
			}
		}

		info.setIpDownload("");
		info.setPortDownload(0);

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpRetr.getInstance()
				.execute(name, argument, info);

	}

	public static FtpList getInstance() {

		if (command == null) {
			command = new FtpList();
		}
		return command;
	}

}
