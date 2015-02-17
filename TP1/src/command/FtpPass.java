package command;

import ftp.InfoConnection;

public class FtpPass extends FtpCommand {

	private static final String PASS = "PASS";
	private static FtpPass command;

	private FtpPass(final InfoConnection info) {
		super(info);
	}

	@Override
	public void execute(final String name, final String argument) {

		if (name.compareTo(PASS) == 0) {
			this.action(argument);
		} else {
			this.successor(name, argument);
		}

	}

	@Override
	public void action(final String argument) {
		
		if(info.isConnected())
			return;
		
		if (argument == null) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		final String login = info.getLogin();
		
		if (!checkCommand(login)) {
			info.getMessageMan().sendMessage(ERROR_IDENTIFICATION);
			return;
		}

		if (login.compareTo(ANONYMOUS) == 0) {
			info.getMessageMan().sendMessage(LOGIN_OK);
			info.setConnected(true);
			return;
		}

		if (info.getBdd().get(login).compareTo(argument) == 0) {
			info.getMessageMan().sendMessage(LOGIN_OK);
			info.setHavePrivilege(false);
			info.setConnected(true);
			return;
		} else {
			info.getMessageMan().sendMessage(ERROR_IDENTIFICATION);
			return;
		}

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpList.getInstance(info).execute(name, argument);
	}

	public static FtpPass getInstance(final InfoConnection info) {

		if (command == null) {
			command = new FtpPass(info);
		}
		return command;
	}

}
