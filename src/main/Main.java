
package main;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import json.BorrarDirectorio;
import json.GenerarPostman;

public class Main {

    static String NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21 = "correos/correoTodos14abr.txt";
    static String NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN = "plantilla/plantilla.json";

    public static void main(String[] args) throws IOException {
        /** Generar test postman */ 

    	//Borrar contenido y directorio Colecciones
    	BorrarDirectorio.eliminarDirectorioYFicheros(new File("Colecciones"));	
    	
    	boolean generarTest = true;
        GenerarPostman generarPostman = new GenerarPostman();
        TreeSet<String> listadoLLamadasJson = 
                generarPostman.obtenerLlamadasJson(NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21);

//        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
//                GenerarPostman.FASE1, generarTest);
//        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
//                GenerarPostman.FASE2, generarTest);
//        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
//                GenerarPostman.FASE3, generarTest);
//        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
//                GenerarPostman.FASE4, generarTest);
//        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
//                GenerarPostman.FASE5, generarTest);
//        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
//                GenerarPostman.FASE6, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE_ERROR3, true);
        

    }
}
