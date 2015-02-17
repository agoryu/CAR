package command;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ftp.InfoConnection;

public class FtpPort extends FtpCommand {
	
	private static FtpPort command;
	private static final String PORT = "PORT";
	
	private FtpPort(final InfoConnection info) {
		super(info);
	}
	
	@Override
	public void execute(final String name, final String argument) {
		
		if(name.compareTo(PORT) == 0) {
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
		
		if (!checkCommand(argument)) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER + NO_PARAMETER);
			return;
		}

		final Pattern patt = Pattern.compile(REGEX_PORT);
		final Matcher mat = patt.matcher(argument);

		if (!mat.matches()) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER + BAD_FORMAT);
			return;
		}

		final StringTokenizer parseParameter = new StringTokenizer(
				argument, ",");

		String ip = "";
		Integer port = 0;

		for (int i = 0; i < 4; i++) {
			ip += parseParameter.nextToken() + ".";
		}

		ip = ip.substring(0, ip.length() - 1);

		try {
			final Integer multiplicateur = Integer.parseInt(parseParameter
					.nextToken());
			port = 256 * multiplicateur;
			final Integer additionneur = Integer.parseInt(parseParameter
					.nextToken());
			port += additionneur;
		} catch (final NumberFormatException e) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER + NOT_A_NUMBER);
			return;
		}

		if (!checkCommand(ip) || port == 0) {
			info.getMessageMan().sendMessage(ERROR_PARAMETER);
			return;
		}
		info.setIpDownload(ip);
		info.setPortDownload(port);
		info.getMessageMan().sendMessage(PORT_SUCCESSFUL);
		
	}

	@Override
	public void successor(final String name, final String argument) {
		FtpPasv.getInstance(info).execute(name, argument);
		
	}
	
	public static FtpPort getInstance(final InfoConnection info) {
		if (command == null) {
			command = new FtpPort(info);
		}
		return command;
	}

}
