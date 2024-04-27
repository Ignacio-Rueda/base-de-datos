package ejemplo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.Server;
import static java.lang.System.*;
import java.sql.ResultSet;
import java.util.stream.Collectors;

/**
 * Aplicación principal del programa. 
 * @author Profesor
 */
public class Aplicacion {

    /**
     * Nombre del archivo de base de datos local.
     */
    private static final String DB_NAME = "proyectobase.h2db";
    /**
     * URL para la conexión a la base de datos.
     */
    private static final String CONNECTION_URL = "jdbc:h2:./" + DB_NAME;
    /**
     * Driver a utilizar para conectarse a la base de datos.
     */
    private static final String DRIVER = "org.h2.Driver";
    /**
     * Opciones de conexión. 
     */
    private static final String CU_PARAMS = ";MODE=MySQL;AUTO_RECONNECT=TRUE";
    
    /**
     * Path al archivo que contiene la estructura de la base de datos.
     */
    public final static String ESTRUCTURA_DB="/resources/estructuradb.sql";  
    
    
    /**
     * Método principal de la aplicación. En él se realiza la preparación
     * del entorno antes de empezar. A destacar:
     * 
     * - Se carga el driver (Class.forName).
     * - Se establece una conexión con la base de datos (DriverManager.getConnection)
     * - Se crean las tablas, si no están creadas, invocando el método createTables.
     * - Se ejecuta una consulta de prueba
     * 
     * @param args 
     */
     public static void main(String[] args) {  
         
        boolean driverCargado=false;
        
        //Carga del driver de la base de datos.
        try {
            Class.forName(DRIVER) ; 
            driverCargado=true;
        } catch (ClassNotFoundException e) {
            err.printf("No se encuentra el driver de la base de datos (%s)\n", DRIVER);
        }
         
        //Si el driver está cargado, aseguramos que podremos conectar.
        if (driverCargado) {
            //Conectamos con la base de datos.
            //El try-with-resources asegura que se cerrará la conexión al salir.
            String[] wsArgs={"-baseDir",System.getProperty("user.dir"),"-browser"};
            try (Connection con = DriverManager.getConnection(CONNECTION_URL+CU_PARAMS,"","")) {
                
                //iniciamos el servidor web interno (consola H2 para depuraciones)
                Server sr=Server.createWebServer(wsArgs);                              
                sr.start();
                
                out.println("¡¡Atención!!");                
                out.println();
                out.println("Mientras tu aplicación se esté ejecutando \n"
                        + "puedes acceder a la consola de la base de datos \n"
                        + "a través del navegador web.");    
                out.println();
                out.println("Página local: " + sr.getURL());     
                out.println();
                out.println("Datos de acceso");                
                out.println("---------------");
                out.println("Controlador: " + DRIVER);
                out.println("URL JDBC: "+CONNECTION_URL);
                out.println("Usuario: (no indicar nada)");                
                out.println("Password: (no indicar nada)");                
                
                /*Creamos las tablas y algunos datos de prueba si no existen
                y continuamos*/
                if (createTables(con)) {
                    
                    //Ejecutamos una consulta de prueba
                    try ( Statement consulta = con.createStatement()) {
                        if (consulta.execute("SELECT id,nombre,precio FROM producto")) {
                            ResultSet resultados = consulta.getResultSet();
                            while (resultados.next()) {
                                long id = resultados.getLong("id");
                                String nombre = resultados.getString("nombre");
                                double precio = resultados.getDouble("precio");
                                System.out.printf("%5d %-15s %10.2f\n", id, nombre, precio);
                            }
                        }
                    } catch (SQLException ex) {
                        System.err.printf("Se ha producio un error al ejecutar la consulta SQL.");
                    }

                    bloquearHastaPulsarTecla();
                    
                } else {
                    err.println("Problema creando las tablas.");

                }                                                        
                
                sr.stop();
                sr.shutdown();
                
            } catch (SQLException ex) {
                err.printf("No se pudo conectar a la base de datos (%s)\n", DB_NAME);
            }
        }

    }
    
    /**
     * Dada una conexión válida, lleva a cabo la creación de la estructura
     * de la base de datos, usando como SQL para la creación el contenido 
     * en la constante ESTRUCTURA_DB
     * @param con conexión a la base de datos.
     * @see ESTRUCTURA_DB
     * @return true si se creo la estructura y false en caso contrario. 
     */
    public static boolean createTables (Connection con) {
        boolean ok = false ;
        
        try (Statement st=con.createStatement())  {                   
            String sqlScript=loadResourceAsString(ESTRUCTURA_DB);
            if (sqlScript!=null) {
                st.execute(sqlScript);                  
                ok = true;
            } else {
                System.out.printf("Problema cargando el archivo: %s \n",ESTRUCTURA_DB);
                System.out.printf("Para ejecutar este proyecto no puede usarse 'Run File'\n");
            }
            
        } catch (SQLException ex) {
            System.err.printf("Problema creando la estructura de la base de datos.\n");
        }
        return ok;        
    }        
    
    /**
      * Carga un recurso que estará dentro del JAR como cadena de texto.
      * @param resourceName Nombre del recurso dentro del JAR.
      * @return Cadena que contiene el contenido del archivo.
      */
    public static String loadResourceAsString(String resourceName) {
        String resource = null;
        InputStream is = Aplicacion.class.getResourceAsStream(resourceName);
        if (is != null) {
            try (InputStreamReader isr = new InputStreamReader(is);  BufferedReader br = new BufferedReader(isr);) {
                resource = br.lines().collect(Collectors.joining("\n"));
            } catch (IOException ex) {
                System.err.printf("Problema leyendo el recurso como cadena: %S\n ", resourceName);
            }
        }
        return resource;
    }
    
    private static void bloquearHastaPulsarTecla() {
        out.println("Antes de terminar, puedes acceder a la consola de H2 para ver"
                + "y modificar la base de datos. Pulsa cualquier tecla para salir.");
        try {
            in.read();
        } catch (IOException e) {
        }
    }
}
