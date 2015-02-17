package command;

import ftp.InfoConnection;

public class FtpQuit extends FtpCommand {

	private static FtpQuit command;
	private static final String QUIT = "QUIT";

	private FtpQuit() {
	}

	@Override
	public void execute(final String name, final String argument, final InfoConnection info) {

		this.info = info;
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
		FtpPort.getInstance().execute(name, argument,info);

	}

	public static FtpQuit getInstance() {
		if (command == null) {
			command = new FtpQuit();
		}
		return command;
	}

}
