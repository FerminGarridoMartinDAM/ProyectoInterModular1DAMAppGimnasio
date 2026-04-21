package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Datos de mi base de datos en Supabase
    private static final String URL = "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres";
    private static final String USUARIO = "postgres.ubgynnvqllswogxuovvw";
    private static final String PASSWORD = "ProyectoIntermodularDAM2026!";

    // Uso 'static' para poder llamar a este metodo desde cualquier DAO
    // escribiendo simplemente ConexionDB.conectar()
    public static Connection conectar() {
        Connection conexion = null;

        try {
            // Intentamos abrir el canal con Supabase usando mis credenciales
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);

        } catch (SQLException e) {
            // Si salta esto, suele ser por falta de internet o contraseñas mal puestas
            System.out.println("❌ ERROR: No se ha podido conectar con la base de datos.");
            e.printStackTrace();
        }

        // Si todo ha ido bien, devuelve la conexion lista para usarse
        System.out.println("🛜Conexion con base de datos establecida");
        return conexion;
    }
}