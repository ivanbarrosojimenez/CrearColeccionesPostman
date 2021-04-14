package json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

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
        
//        CharsetEncoder encoder = Charset.forName("Windows-1252").newEncoder(); 
//        encoder.onMalformedInput(CodingErrorAction.REPORT); 
//        encoder.onUnmappableCharacter(CodingErrorAction.REPORT); 
//        
//
//
//        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),encoder)); 
        
        
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
