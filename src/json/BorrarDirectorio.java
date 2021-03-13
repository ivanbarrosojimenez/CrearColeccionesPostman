package json;

import java.io.File;

public class BorrarDirectorio {
	
	/**
	 * Borra directorio y ficheros (si contiene)
	 * @param borrar
	 * @return
	 */
	public static boolean eliminarDirectorioYFicheros(File borrar) {
		if (borrar.isDirectory()) {
			try {
				for (File file : borrar.listFiles()) {
					if (file.isFile()) {
						file.delete();
						file.deleteOnExit();
					} else {
						if (file.isDirectory()) {
							eliminarDirectorioYFicheros(file);
							file.delete();
							file.deleteOnExit();
						}
					}
				}
			} catch (NullPointerException e) {
				System.err.println(e);
				return false;
			}
		}
		borrar.delete();
		borrar.deleteOnExit();
		return true;
	}
	static public boolean deleteDirectory(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDirectory(files[i]);
	            } else {
	            	files[i].delete();
	                
	            }
	        }
	    }
	    return (path.delete());
	}
}
