package ejemplo01;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

/**
 * Proyectoo base que utiliza JPA con H2 donde la información necesaria para 
 * acceder al almacén de datos se proporciona a través del archivo persistence.xml
 * 
 * @author Profesor
 */
public class Ejemplo01 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            EntityManagerFactory emf=Persistence.createEntityManagerFactory("EjemplosJPA");
            /*
             Creamos el EntityManager.  
             */
            EntityManager em=emf.createEntityManager();
        
            em.close();
            /*
            No debemos olvidar cerrar el EntityManagerFactory al salir 
            de la aplicación.
            */
            emf.close();
        } catch (PersistenceException pe) {
            System.err.println("Error al conectar con la base de datos.");
        }
    }
    
}
