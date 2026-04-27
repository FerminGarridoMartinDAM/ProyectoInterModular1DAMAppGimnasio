package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Datos de mi base de datos en Supabase
    // No sabia si guardarla aqui o en el metodo , pero lo veia mas ordenado asi. Y como el metodo era static creo que era mas logico
    // crearlas static para que no tenga que "llamarlas" cada vez que llamamos al metodo.

    private static final String URL = "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres";
    private static final String USUARIO = "postgres.ubgynnvqllswogxuovvw";
    private static final String PASSWORD = "ProyectoIntermodularDAM2026!";

    // La variable estática que guardará la ÚNICA conexión (Patrón Singleton)
    private static Connection conexion;


        //Aqui creamos la conexion con SUPABASE
    private static void getConnection() {
        try {
            // Intentamos abrir el canal con Supabase
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("🛜 Conexión con Supabase establecida correctamente (Singleton)");

        } catch (SQLException e) {
            System.out.println("❌ ERROR: No se ha podido conectar con la base de datos.");
            System.out.println(e.getMessage());
        }
    }

    // Método público para pedir la conexión a la base de datos
    public static Connection conectar() {
        // Solo si la conexión es nula (no existe aún), la creamos
        if (conexion == null) {
            getConnection();
        }
        // Si ya existe, simplemente devolvemos la que está abierta
        return conexion;
    }



    // Asi estaba hecho antes de ver la forma de Borja, el encasuplamiento es peor, y perdemos el principio de responsabilidad unica. Ahora cada metodo tiene su funcion.
// La variable estática que guarda la conexión única
  /*  private static Connection conexion = null;
    // Uso 'static' para poder llamar a este metodo desde cualquier DAO
    // escribiendo simplemente ConexionDB.conectar()
    public static Connection conectar() {

        // 1. Comprobamos si la conexión está vacía o si por algún casual se ha cerrado
        try {
            if (conexion == null || conexion.isClosed()) {

                // Intentamos abrir el canal con Supabase usando mis credenciales
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("🛜 Conexión con base de datos establecida (Nueva apertura)");
            }
        } catch (SQLException e) {
            // Si salta esto, suele ser por falta de internet o contraseñas mal puestas
            System.out.println("❌ ERROR: No se ha podido conectar con la base de datos.");
            e.printStackTrace();
        }

        // Si todo ha ido bien, devuelve la conexion lista para usarse
        System.out.println("🛜Conexion con base de datos establecida");
        // 2. PASE LO QUE PASE, devolvemos la conexión (ya sea recién creada o la que ya teníamos)
        return conexion;
    }*/

}