package ejemplo01;

import java.io.IOException;
import static java.lang.System.in;
import static java.lang.System.out;
import java.sql.SQLException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import model.Producto;
import org.h2.tools.Server;

/**
 * Proyectoo base que utiliza JPA con H2 donde la información necesaria para
 * acceder al almacén de datos se proporciona a través del archivo
 * persistence.xml
 *
 * @author Profesor
 */
public class Ejemplo01 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] wsArgs = {"-baseDir", System.getProperty("user.dir"), "-browser"};
        try {
            //iniciamos el servidor web interno (consola H2 para depuraciones)
            Server sr = Server.createWebServer(wsArgs);
            sr.start();

            /**
             * Tendremos un EntityManagerFactory por cada unidad de persistencia
             * que necesitemos usar. La función de esta clases es encargarse de
             * gestionar todas las conxiones al almacénn de datos. Crear un
             * EntityManagerFactory es una operación costosa y solo debe hacerse
             * una vez. Se debe usar la misma instancia en todo el código de tu
             * aplicación.
             */
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("EjemplosJPA");
            /*
             Creamos el EntityManager.  
                Podemos crear tantos EntityManager como queramos, pero en una aplicación de escritorio es suficiente con crear uno solo.
             */
            EntityManager em = emf.createEntityManager();
            Producto p = new Producto("Cuchara Elegance", 3.49, "111111111");
            persistirProducto(em, p);
            bloquearHastaPulsarTecla();
            sr.stop();
            sr.shutdown();
            em.close();
            /*
            No debemos olvidar cerrar el EntityManagerFactory al salir 
            de la aplicación.
             */
            emf.close();
        } catch (PersistenceException pe) {
            System.err.println("Error al conectar con la base de datos.");
        } catch (SQLException ex) {
            System.err.printf("No se pudo conectar a la base de datos\n");
        }
    }

    private static void bloquearHastaPulsarTecla() {
        out.println("Antes de terminar, puedes acceder a la consola de H2 para ver"
                + "y modificar la base de datos. Pulsa cualquier tecla para salir.");
        try {
            in.read();
        } catch (IOException e) {
        }
    }

    public static boolean persistirProducto(EntityManager em, Producto p) {
        boolean ok = false;

        /*1º Iniciamos la transacción.*/
        em.getTransaction().begin();
        try {
            /*2º Persistimos la entidad usando el método persist.*/
            em.persist(p);

            /*3º Concluimos la transacción*/
            em.getTransaction().commit();

            /*4º (opcional, pero recomendable) Desligamos la entidad del 
             EntityManager para que cambios en la misma no persistan en 
            otro commit posterior */
            em.detach(p);

            ok = true;
        } catch (EntityExistsException ex) {

            /*5º Excepción que saltará si ya existe una entidad con el mismo ID,
            es conveniente capturarla.*/
            System.out.println("El producto ya existe.");

            /*6º Como la transacción ha ido mal, deshacemos la operación */
            em.getTransaction().rollback();
        }

        return ok;
    }

    public static Producto leerProducto(EntityManager em, long id) {
        /* 1º Buscamos el producto por Id */
        Producto p = em.find(Producto.class, id);
        /* 2º (opcional, pero recomendable) 
           Si el producto se ha encontrado, entonces, lo desacoplamos. */
        if (p != null) {
            em.detach(p);
        }
        return p;
    }
}
