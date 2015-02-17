package command;

import ftp.InfoConnection;

public class FtpUser extends FtpCommand {

	private static final String USER = "USER";
	private static FtpUser command;

	private FtpUser() {
	}

	@Override
	public void execute(final String name, final String argument, final InfoConnection info) {

		this.info = info;
		if (name.compareTo(USER) == 0) {
			this.action(argument);
		} else {
			this.successor(name, argument);
		}

	}

	@Override
	public void action(final String argument) {

		if (info.isConnected())
			return;

		if (!checkCommand(argument)) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		if (info.getBdd().containsKey(argument)) {
			info.getMessageMan().sendMessage(SPECIFY_MDP);
			info.setLogin(argument);
		} else {
			info.getMessageMan().sendMessage(ERROR_IDENTIFICATION);
			return;
		}

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpPass.getInstance().execute(name, argument, info);

	}

	public static FtpCommand getInstance() {

		if (command == null) {
			command = new FtpUser();
		}
		return command;
	}

}
