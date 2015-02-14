package file;

import java.io.*;

/**
 * @author agoryu
 *
 */
public class FileManagement {

	private static final String ERROR_PARAMETER_READ = "Erreur l'argument est vide dans la méthode de lecture de fichier";
	private static final String ERROR_PARAMETER_WRITE = "Erreur l'un des paramètres de la méthode writeFile est vide";

	/**
	 * Ecriture de donnée dans un fichier 
	 * 
	 * @param o
	 *            Données à écrire
	 * @param fichier
	 *            Nom fichier
	 */
	public boolean writeFile(byte[] o, String fichier) {
		
		FileOutputStream fos;
		ObjectOutputStream out = null;
		boolean result = false;
		
		if(o == null || fichier == null) {
			System.err.println(ERROR_PARAMETER_WRITE);
			return false;
		}
		
		if(fichier.compareTo("") == 0) {
			System.err.println(ERROR_PARAMETER_WRITE);
			return false;
		}

		try {
			fos = new FileOutputStream(fichier);
			out = new ObjectOutputStream(fos);
			out.write(o);
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
	 * @return
	 */
	public byte[] readFile(String f) {
		
		FileInputStream fin;
		DataInputStream input = null;
		byte[] data = null;
		if(f == null) {
			System.err.println(ERROR_PARAMETER_READ);
			return null;
		}
		
		try {
			fin = new FileInputStream(f);
			input = new DataInputStream(fin);
			final int size = input.available();
			data = new byte[size];
			input.read(data);
		} catch (FileNotFoundException e) {
			// TODO
			System.out.println("Aucun fichier BD");
		} catch (IOException e) {
			e.printStackTrace();
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