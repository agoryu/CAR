package file;

import java.io.*;

/**
 * @author agoryu
 *
 */
public class FileMagnagement {
	
	/** Ecriture fichier local
	 * @param o données
	 * @param fichier nom fichier
	 */
	public static void ecrire(Object o,String fichier)
	{
		//TODO object => byte
		FileOutputStream fos;
		ObjectOutputStream out = null;
		
		try 
		{
			fos = new FileOutputStream(fichier);
			out = new ObjectOutputStream(fos);
			out.writeObject(o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally
		{
				if (out != null)
				{
					try 
					{
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
			
	}
	
	/** lire fichier locale
	 * @param f nom du fichier
	 * @return
	 */
	public byte[] lire(String f)
	{
		//TODO object => byte
		FileInputStream fin;
		ObjectInputStream input = null;
		byte[] o1 = new byte[6400];
		
		try 
		{
			fin = new FileInputStream(f);
			input = new ObjectInputStream(fin);
			input.read(o1);
			
		}catch (FileNotFoundException e) {
			//o1 = new Object();
			System.out.println("Aucun fichier BD");
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		
		finally
		{
			if (input != null)
			{
				try 
				{
					input.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
					
		return o1;
		
	}
	
	
	/*public static void telechargerFichierOnFTP(final String file)
	{
		//FTPClient client = new FTPClient();
        FileOutputStream fos = null;

        final Socket s = new Socket("anonymous", "mdp");
        try {
        	client.connect("anonymous");
            client.login("anonymous", "ok");
            
            String filename = file; // FICHIER QUI DOIT ETRE TELECHARGER SUR LE FTP
            fos = new FileOutputStream(filename);
            //TODO
            FileMagnagement man;
            Byte contenu = man.lire();		
            client.setFileType(client.BINARY_FILE_TYPE);	// INITIALISATION DE LA CONNECTION EN MODE BINAIRE
            client.retrieveFile("/" + filename, fos); // TELECHARGEMENT DU FICHIER
            fos.flush();
                    
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

	}
	
	public static void uploadFichierOnFTP()
	{
		 FTPClient client = new FTPClient();
	        FileInputStream fis = null;

	        try {
	            client.connect("ftp.gl-sound.com");
                client.login("admin@gl-sound.com", "helha");

	            String filename = "Bd.dat";
	            fis = new FileInputStream(filename);
	            client.setFileType(client.BINARY_FILE_TYPE);
	            client.storeFile(filename, fis);
	           
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	        	try {
	                if (fis != null) {
	                    fis.close();
	                    client.logout();
	                }
	                client.disconnect();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
			
		
		
	}*/
	
	
	
}