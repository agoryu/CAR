package command;

import ftp.InfoConnection;

public class FtpQuit extends FtpCommand {

	private static FtpQuit command;
	private static final String QUIT = "QUIT";

	private FtpQuit(final InfoConnection info) {
		super(info);
	}

	@Override
	public void execute(final String name, final String argument) {

		if (name.compareTo(QUIT) == 0) {
			this.action(argument);
		} else {
			this.successor(name, argument);
		}

	}

	@Override
	public void action(final String argument) {
		info.getMessageMan().sendMessage(GOODBYE);
		info.getMessageMan().closeConnection();
		info.setFinish(true);
		info.setConnected(false);

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpPort.getInstance(info).execute(name, argument);

	}

	public static FtpQuit getInstance(final InfoConnection info) {
		if (command == null) {
			command = new FtpQuit(info);
		}
		return command;
	}

}
