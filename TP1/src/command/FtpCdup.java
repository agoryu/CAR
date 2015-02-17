package command;

import java.io.File;

import ftp.InfoConnection;

public class FtpCdup extends FtpCommand {
	
	private static FtpCdup command;
	private static final String CDUP = "CDUP";
	
	private FtpCdup(final InfoConnection info) {
		super(info);
	}
	
	@Override
	public void execute(final String name, final String argument) {
		
		if(name.compareTo(CDUP) == 0) {
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
		
		final String directory = info.getDirectory();
		final String beginDirectory = info.getBeginDirectory();
		
		/* Verifier que le repertoire courant n'est pas null */
		if (!checkCommand(directory)) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		File file1 = new File(directory);
		File file2 = new File(beginDirectory);

		if (file1.getAbsolutePath().compareTo(file2.getAbsolutePath()) == 0) {
			info.getMessageMan().sendMessage(ERROR_DIRECTORY_PATH);
		} else {
			File parentDirectory = file1.getParentFile();
			info.setDirectory(parentDirectory.getName());
			info.getMessageMan().sendMessage(CHANGE_DIRECTORY);
		}
		
	}

	@Override
	public void successor(final String name, final String argument) {
		info.getMessageMan().sendMessage(ERROR_NO_COMMAND);
	}
	
	public static FtpCdup getInstance(final InfoConnection info) {
		if (command == null) {
			command = new FtpCdup(info);
		}
		return command;
	}

}
