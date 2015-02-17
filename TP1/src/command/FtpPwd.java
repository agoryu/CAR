package command;

import ftp.InfoConnection;

public class FtpPwd extends FtpCommand {

	private static FtpPwd command;
	private static final String PWD = "PWD";

	private FtpPwd() {
	}

	@Override
	public void execute(final String name, final String argument, final InfoConnection info) {

		this.info = info;
		if (name.compareTo(PWD) == 0) {
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

		info.getMessageMan().sendMessage(PWD_MSG + info.getDirectory());

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpCwd.getInstance().execute(name, argument, info);

	}

	public static FtpPwd getInstance() {
		if (command == null) {
			command = new FtpPwd();
		}
		return command;
	}

}