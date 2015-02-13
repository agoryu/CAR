package file;

import java.io.*;

/**
 * @author agoryu
 *
 */
public class FileMagnagement {

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
	public void ecrire(Object o, String fichier) {
		
		FileOutputStream fos;
		ObjectOutputStream out = null;
		
		if(o == null || fichier == null) {
			System.err.println(ERROR_PARAMETER_WRITE);
			return;
		}
		
		if(fichier.compareTo("") == 0) {
			System.err.println(ERROR_PARAMETER_WRITE);
			return;
		}

		try {
			fos = new FileOutputStream(fichier);
			out = new ObjectOutputStream(fos);
			out.writeObject(o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Lecture de données dans un fichier locale
	 * 
	 * @param f
	 *            nom du fichier
	 * @return
	 */
	public byte[] lire(String f) {
		
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
			data = new byte[input.available()];
			final int size = input.read(data);
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

	/*
	 * public static void telechargerFichierOnFTP(final String file) {
	 * //FTPClient client = new FTPClient(); FileOutputStream fos = null;
	 * 
	 * final Socket s = new Socket("anonymous", "mdp"); try {
	 * client.connect("anonymous"); client.login("anonymous", "ok");
	 * 
	 * String filename = file; // FICHIER QUI DOIT ETRE TELECHARGER SUR LE FTP
	 * fos = new FileOutputStream(filename); //TODO FileMagnagement man; Byte
	 * contenu = man.lire(); client.setFileType(client.BINARY_FILE_TYPE); //
	 * INITIALISATION DE LA CONNECTION EN MODE BINAIRE client.retrieveFile("/" +
	 * filename, fos); // TELECHARGEMENT DU FICHIER fos.flush();
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } finally { try { if (fos
	 * != null) { fos.close(); client.disconnect(); } } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * }
	 * 
	 * public static void uploadFichierOnFTP() { FTPClient client = new
	 * FTPClient(); FileInputStream fis = null;
	 * 
	 * try { client.connect("ftp.gl-sound.com");
	 * client.login("admin@gl-sound.com", "helha");
	 * 
	 * String filename = "Bd.dat"; fis = new FileInputStream(filename);
	 * client.setFileType(client.BINARY_FILE_TYPE); client.storeFile(filename,
	 * fis);
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } finally { try { if (fis
	 * != null) { fis.close(); client.logout(); } client.disconnect(); } catch
	 * (IOException e) { e.printStackTrace(); } }
	 * 
	 * 
	 * 
	 * }
	 */

}