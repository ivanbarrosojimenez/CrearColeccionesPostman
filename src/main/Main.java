
package main;

import java.io.IOException;
import java.util.TreeSet;

import json.GenerarPostman;

public class Main {

    static String NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21 = "correos/llamadas_con_error10032021 1424_Db2.txt";
    static String NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN = "plantilla/plantilla.json";

    public static void main(String[] args) throws IOException {
        /** Generar test postman */ 

        GenerarPostman generarPostman = new GenerarPostman();
        TreeSet<String> listadoLLamadasJson = 
                generarPostman.obtenerLlamadasJson(NOMBRE_FICHERO_ENTRADA_CORREOS_FASE21);

        generarPostman.obtenerSalida(NOMBRE_FICHERO_ENTRADA_PLANTILLA_POSTMAN, listadoLLamadasJson,
                GenerarPostman.FASE_ERROR, true);

    }
}
