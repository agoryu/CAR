package command;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import file.FileManagement;
import ftp.InfoConnection;
import ftp.MessageManager;

public class FtpStor extends FtpCommand {

	private static FtpStor command;
	private static final String STOR = "STOR";

	private FtpStor(final InfoConnection info) {

		super(info);
	}

	@Override
	public void execute(final String name, final String argument) {

		if (name.compareTo(STOR) == 0) {
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

		if (!info.isHavePrivilege()) {
			info.getMessageMan().sendMessage(ERROR_PERMISSION);
			return;
		}

		if (!checkCommand(argument)) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER + NO_PARAMETER);
			return;
		}

		final String ipDownload = info.getIpDownload();
		final int portDownload = info.getPortDownload();

		if (!checkCommand(ipDownload) || portDownload == 0) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		Socket socket = null;
		try {
			socket = new Socket(ipDownload, portDownload);
		} catch (final UnknownHostException e) {
			info.getMessageMan().sendMessage(ERROR_CONNECTION);
		} catch (final IOException e) {
			info.getMessageMan().sendMessage(ERROR_CONNECTION);
		}

		info.getMessageMan().sendMessage(BEGIN_CONNECTION_DATA);

		/* Recevoir le fichier du répertoire local */
		final MessageManager mm = new MessageManager(socket);
		byte[] data = mm.receiveMessageByte();

		/* Ecrire le ficher dans le répertoire distant */
		final FileManagement management = new FileManagement();
		management.writeFile(data, info.getDirectory() + argument);

		if (socket != null) {
			mm.closeConnection();
		}

		info.getMessageMan().sendMessage(END_CONNECTION_DATA);

		info.setIpDownload("");
		info.setPortDownload(0);

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpQuit.getInstance(info).execute(name, argument);

	}

	public static FtpStor getInstance(final InfoConnection info) {
		if (command == null) {
			command = new FtpStor(info);
		}
		return command;
	}

}
