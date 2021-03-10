package json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GrabarFichero {

    private File f;
    private FileWriter w;
    private BufferedWriter bw;
    private PrintWriter wr;

    public void crearFichero(String nombre, boolean crearDirectorio) throws IOException {
        if (crearDirectorio) {
            String[] partesRuta = nombre.split("/");
            for (int i = 0; i < partesRuta.length - 1; i++) {
                f = new File(partesRuta[i]);
                if (!f.exists()) {
                    if (f.mkdirs()) {
                        System.out.println("Directorio creado");
                    }
                    else {
                        System.out.println("Error al crear directorio");
                    }
                }
            }
            // nombre = partesRuta[partesRuta.length - 1];
        }
        f = new File(nombre);
        w = new FileWriter(f);
        bw = new BufferedWriter(w);
        wr = new PrintWriter(bw);
    }

    public void agregarAFichero(String contenido) throws IOException {
        wr.write(contenido);
    }

    public void cerrarFichero() throws IOException {
        wr.flush();
        wr.close();
        bw.close();
    }
    
    public void agregarAFicheroExistente(String contenido)  throws IOException{
        wr.write(contenido);
    }
    
    public void abrirFichero(String nombre, boolean crearDirectorio)  throws IOException {
        if (crearDirectorio) {
            String[] partesRuta = nombre.split("/");
            for (int i = 0; i < partesRuta.length - 1; i++) {
                f = new File(partesRuta[i]);
                if (!f.exists()) {
                    if (f.mkdirs()) {
                        System.out.println("Directorio creado");
                    }
                    else {
                        System.out.println("Error al crear directorio");
                    }
                }
            }
            // nombre = partesRuta[partesRuta.length - 1];
        }
    	f = new File(nombre);
    	w = new FileWriter(nombre, true);
        bw = new BufferedWriter(w);
        wr = new PrintWriter (bw);
    }
}
