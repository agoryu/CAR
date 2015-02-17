package file;

import java.io.*;

/**
 * Classe permettant de lire et écrire dans des fichiers
 * 
 * @author elliot et salsabile
 *
 */
public class FileManagement {

	private static final String ERROR_NOT_POSSIBLE_TO_WRITE = "Erreur impossible de lire dans le fichier";
	private static final String ERROR_NO_FILE = "Erreur le fichier est inexistant.";
	private static final String ERROR_PARAMETER_READ = "Erreur l'argument est vide dans la méthode de lecture de fichier";
	private static final String ERROR_PARAMETER_WRITE = "Erreur l'un des paramètres de la méthode writeFile est vide";

	/**
	 * Ecriture de donnée dans un fichier
	 * 
	 * @param data
	 *            Données à écrire
	 * @param fichier
	 *            Nom fichier
	 * 
	 * @return Vrai si l'opération s'est bien passé, faux sinon
	 */
	public boolean writeFile(final byte[] data, final String fichier) {

		FileOutputStream fos;
		ObjectOutputStream out = null;
		boolean result = false;

		if (data == null || fichier == null) {
			System.err.println(ERROR_PARAMETER_WRITE);
			return false;
		}

		if (fichier.compareTo("") == 0) {
			System.err.println(ERROR_PARAMETER_WRITE);
			return false;
		}

		try {
			fos = new FileOutputStream(fichier);
			out = new ObjectOutputStream(fos);
			out.write(data);
			result = true;
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			result = false;
		} catch (final IOException e) {
			e.printStackTrace();
			result = false;
		}

		finally {
			if (out != null) {
				try {
					out.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;

	}

	/**
	 * Lecture de données dans un fichier locale
	 * 
	 * @param f
	 *            nom du fichier
	 * @return Les données lu dans le fichier
	 */
	public byte[] readFile(final String f) {

		FileInputStream fin;
		DataInputStream input = null;
		byte[] data = null;

		if (f == null) {
			System.err.println(ERROR_PARAMETER_READ);
			return null;
		}

		try {
			fin = new FileInputStream(f);
			input = new DataInputStream(fin);
			final int size = input.available();
			data = new byte[size];
			input.read(data);
		} catch (final FileNotFoundException e) {
			System.err.println(ERROR_NO_FILE);
		} catch (final IOException e) {
			System.err.println(ERROR_NOT_POSSIBLE_TO_WRITE);
		}

		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}
}