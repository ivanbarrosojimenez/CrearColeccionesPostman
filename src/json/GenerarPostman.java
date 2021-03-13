
package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GenerarPostman {
    public static final int FASE1 = 1;
    public static final int FASE2 = 2;
    public static final int FASE3 = 3;
    public static final int FASE4 = 4;
    public static final int FASE5 = 5;
    public static final int FASE_ERROR = 10;
    StringBuffer sfTransaccionesPorColeccion = new StringBuffer();
    public GenerarPostman() {
    	sfTransaccionesPorColeccion.append("Transación;numero pruebas; colección\r\n");
    	
    }
    public TreeSet<String> obtenerLlamadasJson(String nombreFichero) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        TreeSet<String> llamadasJson = new TreeSet<>();
        try {
            boolean lineaEncontrada = false;
            archivo = new File(nombreFichero);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            while ((linea = br.readLine()) != null) {
                // System.out.println(linea);
                if (linea.startsWith("############ RESPUESTA #############")
                        || linea.startsWith("############ TIEMPOS #############")) {
                    lineaEncontrada = false;
                }
                if (lineaEncontrada && !linea.trim().equals("")) {
                    llamadasJson.add(linea.replaceAll("_ ", "_").replaceAll(" \"", "\"")
                            .replaceAll(" _", "_").replaceAll("\" ", "\"")
                            .replaceAll("(?<=\\w) +(?=\\w)", ""));
                }
                if (linea.startsWith("############ JSON DE LLAMADA ")) {
                    lineaEncontrada = true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return llamadasJson;
    }

    private static int FASE;
    public final static int MAX_NUM_PRUEBAS = 50;
    private static int coleccion = 0;

    private String obtenerIdPostman() {
        Random random = new Random();
        random.ints(10000000l);
        int r1 = random.nextInt();
        int r2 = random.nextInt();
        int r3 = random.nextInt();
        while (r1 < 0 || r2 < 0 || r3 < 0) {
            r1 = random.nextInt();
            r2 = random.nextInt();
            r3 = random.nextInt();
        }
        return "idpostm" + r1 + "" + r2 + "" + r3;
    }

    public StringBuffer obtenerSalida(String f1, TreeSet<String> listaJsonLlamada, int FASE,
            boolean generarTest) throws IOException {
        this.FASE = FASE;
        int numFichero = 0;
        StringBuffer sfRespuesta = new StringBuffer();
        JSONParser parser = new JSONParser();

        // FILTRO DE TRANSACCIONES
        TreeSet<String> listadoFiltrado = new TreeSet<>();
        for (String jsonLlamada : listaJsonLlamada) {
            if (pasaFiltro(jsonLlamada)) {
                listadoFiltrado.add(jsonLlamada);
            }
        }
        // FILTRO DE TRANSACCIONES FIN
        TreeMap<String, Integer> mapaProgramas = new TreeMap<>();
        TreeMap<String, Integer> mapaProgramasTotal = new TreeMap<>();
        String postmanId = obtenerIdPostman();
        TreeSet<String> listaProgramas = new TreeSet<>();
        try {

            // Pares de ficheros, poner de dos en dos

            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(f1));

            // Elemento item (tiene el nombre carpeta y el array item)
            JSONArray arrayItem = (JSONArray) jsonObject.get("item");

            // Copiar y sobrescribir INFO
            JSONObject info = (JSONObject) jsonObject.get("info");
            info.put("name", generarTest ? "Pruebas fase " + FASE + " parte " + numFichero
                    : "Pruebas sin test fase " + FASE + " parte " + numFichero);
            info.put("_postman_id", postmanId);

            // Copia para no alterar el original

            JSONObject elementoRaiz0 = (JSONObject) arrayItem.get(0);

            ArrayList<JSONObject> listadoElementos = new ArrayList<JSONObject>();

            System.out.println("Total de pruebas: " + listadoFiltrado.size());
            int numeroPrueba = 0;
            for (String jsonLlamada : listadoFiltrado) {
                numeroPrueba++;
                // por ejemplo PWSAO999-js
                String nombreCopy = obtenerNombreCopy(jsonLlamada);
                String nombrePrograma = obtenerNombrePrograma(jsonLlamada);
                listaProgramas.add(nombrePrograma);

                mapaProgramas.put(nombrePrograma, mapaProgramas.get(nombrePrograma) == null ? 1
                        : mapaProgramas.get(nombrePrograma) + 1);
                mapaProgramasTotal.put(nombrePrograma, mapaProgramas.get(nombrePrograma) == null ? 1
                        : mapaProgramas.get(nombrePrograma) + 1);
                JSONObject copiaElementoRaiz0 = new JSONObject(elementoRaiz0);
                // NOMBRE DE LA CARPETA A CREAR
                copiaElementoRaiz0.put("name", nombrePrograma);

                JSONArray copiaItem = (JSONArray) copiaElementoRaiz0.get("item");
                JSONObject copiaItem0 = (JSONObject) copiaItem.get(0);
                JSONArray itemItem = (JSONArray) copiaElementoRaiz0.get("item");

                JSONObject elementoItemItem0 = ((JSONObject) itemItem.get(0));
                // NOMBRE DE LA PRUEBA A CREAR
                elementoItemItem0.put("name",
                        "Prueba fase " + FASE + " " + nombreCopy + " #" + numeroPrueba);
                // System.out.println(FASE + "_" + nombreCopy + "_" + numeroPrueba);

                JSONObject request = (JSONObject) elementoItemItem0.get("request");
                JSONObject requestBody = (JSONObject) request.get("body");
                // BODY JSON DE LA LLAMADA
                requestBody.put("raw", jsonLlamada);

                // REQUEST URL
                JSONObject requestUrl = (JSONObject) request.get("url");
                requestUrl.put("raw", "{{urlApi}}/" + nombreCopy);
                JSONArray requestUrlPath = (JSONArray) requestUrl.get("path");
                requestUrlPath.remove(0);
                ArrayList<String> listaPath = new ArrayList<>();
                listaPath.add(nombreCopy);
                requestUrlPath.add(listaPath);

                // NOMBRE DE LA PRUEBA JAVASCRIPT
                JSONArray event = (JSONArray) elementoItemItem0.get("event");
                JSONObject event0 = (JSONObject) event.get(0);
                JSONObject script = (JSONObject) event0.get("script");
                JSONArray exec = (JSONArray) script.get("exec");
                if (generarTest) {

                    exec.set(2, "const NOMBRE_PRUEBA = \"fase_" + FASE + "_" + nombreCopy + "_t_"
                            + numeroPrueba + "\";");
                }
                else {
                    exec.clear();
                }

                String copiaString = copiaElementoRaiz0.toJSONString();
                JSONObject objetoNuevo = (JSONObject) org.json.simple.JSONValue.parse(copiaString);
                listadoElementos.add(objetoNuevo);

                if (numeroPrueba % MAX_NUM_PRUEBAS == 0 || numeroPrueba == listadoFiltrado.size()) {
                    // Borramos el primer elemento
                    arrayItem.remove(0);
                    for (JSONObject jsonObject2 : listadoElementos) {
                        arrayItem.add(jsonObject2);
                    }

                    for (String string : listaProgramas) {
                        // System.out.println(string);
                    }

                    System.out.println("Tamaño item pruebas: " + listadoElementos.size());
                    listadoElementos = new ArrayList<>();

                    System.out.println("prueba # " + (numeroPrueba - 50) + "-" + numeroPrueba);
                    
                    for (Map.Entry<String, Integer> entry : mapaProgramas.entrySet()) {
                        String key = entry.getKey();
                        Integer value = entry.getValue();

                        System.out.println(key + "\t" + value);

                        sfTransaccionesPorColeccion.append(key + ";" + value+";"+coleccion +"\r\n");
                        
                    }
                    coleccion++;
                    mapaProgramas = new TreeMap<>();
                    GrabarFichero grabarFichero = new GrabarFichero();
                    //En la misma carpeta
                    grabarFichero.crearFichero("Colecciones/ColeccionPostman_fase_"
                            + FASE + "_" + (numFichero++) + ".json", true);
                    //Cada una en su carpeta
//                    grabarFichero.crearFichero("ColeccionesFase" + FASE + "/ColeccionPostman_fase_"
//                            + FASE + "_" + (numFichero++) + ".json", true);
                    grabarFichero.agregarAFichero(jsonObject.toJSONString());
                    grabarFichero.cerrarFichero();

                    jsonObject = (JSONObject) parser.parse(new FileReader(f1));
                    // Elemento item (tiene el nombre carpeta y el array item)
                    arrayItem = (JSONArray) jsonObject.get("item");

                    postmanId = obtenerIdPostman();
                    info = (JSONObject) jsonObject.get("info");
                    info.put("name", generarTest ? "Pruebas fase " + FASE + " parte " + numFichero
                            : "Pruebas sin test fase " + FASE + " parte " + numFichero);
                    info.put("_postman_id", postmanId);
                    System.out.println("  ___________");
                }

            }
        }

        catch (

        Exception e) {
            System.out.println(e);
        }
        finally {
            System.out.println("  ___________");

            System.out.println("Total programas fase " + FASE + " " + listaProgramas.size() + "/"
                    + listadoTrnasacciones(FASE).size());
            System.out.println("Programas que faltan:");

            for (String programa : listadoTrnasacciones(FASE)) {
                if (mapaProgramasTotal.get(programa) == null) {
                    System.out.println("    " + programa);
                }
            }
            System.out.println("Listado total");
            for (String programa : listadoTrnasacciones(FASE)) {
                    System.out.println("    " + programa);
                
            }
            
            //Grabar excel con relacion de programas por cada coleccion
            GrabarFichero grabarFichero = new GrabarFichero();
            //En la misma carpeta
            grabarFichero.crearFichero("Colecciones/RelacionColecciones_fase_"
                    + FASE + ".csv", true);
            //En carpetas separadas
//            grabarFichero.crearFichero("ColeccionesFase" + FASE + "/RelacionColecciones_fase_"
//                    + FASE + ".csv", true);
            grabarFichero.agregarAFichero(sfTransaccionesPorColeccion.toString());
            grabarFichero.cerrarFichero();
            
        }
        return sfRespuesta;

    }

    private boolean pasaFiltro(String jsonLlamada) {
        String nombrePrograma = obtenerNombrePrograma(jsonLlamada);
        return listadoTrnasacciones(FASE).contains(nombrePrograma);
    }

    private ArrayList<String> listadoTrnasacciones(int fases) {
        ArrayList<String> a = new ArrayList<String>();

        switch (fases) {
        case 1:
            a.add("POSAZ501");
            a.add("POSAZ521");
            a.add("POSAZ585");
            a.add("POSAZ588");
            a.add("POSAZ593");
            a.add("POSAZ594");
            a.add("POSAZ596");
            a.add("POSAZ598");
            a.add("POSAZ599");
            a.add("POSAZ602");
            a.add("POSAZ608");
            a.add("POSAZ609");
            a.add("POSAZ610");
            a.add("POSAZ612");
            a.add("POSAZ615");
            a.add("POSAZ628");
            a.add("POSAZ500");
            a.add("POSAZ502");
            a.add("POSAZ503");
            a.add("POSAZ504");
            a.add("POSAZ507");
            a.add("POSAZ517");
            a.add("POSAZ519");
            a.add("POSAZ520");
            a.add("POSAZ522");
            a.add("POSAZ523");
            a.add("POSAZ524");
            a.add("POSAZ525");
            a.add("POSAZ526");
            a.add("POSAZ527");
            a.add("POSAZ528");
            a.add("POSAZ530");
            a.add("POSAZ532");
            a.add("POSAZ536");
            a.add("POSAZ544");
            a.add("POSAZ592");
            a.add("POSAZ597");
            a.add("POSAZ600");
            a.add("POSAZ601");
            a.add("POSAZ603");
            a.add("POSAZ604");
            a.add("POSAZ614");
            a.add("POSAZ616");
            a.add("POSAZ617");
            a.add("POSAZ627");
            a.add("POSAZ629");
            a.add("POSAZ631");
            a.add("POSAZ635");
            break;
        case 2:
        	a.add("POSMZ135");
            a.add("POSMZ136");
            a.add("POSMZ137");
            a.add("POSMZ138");
            a.add("POSMZ139");
            a.add("POSMZ140");
            a.add("POSMZ141");
            a.add("POSMZ142");
            a.add("POSMZ144");
            a.add("POSMZ145");
            a.add("POSMZ147");
            a.add("POSMZ148");
            a.add("POSMZ149");
            a.add("POSMZ150");
            a.add("POSMZ151");
            a.add("POSAZ581");
            a.add("POSAZ583");
            a.add("POSAZ584");
            a.add("POSAZ573");
            a.add("POSAZ571");
            a.add("POSLZ165");
            a.add("POSLZ166");
            a.add("POSLZ167");
            a.add("POSLZ168");
            a.add("POSLZ169");
            a.add("POSLZ170");
            a.add("POSAZ545");
            a.add("POSAZ546");
            a.add("POSAZ548");
            a.add("POSAZ591");
            a.add("POSAZ595");

            break;
        case 3:
        	a.add("POSMZ143");
            a.add("POSAZ131");
            a.add("POSAZ505");
            a.add("POSAZ508");
            a.add("POSAZ509");
            a.add("POSAZ513");
            a.add("POSAZ515");
            a.add("POSAZ518");
            a.add("POSAZ535");
            a.add("POSAZ586");
            a.add("POSAZ611");
            a.add("POSAZ102");
            a.add("POSAZ130");
            a.add("POSAZ514");
            a.add("POSAZ531");
            a.add("POSAZ538");
            a.add("POSAZ539");
            a.add("POSAZ547");
            a.add("POSAZ574");
            a.add("POSAZ576");
            a.add("POSAZ589");
            a.add("POSAZ590");
            
            break;
        case 4:
            a.add("POSAZ552");
            a.add("POSAZ516");
            a.add("POSAZ510");
            a.add("POSAZ533");
            a.add("POSAZ534");
            a.add("POSAZ537");
            a.add("POSAZ549");
            a.add("POSAZ550");
            a.add("POSAZ551");
            a.add("POSAZ556");
            a.add("POSAZ557");
            a.add("POSAZ558");
            a.add("POSAZ559");
            a.add("POSAZ560");
            a.add("POSAZ561");
            a.add("POSAZ562");
            a.add("POSAZ563");
            a.add("POSAZ564");
            a.add("POSAZ565");
            a.add("POSAZ569");
            a.add("POSAZ572");
            a.add("POSAZ575");
            a.add("POSAZ577");
            a.add("POSAZ578");
            a.add("POSAZ579");
            a.add("POSAZ580");
            a.add("POSAZ582");
            a.add("POSAZ587");
            a.add("POSAZ605");
            a.add("POSAZ618");
            a.add("POSAZ619");
            a.add("POSAZ620");
            a.add("POSAZ621");
            a.add("POSAZ622");
            a.add("POSAZ623");
            a.add("POSAZ624");
            a.add("POSAZ625");
            a.add("POSAZ626");
            a.add("POSAZ632");
            a.add("POSAZ633");
            a.add("POSAZ634");
            a.add("POSAZ636");
            a.add("POSAZ637");
            break;
        case 10:
//        	 a.add("POSAZ102");
//        	 a.add("POSAZ130");
//        	 a.add("POSAZ500");
//        	 a.add("POSAZ502");
//        	 a.add("POSAZ503");
//        	 a.add("POSAZ504");
//        	 a.add("POSAZ505");
//        	 a.add("POSAZ509");
//        	 a.add("POSAZ510");
//        	 a.add("POSAZ513");
//        	 a.add("POSAZ514");
//        	 a.add("POSAZ515");
//        	 a.add("POSAZ516");
//        	 a.add("POSAZ518");
//        	 a.add("POSAZ519");
//        	 a.add("POSAZ520");
//        	 a.add("POSAZ521");
//        	 a.add("POSAZ522");
//        	 a.add("POSAZ523");
//        	 a.add("POSAZ524");
//        	 a.add("POSAZ525");
//        	 a.add("POSAZ526");
//        	 a.add("POSAZ528");
//        	 a.add("POSAZ530");
//        	 a.add("POSAZ533");
//        	 a.add("POSAZ535");
//        	 a.add("POSAZ536");
//        	 a.add("POSAZ538");
//        	 a.add("POSAZ545");
//        	 a.add("POSAZ546");
//        	 a.add("POSAZ557");
//        	 a.add("POSAZ558");
//        	 a.add("POSAZ559");
//        	 a.add("POSAZ560");
//        	 a.add("POSAZ561");
//        	 a.add("POSAZ563");
//        	 a.add("POSAZ565");
//        	 a.add("POSAZ576");
//        	 a.add("POSAZ578");
//        	 a.add("POSAZ581");
//        	 a.add("POSAZ582");
//        	 a.add("POSAZ586");
//        	 a.add("POSAZ587");
//        	 a.add("POSAZ588");
//        	 a.add("POSAZ589");
//        	 a.add("POSAZ593");
//        	 a.add("POSAZ595");
//        	 a.add("POSAZ596");
//        	 a.add("POSAZ599");
//        	 a.add("POSAZ611");
//        	 a.add("POSAZ617");
//        	 a.add("POSAZ618");
//        	 a.add("POSAZ620");
//        	 a.add("POSAZ621");
//        	 a.add("POSAZ626");
//        	 a.add("POSAZ627");
//        	 a.add("POSAZ634");
//        	 a.add("POSAZ637");
//        	 a.add("POSLZ167");
//        	 a.add("POSLZ169");
//        	 a.add("POSLZ170");
//        	 a.add("POSMZ135");
//        	 a.add("POSMZ140");
//        	 a.add("POSMZ141");
//        	 a.add("POSMZ142");
//        	 a.add("POSMZ143");
//        	 a.add("POSMZ144");
//        	 a.add("POSMZ145");
//        	 a.add("POSMZ149");
//        	 a.add("POSMZ150");
//        	 a.add("POSMZ151");
        	 a.add("POSAZ501");
             a.add("POSAZ521");
             a.add("POSAZ585");
             a.add("POSAZ588");
             a.add("POSAZ593");
             a.add("POSAZ594");
             a.add("POSAZ596");
             a.add("POSAZ598");
             a.add("POSAZ599");
             a.add("POSAZ602");
             a.add("POSAZ608");
             a.add("POSAZ609");
             a.add("POSAZ610");
             a.add("POSAZ612");
             a.add("POSAZ615");
             a.add("POSAZ628");
             a.add("POSAZ500");
             a.add("POSAZ502");
             a.add("POSAZ503");
             a.add("POSAZ504");
             a.add("POSAZ507");
             a.add("POSAZ517");
             a.add("POSAZ519");
             a.add("POSAZ520");
             a.add("POSAZ522");
             a.add("POSAZ523");
             a.add("POSAZ524");
             a.add("POSAZ525");
             a.add("POSAZ526");
             a.add("POSAZ527");
             a.add("POSAZ528");
             a.add("POSAZ530");
             a.add("POSAZ532");
             a.add("POSAZ536");
             a.add("POSAZ544");
             a.add("POSAZ592");
             a.add("POSAZ597");
             a.add("POSAZ600");
             a.add("POSAZ601");
             a.add("POSAZ603");
             a.add("POSAZ604");
             a.add("POSAZ614");
             a.add("POSAZ616");
             a.add("POSAZ617");
             a.add("POSAZ627");
             a.add("POSAZ629");
             a.add("POSAZ631");
             a.add("POSAZ635");
         	a.add("POSMZ135");
             a.add("POSMZ136");
             a.add("POSMZ137");
             a.add("POSMZ138");
             a.add("POSMZ139");
             a.add("POSMZ140");
             a.add("POSMZ141");
             a.add("POSMZ142");
             a.add("POSMZ144");
             a.add("POSMZ145");
             a.add("POSMZ147");
             a.add("POSMZ148");
             a.add("POSMZ149");
             a.add("POSMZ150");
             a.add("POSMZ151");
             a.add("POSAZ581");
             a.add("POSAZ583");
             a.add("POSAZ584");
             a.add("POSAZ573");
             a.add("POSAZ571");
             a.add("POSLZ165");
             a.add("POSLZ166");
             a.add("POSLZ167");
             a.add("POSLZ168");
             a.add("POSLZ169");
             a.add("POSLZ170");
             a.add("POSAZ545");
             a.add("POSAZ546");
             a.add("POSAZ548");
             a.add("POSAZ591");
             a.add("POSAZ595");
         	a.add("POSMZ143");
             a.add("POSAZ131");
             a.add("POSAZ505");
             a.add("POSAZ508");
             a.add("POSAZ509");
             a.add("POSAZ513");
             a.add("POSAZ515");
             a.add("POSAZ518");
             a.add("POSAZ535");
             a.add("POSAZ586");
             a.add("POSAZ611");
             a.add("POSAZ102");
             a.add("POSAZ130");
             a.add("POSAZ514");
             a.add("POSAZ531");
             a.add("POSAZ538");
             a.add("POSAZ539");
             a.add("POSAZ547");
             a.add("POSAZ574");
             a.add("POSAZ576");
             a.add("POSAZ589");
             a.add("POSAZ590");
             a.add("POSAZ552");
             a.add("POSAZ516");
             a.add("POSAZ510");
             a.add("POSAZ533");
             a.add("POSAZ534");
             a.add("POSAZ537");
             a.add("POSAZ549");
             a.add("POSAZ550");
             a.add("POSAZ551");
             a.add("POSAZ556");
             a.add("POSAZ557");
             a.add("POSAZ558");
             a.add("POSAZ559");
             a.add("POSAZ560");
             a.add("POSAZ561");
             a.add("POSAZ562");
             a.add("POSAZ563");
             a.add("POSAZ564");
             a.add("POSAZ565");
             a.add("POSAZ569");
             a.add("POSAZ572");
             a.add("POSAZ575");
             a.add("POSAZ577");
             a.add("POSAZ578");
             a.add("POSAZ579");
             a.add("POSAZ580");
             a.add("POSAZ582");
             a.add("POSAZ587");
             a.add("POSAZ605");
             a.add("POSAZ618");
             a.add("POSAZ619");
             a.add("POSAZ620");
             a.add("POSAZ621");
             a.add("POSAZ622");
             a.add("POSAZ623");
             a.add("POSAZ624");
             a.add("POSAZ625");
             a.add("POSAZ626");
             a.add("POSAZ632");
             a.add("POSAZ633");
             a.add("POSAZ634");
             a.add("POSAZ636");
             a.add("POSAZ637");
            break;
        default:
            break;
        }
        return a;
    }

    private String obtenerNombrePrograma(String jsonLlamada) {
        return jsonLlamada.substring(2, 10);
    }

    private String obtenerNombreCopy(String jsonLlamada) {
        return "PW" + jsonLlamada.substring(4, 6) + "O" + jsonLlamada.substring(7, 10) + "-js";
    }

}
