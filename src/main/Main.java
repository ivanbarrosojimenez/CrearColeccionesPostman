
package main;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import json.BorrarDirectorio;
import json.GenerarPostman;

public class Main {

    static String NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21 = "correos/correoTodos13junio.txt";
    //static String NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21 = "correos/resultados.log";
    static String NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN = "plantilla/plantilla.json";

    public static void main(String[] args) throws IOException {
        /** Generar test postman */ 

    	//Borrar contenido y directorio Colecciones
    	//BorrarDirectorio.eliminarDirectorioYFicheros(new File("Colecciones"));	
    	
    	boolean generarTest = true;
        GenerarPostman generarPostman = new GenerarPostman();
        TreeSet<String> listadoLLamadasJson = 
                generarPostman.obtenerLlamadasJson(NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21);
        
        //TreeSet<String> listadoLLamadasJson = 
                //generarPostman.obtenerLlamadasLog(NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21);

        
        //TreeSet<String> listadoLLamadasJson = 
                      //generarPostman.obtenerTiemposJson(NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21);
        
        System.out.println(listadoLLamadasJson.size());
        

        //generarPostman.obtenerSalidaTiempos(listadoLLamadasJson);

        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE1, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE2, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE3, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE4, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
               GenerarPostman.FASE5, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE6, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE7, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE8, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE9, generarTest);


        //generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                //GenerarPostman.PERFORMANCE, generarTest);
        

        /*generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE21, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE22, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE23, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE24, generarTest);
        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE25, generarTest);*/
        
        //generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                //GenerarPostman.FASE_ERROR, generarTest);
        

    }
}
