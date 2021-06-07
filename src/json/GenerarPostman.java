
package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
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
    public static final int FASE6 = 6;
    public static final int FASE7 = 7;
    public static final int FASE8 = 8;
    public static final int FASE9 = 9;
    public static final int FASE_ERROR = 10;
    public static final int FASE_ERROR2 = 11;
    public static final int FASE_ERROR3 = 12;
    
    public static final int FASE21 = 21;
    public static final int FASE22 = 22;
    public static final int FASE23 = 23;
    public static final int FASE24 = 24;
    public static final int FASE25 = 25;
    public static final int FASE26 = 26;
    
    public static final int PERFORMANCE = 50;
    StringBuffer sfTransaccionesPorColeccion = new StringBuffer();
    StringBuffer sfTransaccionesPorTiempo = new StringBuffer();
    public GenerarPostman() {
    	sfTransaccionesPorColeccion.append("Transaci�n;numero pruebas; colecci�n\r\n");
    	sfTransaccionesPorTiempo.append("URL;Transaccion;Tiempo\r\n");    	
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
            //br = new BufferedReader(fr);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(nombreFichero), "Cp1252"));

            // Lectura del fichero
            String linea;
            while ((linea = br.readLine()) != null) {
                // System.out.println(linea);
                if (linea.startsWith("############ RESPUESTA #############")
                        || linea.startsWith("############ TIEMPOS #############")) {
                    lineaEncontrada = false;
                }
                if (lineaEncontrada && !linea.trim().equals("")) {
                	linea = Normalizer.normalize(linea, Normalizer.Form.NFD);   
                    linea = linea.replaceAll("[^\\p{ASCII}]", "");
                    
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
    
    public TreeSet<String> obtenerTiemposJson(String nombreFichero) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        TreeSet<String> llamadasJson = new TreeSet<>();
        try {
            boolean lineaEncontrada = false;
            archivo = new File(nombreFichero);
            fr = new FileReader(archivo);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(nombreFichero), "Cp1252"));

            // Lectura del fichero
            String linea;
            List<String> listCampos = new ArrayList<String>();
            while ((linea = br.readLine()) != null) {
            	if (linea.startsWith("De:")) {
            		listCampos = new ArrayList<String>();
            	}
            	
            	if (linea.startsWith("Asunto:	LLAMADA Y RESPUESTA URL:") || linea.startsWith("{\"P")
            			|| linea.contains("segundos.")) {
                    lineaEncontrada = true;
                }
            	
            	if (lineaEncontrada && !linea.trim().equals("")) {
            		if (linea.indexOf("http") != -1 && linea.indexOf("-js") != -1) {
            			String url = linea.substring(linea.indexOf("http"), linea.indexOf("-js") + 3);
                		listCampos.add(url);	
            		} else if (linea.indexOf("{\"P") != -1) {
            			String nombrePrograma = linea.substring(2, 10);
            			listCampos.add(nombrePrograma);
            		} else if (linea.indexOf("segundos.") != -1) {
            			String tiempo = linea.substring(0, linea.indexOf("segundos.") - 1);
            			listCampos.add(tiempo);
            		}
            	}
            	
            	if (listCampos.size() == 3) {
            		if (listCampos.get(0).contains("http")) {
            			String lineaTiempos = listCampos.get(0) + ";" + listCampos.get(1) + ";" + listCampos.get(2);
                		llamadasJson.add(lineaTiempos);	
            		}            		
            		listCampos = new ArrayList<String>();
            	}
            	lineaEncontrada = false;
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
    public final static int MAX_NUM_PRUEBAS = 25;
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
    
    public void obtenerSalidaTiempos(TreeSet<String> listaJsonTiempos) throws IOException {
        for (String jsonLlamada : listaJsonTiempos) {
        	sfTransaccionesPorTiempo.append(jsonLlamada + "\r\n");
        }
        try {
        	GrabarFichero grabarFichero = new GrabarFichero();
            grabarFichero.crearFichero("Tiempos/TiemposTransaccion" + ".csv", true);
            grabarFichero.agregarAFichero(sfTransaccionesPorTiempo.toString());
            grabarFichero.cerrarFichero();
        }

        catch (Exception e) {
            System.out.println(e);
        }
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
            if (pasaFiltro(jsonLlamada) && pasaFiltroOperacion(jsonLlamada, FASE)) {
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

                    System.out.println("Tamanio item pruebas: " + listadoElementos.size());
                    listadoElementos = new ArrayList<>();

                    System.out.println("prueba # " + (numeroPrueba - MAX_NUM_PRUEBAS) + "-" + numeroPrueba);
                    
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
    
    private boolean pasaFiltroOperacion(String jsonLlamada, int fase) {
    	String nombrePrograma = obtenerNombrePrograma(jsonLlamada);
    	String codigoOperacion = obtenerCodigoOperacion(jsonLlamada, nombrePrograma);
    	
    	switch (fase) {
    	case 1:
    		if (nombrePrograma.contains("POSAZ599")) {
    			return !codigoOperacion.equals("L");
    		} else if (nombrePrograma.contains("POSAZ609")) {
    			return !codigoOperacion.equals("L");
    		} else if (nombrePrograma.contains("POSAZ610")) {
    			return !codigoOperacion.equals("D");
    		} else if (nombrePrograma.contains("POSAZ526")) {
    			return !codigoOperacion.equals("C");
    		} else if (nombrePrograma.contains("POSAZ527")) {
    			return !codigoOperacion.equals("D");
    		} else if (nombrePrograma.contains("POSAZ502")) {
    			return !codigoOperacion.equals("C");
    		} else if (nombrePrograma.contains("POSAZ557")) {
    			return !codigoOperacion.equals("L");
    		} else if (nombrePrograma.contains("POSAZ102")) {
    			return !(codigoOperacion.equals("L") && codigoOperacion.equals("D"));
    		} else if (nombrePrograma.contains("POSLZ169")) {
    			return !codigoOperacion.equals("L");
    		}

    		return true;
    		
    	case 2:
    		if (nombrePrograma.contains("POSAZ516")) {
    			return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ510")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ130")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ509")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ536")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ601")) {
    			return !codigoOperacion.equals("D");
    		}
    		
    		return true;
    		
    	case 3:
    		if (nombrePrograma.contains("POSAZ538")) {
    			return codigoOperacion.equals("C");
        	} else if (nombrePrograma.contains("POSAZ516")) {
    			return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ514")) {
    			return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ510")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ130")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ509")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ536")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ561")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ631")) {
        		return codigoOperacion.equals("A1");
        	} else if (nombrePrograma.contains("POSMZ138")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSLZ170")) {
        		return !codigoOperacion.equals("C");
        	} else if (nombrePrograma.contains("POSMZ148")) {
        		return !codigoOperacion.equals("L");
        	}
    		
    		return true;
    		
    	case 4:
    		if (nombrePrograma.contains("POSAZ558")) {
    			return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ514")) {
    			return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ552")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ510")) {
        		return codigoOperacion.equals("C");
        	} else if (nombrePrograma.contains("POSAZ130")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ509")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ536")) {
        		return codigoOperacion.equals("C") || codigoOperacion.equals("V");
        	} else if (nombrePrograma.contains("POSAZ561")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ631")) {
        		return codigoOperacion.equals("U1");
        	} else if (nombrePrograma.contains("POSAZ503")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ535")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.equals("POSMZ135")) {
        		return codigoOperacion.equals("A");
        	} 
    		
    		return true;
    		
		case 5:
			if (nombrePrograma.contains("POSAZ582")) {
				return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ558")) {
				return codigoOperacion.equals("M");				
        	} else if (nombrePrograma.contains("POSAZ538")) {
    			return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ516")) {
    			return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ514")) {
    			return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ552")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ510")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ132")) {
        		return codigoOperacion.equals("A");
        	} else if (nombrePrograma.contains("POSAZ561")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ631")) {
        		return codigoOperacion.equals("S1") || codigoOperacion.equals("S2") || 
        				codigoOperacion.equals("S3") || codigoOperacion.equals("O1");
        	} else if (nombrePrograma.contains("POSAZ503")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ535")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.equals("POSMZ135")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSMZ138")) {
        		return codigoOperacion.equals("C");
        	} else if (nombrePrograma.contains("POSAZ598")) {
        		return !codigoOperacion.equals("D");
        	}
			
			return true;

		case 6:
			if (nombrePrograma.contains("POSAZ582")) {
				return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ538")) {
    			return codigoOperacion.equals("R");
        	} else if (nombrePrograma.contains("POSAZ132")) {
        		return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ503")) {
        		return codigoOperacion.equals("R");
        	} else if (nombrePrograma.contains("POSAZ535")) {
        		return codigoOperacion.equals("C") || codigoOperacion.equals("L");
        	} else if (nombrePrograma.equals("POSMZ135")) {
        		return codigoOperacion.equals("D");
        	} else if (nombrePrograma.contains("POSMZ138")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ611")) {
        		return !codigoOperacion.equals("L");
        	}
			
			return true;
		
		case 7:
			if (nombrePrograma.contains("POSAZ538")) {
    			return codigoOperacion.equals("M");
        	} else if (nombrePrograma.contains("POSAZ132")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.contains("POSAZ503")) {
        		return codigoOperacion.equals("B");
        	} else if (nombrePrograma.equals("POSMZ135")) {
        		return codigoOperacion.equals("R");
        	}
			
			return true;
		
		case 8:

			if (nombrePrograma.contains("POSMZ138")) {
        		return codigoOperacion.equals("M");
        	}
			if (nombrePrograma.contains("POSAZ599")) {
				return codigoOperacion.equals("L");
			} else if (nombrePrograma.contains("POSAZ609")) {
				return codigoOperacion.equals("L");
			} else if (nombrePrograma.contains("POSAZ610")) {
				return codigoOperacion.equals("D");
			} else if (nombrePrograma.contains("POSAZ526")) {
				return codigoOperacion.equals("C");
			} else if (nombrePrograma.contains("POSAZ527")) {
				return codigoOperacion.equals("D");
			} else if (nombrePrograma.contains("POSAZ601")) {
				return codigoOperacion.equals("D");
			} else if (nombrePrograma.contains("POSLZ170")) {
				return codigoOperacion.equals("C");
			} else if (nombrePrograma.contains("POSMZ148")) {
				return codigoOperacion.equals("L");
			} else if (nombrePrograma.contains("POSAZ102")) {
				return codigoOperacion.equals("L") || codigoOperacion.equals("D");
			} else if (nombrePrograma.contains("POSLZ169")) {
				return codigoOperacion.equals("L");
			}

			return true;
			
		case 9:
			if (nombrePrograma.contains("POSAZ611")) {
				return codigoOperacion.equals("L");
			} else if (nombrePrograma.contains("POSAZ598")) {
				return codigoOperacion.equals("D");
			} else if (nombrePrograma.contains("POSAZ502")) {
				return codigoOperacion.equals("C");
			} else if (nombrePrograma.contains("POSAZ557")) {
				return codigoOperacion.equals("L");
			}
			
			return true;
			
		case 10:
			return true;
			
		case 11:
			return true;
			
		case 12:
			return true;
			
		case 21:
			return true;
		
		case 22:
			return true;
			
		case 23:
			return true;
			
		case 24:
			return true;
			
		case 25:
			return true;
			
		case 26:
			return true;
			
		case PERFORMANCE:
    		return true;
			
		default:
			break;
		}   	
    	
    	return false;
    }

    private ArrayList<String> listadoTrnasacciones(int fases) {
        ArrayList<String> a = new ArrayList<String>();

        switch (fases) {
        case 1:
        	//solo lectura
        	
        	//fin solo lectura
        	
            a.add("POSAZ593");
            a.add("POSAZ599");
            a.add("POSAZ602");
            a.add("POSAZ608");
            a.add("POSAZ609");
            a.add("POSAZ610");
           
            a.add("POSAZ504");
            a.add("POSAZ507");
            a.add("POSAZ519");
            a.add("POSAZ526");
            a.add("POSAZ527");
            a.add("POSAZ502");
            a.add("POSAZ557");
            a.add("POSAZ102");
            a.add("POSLZ169");
            
            break;
            
        case 2:
            a.add("POSAZ532");
            a.add("POSAZ536");
            a.add("POSAZ601");
            a.add("POSAZ603");
            a.add("POSAZ617");
            a.add("POSAZ509");
            a.add("POSAZ516");
            a.add("POSAZ510");
            a.add("POSAZ130");
            
        	break;
        case 3:
            a.add("POSAZ631");
            a.add("POSMZ138");
            a.add("POSMZ140");
            a.add("POSMZ142");
            a.add("POSMZ145");
            a.add("POSMZ147");
            a.add("POSMZ148");
            a.add("POSMZ151");
            a.add("POSAZ573");
            a.add("POSLZ165");
            a.add("POSLZ167");
            a.add("POSLZ168");
            a.add("POSLZ170");
            a.add("POSAZ130");
            a.add("POSAZ538");
            a.add("POSAZ516");
            a.add("POSAZ514");
            a.add("POSAZ510");
            a.add("POSAZ509");
            a.add("POSAZ536");
            a.add("POSAZ561");

            break;
        case 4:
            a.add("POSAZ503");
            a.add("POSAZ535");
            a.add("POSAZ531");
            a.add("POSAZ558");
            a.add("POSAZ514");
            a.add("POSAZ552");
            a.add("POSAZ510");
            a.add("POSAZ130"); 
            a.add("POSAZ509");
            a.add("POSAZ536");
            a.add("POSAZ561");
            a.add("POSAZ631");
            a.add("POSMZ135");
            
            break;
        case 5:
            a.add("POSAZ552");
            a.add("POSAZ516");
            a.add("POSAZ510");
            a.add("POSAZ533");
            a.add("POSAZ537");
            a.add("POSAZ549");
            a.add("POSAZ556");            
            a.add("POSAZ558");           
            a.add("POSAZ561");
            a.add("POSAZ565");
            a.add("POSAZ575");
            a.add("POSAZ577");            
            a.add("POSAZ582");
            a.add("POSAZ619");
            a.add("POSAZ623");
            a.add("POSAZ625");            
            a.add("POSAZ632");
            a.add("POSAZ633");
            a.add("POSAZ543");
            a.add("POSAZ538");
            a.add("POSAZ514");
            a.add("POSAZ132");
            a.add("POSAZ598");
            a.add("POSAZ631");
            a.add("POSAZ503");
            a.add("POSAZ535");
            a.add("POSMZ135");
            a.add("POSMZ138");

            break;
        case 6:
            a.add("POSAZ636");
            a.add("POSAZ520");
            a.add("POSAZ523");
            a.add("POSAZ538");                        
        	a.add("POSMZ135");
            a.add("POSMZ137");
            a.add("POSAZ611");
            a.add("POSAZ600");
            a.add("POSAZ582");
            a.add("POSAZ132");
            a.add("POSAZ503");
            a.add("POSAZ535");
            a.add("POSMZ138");

            break;
        case 7:
            a.add("POSAZ538");
            a.add("POSAZ132");
            a.add("POSAZ503");
            a.add("POSMZ135");
            a.add("POSAZ594");

        	break;
        	
        //Operaciones de solo lectura	
        case 8:
        	
        	a.add("POSAZ500");
            a.add("POSLZ166");
            a.add("POSLZ169");
            a.add("POSAZ501");
            a.add("POSAZ521");
            a.add("POSAZ585");
            a.add("POSAZ588");
            a.add("POSAZ596");
            a.add("POSAZ612");
            a.add("POSAZ615");
            a.add("POSAZ628");
            a.add("POSAZ522");
            a.add("POSAZ524");
            a.add("POSAZ525");
            a.add("POSAZ528");
            a.add("POSAZ508");
            a.add("POSAZ505");
            a.add("POSAZ513");
            a.add("POSAZ515");
            a.add("POSAZ518");
            a.add("POSAZ534");
            a.add("POSAZ586");
            a.add("POSAZ102");
            a.add("POSAZ539");
            a.add("POSAZ547");
            a.add("POSAZ574");
            a.add("POSAZ576");
            a.add("POSAZ589");
            a.add("POSAZ590");            
            a.add("POSAZ591");
            a.add("POSAZ595");
            a.add("POSAZ635");
            a.add("POSAZ581");
            a.add("POSAZ583");
            a.add("POSAZ584");
            a.add("POSMZ149");
            a.add("POSMZ150");
            a.add("POSMZ136");
            a.add("POSMZ139");
            a.add("POSMZ141");
        	a.add("POSMZ143");
            a.add("POSMZ144");

            a.add("POSAZ599");
            a.add("POSAZ609");
            a.add("POSAZ610");
            a.add("POSAZ526");
            a.add("POSAZ527");
            a.add("POSAZ601");
            a.add("POSLZ170");
            a.add("POSMZ148");
            
        	break;  
        //Operaciones de solo lectura	
        case 9:
            a.add("POSAZ545");
            a.add("POSAZ546");
            a.add("POSAZ548");
            a.add("POSAZ550");
            a.add("POSAZ551");
            a.add("POSAZ557");
            a.add("POSAZ559");
            a.add("POSAZ560");
            a.add("POSAZ562");
            a.add("POSAZ563");
            a.add("POSAZ564");
            a.add("POSAZ569");
            a.add("POSAZ571");
            a.add("POSAZ572");
            a.add("POSAZ578");
            a.add("POSAZ579");
            a.add("POSAZ580");
            a.add("POSAZ587");
            a.add("POSAZ605");
        	a.add("POSAZ618");
            a.add("POSAZ620");
            a.add("POSAZ621");
            a.add("POSAZ622");
            a.add("POSAZ624");
            a.add("POSAZ626");
            a.add("POSAZ634");
            a.add("POSAZ637");
            a.add("POSAZ502");
            a.add("POSAZ517");
            a.add("POSAZ530");
            a.add("POSAZ544");
            a.add("POSAZ592");
            a.add("POSAZ597");
            a.add("POSAZ604");
            a.add("POSAZ614");
            a.add("POSAZ616");
            a.add("POSAZ627");
            a.add("POSAZ629");
            a.add("POSAZ131");
            a.add("POSAZ611");
            a.add("POSAZ598");
            
        	break;
        case 10:
/*        	 a.add("POSAZ102");
        	 a.add("POSAZ130");
        	 a.add("POSAZ500");
        	 a.add("POSAZ502");
        	 a.add("POSAZ503");
        	 a.add("POSAZ504");
        	 a.add("POSAZ505");
        	 a.add("POSAZ509");
        	 a.add("POSAZ510");
        	 a.add("POSAZ513");
        	 a.add("POSAZ514");
        	 a.add("POSAZ515");
        	 a.add("POSAZ516");
        	 a.add("POSAZ518");
        	 a.add("POSAZ519");
        	 a.add("POSAZ520");
        	 a.add("POSAZ521");
        	 a.add("POSAZ522");
        	 a.add("POSAZ523");
        	 a.add("POSAZ524");
        	 a.add("POSAZ525");
        	 a.add("POSAZ526");
        	 a.add("POSAZ528");
        	 a.add("POSAZ530");
        	 a.add("POSAZ533");
        	 a.add("POSAZ535");
        	 a.add("POSAZ536");
        	 a.add("POSAZ538");
        	 a.add("POSAZ545");
        	 a.add("POSAZ546");
        	 a.add("POSAZ557");
        	 a.add("POSAZ558");
        	 a.add("POSAZ559");
        	 a.add("POSAZ560");
        	 a.add("POSAZ561");
        	 a.add("POSAZ563");
        	 a.add("POSAZ565");
        	 a.add("POSAZ576");
        	 a.add("POSAZ578");
        	 a.add("POSAZ581");
        	 a.add("POSAZ582");
        	 a.add("POSAZ586");
        	 a.add("POSAZ587");
        	 a.add("POSAZ588");
        	 a.add("POSAZ589");
        	 a.add("POSAZ593");
        	 a.add("POSAZ595");
        	 a.add("POSAZ596");
        	 a.add("POSAZ599");
        	 a.add("POSAZ611");
        	 a.add("POSAZ617");
        	 a.add("POSAZ618");
        	 a.add("POSAZ620");
        	 a.add("POSAZ621");
        	 a.add("POSAZ626");
        	 a.add("POSAZ627");
        	 a.add("POSAZ634");
        	 a.add("POSAZ637");
        	 a.add("POSLZ167");
        	 a.add("POSLZ169");
        	 a.add("POSLZ170");
        	 a.add("POSMZ135");
        	 a.add("POSMZ140");
        	 a.add("POSMZ141");
        	 a.add("POSMZ142");
        	 a.add("POSMZ143");
        	 a.add("POSMZ144");
        	 a.add("POSMZ145");
        	 a.add("POSMZ149");
        	 a.add("POSMZ150");
        	 a.add("POSMZ151");
        	
        	
        	
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
             a.add("POSAZ637");*/
        	
        	a.add("POSAZ593");
        	a.add("POSAZ596");
        	a.add("POSAZ601");
        	a.add("POSAZ599");
        	a.add("POSAZ598");
        	a.add("POSAZ521");
        	a.add("POSAZ501");
        	a.add("POSAZ588");
        	a.add("POSAZ600");
        	a.add("POSAZ585");
        	a.add("POSAZ604");
        	a.add("POSAZ628");
        	a.add("POSAZ594");
        	a.add("POSAZ611");
        	a.add("POSAZ627");
        	a.add("POSAZ597");
        	a.add("POSAZ592");
        	a.add("POSAZ631");
        	
            break;
        case FASE_ERROR2:
//	        a.add("POSAZ593");
//	        a.add("POSAZ520");
//            a.add("POSAZ557");

        break;
        case 12:
//	        a.add("POSAZ593");
//	        a.add("POSAZ626");
//            a.add("POSAZ637");

        break;
        
        /*case 21:
        	a.add("POSLZ166");
        	a.add("POSLZ169");
        	a.add("POSAZ501");
        	a.add("POSAZ521");
        	a.add("POSAZ585");
        	a.add("POSAZ588");
        	a.add("POSAZ594");
        	a.add("POSAZ596");
        	a.add("POSAZ612");
        	a.add("POSAZ615");
        	a.add("POSAZ628");
        	a.add("POSAZ545");
        	a.add("POSAZ546");
        	a.add("POSAZ548");
        	a.add("POSAZ550");
        	
        	break;
        	
        case 22:
        	a.add("POSAZ551");
        	a.add("POSAZ557");
        	a.add("POSAZ559");
        	a.add("POSAZ560");
        	a.add("POSAZ562");
        	a.add("POSAZ563");
        	a.add("POSAZ564");
        	a.add("POSAZ569");
        	a.add("POSAZ571");
        	a.add("POSAZ572");
        	a.add("POSAZ578");
        	a.add("POSAZ579");
        	a.add("POSAZ580");
        	a.add("POSAZ587");
        	a.add("POSAZ618");
        	
        	break;
        
        case 23:
        	a.add("POSAZ620");
        	a.add("POSAZ621");
        	a.add("POSAZ622");
        	a.add("POSAZ624");
        	a.add("POSAZ626");
        	a.add("POSAZ634");
        	a.add("POSAZ637");
        	a.add("POSAZ502");
        	a.add("POSAZ522");
        	a.add("POSAZ524");
        	a.add("POSAZ528");
        	a.add("POSAZ530");
        	a.add("POSAZ544");
        	a.add("POSAZ592");
        	a.add("POSAZ597");
        	a.add("POSAZ604");
        	
        	break;
        	
        case 24:
        	a.add("POSAZ614");
        	a.add("POSAZ627");
        	a.add("POSAZ629");
        	a.add("POSAZ131");
        	a.add("POSAZ505");
        	a.add("POSAZ508");
        	a.add("POSAZ513");
        	a.add("POSAZ515");
        	a.add("POSAZ518");
        	a.add("POSAZ534");
        	a.add("POSAZ102");
        	a.add("POSAZ539");
        	a.add("POSAZ547");
        	a.add("POSAZ574");
        	
        	break;
        	
        case 25:
        	a.add("POSAZ576");
        	a.add("POSAZ589");
        	a.add("POSAZ590");
        	a.add("POSAZ591");
        	a.add("POSAZ595");
        	a.add("POSAZ635");
        	a.add("POSAZ581");
        	a.add("POSAZ583");
        	a.add("POSAZ584");
        	a.add("POSMZ149");
        	a.add("POSMZ150");
        	a.add("POSMZ136");
        	a.add("POSMZ139");
        	a.add("POSMZ141");
        	a.add("POSMZ143");
        	a.add("POSMZ144");
        	
        	break;*/
        
        case 26:
        	/*a.add("POSAZ526");
        	a.add("POSAZ595");
        	a.add("POSAZ631");
        	a.add("POSAZ537");
        	a.add("POSAZ561");
        	a.add("POSLZ167");
        	a.add("POSAZ584");
        	a.add("POSAZ528");*/
        	//a.add("POSAZ502");
        	//a.add("POSAZ503");
        	
        	break;
        	
        case PERFORMANCE:
        	a.add("POSAZ500");
        	a.add("POSAZ517");
        	a.add("POSAZ524");
        	a.add("POSAZ527");
        	a.add("POSAZ528");
        	a.add("POSAZ530");
        	a.add("POSAZ544");
        	a.add("POSAZ549");
        	a.add("POSAZ550");
        	a.add("POSAZ552");
        	a.add("POSAZ587");
        	a.add("POSAZ591");
        	a.add("POSAZ593");
        	a.add("POSAZ603");
        	a.add("POSAZ605");
        	a.add("POSAZ608");
        	a.add("POSAZ618");
        	a.add("POSAZ621");
        	a.add("POSLZ165");
        	a.add("POSLZ166");
        	a.add("POSLZ167");
        	a.add("POSLZ169");
        	a.add("POSMZ139");
        	a.add("POSMZ150");
        	break;
        default:
            break;
        }
        return a;
    }

    private String obtenerNombrePrograma(String jsonLlamada) {    	
    	System.out.println(jsonLlamada.replaceAll(" ", "").substring(2, 10));
        return jsonLlamada.replaceAll(" ", "").substring(2, 10);
    }
    
    private String obtenerCodigoOperacion(String jsonLlamada, String nombrePrograma) {
    	int indexOpe = jsonLlamada.indexOf("cod_operacion_e");
    	if (nombrePrograma.contains("POSAZ631")) {
    		return jsonLlamada.substring(indexOpe + 18, indexOpe + 20);	
    	} else {
    		return jsonLlamada.substring(indexOpe + 18, indexOpe + 19);	
    	}    	
    }

    private String obtenerNombreCopy(String jsonLlamada) {
    	jsonLlamada = jsonLlamada.replace(" ", "");
        return "PW" + jsonLlamada.substring(4, 6) + "O" + jsonLlamada.substring(7, 10) + "-js";
    }

}
