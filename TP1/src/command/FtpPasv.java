package command;

import ftp.InfoConnection;

public class FtpPasv extends FtpCommand {

	private static FtpPasv command;
	private static final String PASV = "PASV";

	private FtpPasv() {
	}

	@Override
	public void execute(final String name, final String argument, final InfoConnection info) {

		this.info = info;
		if (name.compareTo(PASV) == 0) {
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

		info.getMessageMan().sendMessage(PASV_MESSAGE);
		info.setIpDownload("127.0.0.1");
		info.setPortDownload(1026);

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpPwd.getInstance().execute(name, argument, info);

	}

	public static FtpPasv getInstance() {
		if (command == null) {
			command = new FtpPasv();
		}
		return command;
	}

}