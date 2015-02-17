package command;

import java.io.File;
import java.util.StringTokenizer;

import ftp.InfoConnection;

public class FtpCwd extends FtpCommand {

	private static FtpCwd command;
	private static final String CWD = "CWD";

	private FtpCwd(final InfoConnection info) {
		super(info);
	}

	@Override
	public void execute(final String name, final String argument) {

		if (name.compareTo(CWD) == 0) {
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
		
		/* Verifier que le repertoire courant n'est pas null */
		if (!checkCommand(argument)) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}

		if (argument.compareTo(info.getBeginDirectory()) == 0) {
			info.setDirectory(argument);
			info.getMessageMan().sendMessage(CHANGE_DIRECTORY);
			return;
		}

		final File fNewFile = new File(info.getDirectory() + argument);
		final File fBeginFile = new File(info.getBeginDirectory());

		if (fNewFile.isDirectory()) {
			final StringTokenizer newDirectoryTocken = new StringTokenizer(
					fNewFile.getAbsolutePath(), "/");
			final StringTokenizer firstDirectory = new StringTokenizer(
					fBeginFile.getAbsolutePath(), "/");

			/*
			 * si le nouveau directory est plus proche de la racine que le
			 * directory de depart alors c'est une erreur de l'utilisateur
			 */
			if (newDirectoryTocken.countTokens() >= firstDirectory
					.countTokens()) {
				info.setDirectory(info.getDirectory() + argument);
				info.getMessageMan().sendMessage(CHANGE_DIRECTORY);
			} else {
				info.getMessageMan().sendMessage(ERROR_DIRECTORY_PATH);
			}
		} else {
			info.getMessageMan().sendMessage(ERROR_DIRECTORY_PATH + NOT_DIRECTORY);
		}

	}

	@Override
	public void successor(final String name, final String argument) {
		FtpCdup.getInstance(info)
				.execute(name, argument);

	}

	public static FtpCwd getInstance(final InfoConnection info) {
		if (command == null) {
			command = new FtpCwd(info);
		}
		return command;
	}

}