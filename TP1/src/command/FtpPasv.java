package command;

import ftp.InfoConnection;

public class FtpPasv extends FtpCommand {

	private static FtpPasv command;
	private static final String PASV = "PASV";

	private FtpPasv(final InfoConnection info) {
		super(info);
	}

	@Override
	public void execute(final String name, final String argument) {

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
		FtpPwd.getInstance(info).execute(name, argument);

	}

	public static FtpPasv getInstance(final InfoConnection info) {
		if (command == null) {
			command = new FtpPasv(info);
		}
		return command;
	}

}