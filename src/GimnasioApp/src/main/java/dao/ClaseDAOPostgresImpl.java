package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Clase;
import model.enums.EstadoClase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaseDAOPostgresImpl implements ClaseDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public ClaseDAOPostgresImpl() {
        // Pedimos la conexión al Singleton al instanciar el DAO
        connection = ConexionDB.getConexion();
    }

    @Override
    public int insert(Clase clase) {
        String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                SchemDB.TAB_CLASE, SchemDB.COL_CLASE_NOMBRE, SchemDB.COL_CLASE_AFORO, SchemDB.COL_CLASE_ESTADO);

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos del objeto al SQL
            preparedStatement.setString(1, clase.getNombre());
            preparedStatement.setInt(2, clase.getAforoMax());
            preparedStatement.setString(3, clase.getEstado().name());

            // Ejecutamos y retornamos el número de filas afectadas
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Clase> selectAll() {
        List<Clase> listaClases = new ArrayList<>();
        String query = "SELECT * FROM " + SchemDB.TAB_CLASE;

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Ejecutamos la consulta de lectura
            resultSet = preparedStatement.executeQuery();

            // Recorremos los resultados mapeando cada columna
            while (resultSet.next()) {
                int id = resultSet.getInt(SchemDB.COL_CLASE_ID);
                String nombre = resultSet.getString(SchemDB.COL_CLASE_NOMBRE);
                int aforo = resultSet.getInt(SchemDB.COL_CLASE_AFORO);
                //AQui hemos parseado directamente estado a estado porque Postgree nos devuelve un String.
                EstadoClase estado = EstadoClase.valueOf(resultSet.getString(SchemDB.COL_CLASE_ESTADO).trim().toUpperCase());
                //Y esto es lo mismo pero en 2 pasos primero geteamos el String y luego lo parseamos Estadoclase
                //String estadoString = resultSet.getString(SchemDB.COL_CLASE_ESTADO);
                //EstadoClase estado = EstadoClase.valueOf(estadoString.trim().toUpperCase());

                listaClases.add(new Clase(id, estado, nombre, aforo));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        }
        return listaClases;
    }
    @Override
    public Clase selectById(int id) {
        /* BÚSQUEDA SIMPLE POR PRIMARY KEY
         *
         * Al ser una tabla plana (sin herencia), la consulta es directa.
         * Buscamos la fila cuyo ID coincida con el número que pasamos por parámetro.
         *
         *
         * Next Level (Optimización): En tablas de catálogo (que cambian muy poco,
         *    como la lista de clases), a veces en lugar de ir a la BD cada vez,
         *    se carga la lista entera una sola vez al arrancar el programa y se
         *    guarda en un 'HashMap' en la RAM (Caché). Así el 'selectById'
         *    sería instantáneo sin tocar SQL. Si la tabla fuera enorme no merece la pena. Pero para una tabla de este tamaño si.
         *    Tambien importante , si esta clase fuera muy dinamica tampoco se puede cargar en el HashMap porque no estaria actualizada.
         * ========================================================================== */
        Clase clase = null;

        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                SchemDB.TAB_CLASE, SchemDB.COL_CLASE_ID);

        try {
            // Preparamos la consulta para evitar Inyección SQL
            preparedStatement = connection.prepareStatement(query);

            // Sustituimos la interrogación por el ID que queremos buscar
            preparedStatement.setInt(1, id);

            // Ejecutamos la lectura
            resultSet = preparedStatement.executeQuery();

            // Usamos 'if' porque es imposible que haya dos clases con el mismo ID
            if (resultSet.next()) {
                // 1. Creamos la "caja vacía" gracias a @NoArgsConstructor de Lombok
                clase = new Clase();

                // 2. Rellenamos todos los atributos uno a uno usando los Setters
                clase.setIdClase(resultSet.getInt(SchemDB.COL_CLASE_ID));
                clase.setNombre(resultSet.getString(SchemDB.COL_CLASE_NOMBRE));
                clase.setAforoMax(resultSet.getInt(SchemDB.COL_CLASE_AFORO));
                // Parseamos el String de la BD a nuestro Enum de Java // Si queremos simplificar podemos odviar esta linea y que no nos devuelva el estado, pero voy a traerlo entero.
                clase.setEstado(EstadoClase.valueOf(resultSet.getString(SchemDB.COL_CLASE_ESTADO).trim().toUpperCase()));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectById clase): " + e.getMessage());
        }
        // Devolverá el objeto si lo encontró, o 'null' si ese ID no existe en el catálogo
        return clase;
    }

    @Override
    public int update(Clase clase) {
        String query = String.format("UPDATE %s SET %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_CLASE, SchemDB.COL_CLASE_NOMBRE, SchemDB.COL_CLASE_AFORO,
                SchemDB.COL_CLASE_ESTADO, SchemDB.COL_CLASE_ID);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos actualizados
            preparedStatement.setString(1, clase.getNombre());
            preparedStatement.setInt(2, clase.getAforoMax());
            preparedStatement.setString(3, clase.getEstado().name());
            preparedStatement.setInt(4, clase.getIdClase());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (update): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int actualizarEstado(int id, EstadoClase nuevoEstado) {
        String query = String.format("UPDATE %s SET %s=? WHERE %s=?",
                SchemDB.TAB_CLASE, SchemDB.COL_CLASE_ESTADO, SchemDB.COL_CLASE_ID);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos el nuevo estado y el ID
            preparedStatement.setString(1, nuevoEstado.name());
            preparedStatement.setInt(2, id);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (actualizarEstado): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        String query = String.format("DELETE FROM %s WHERE %s=?",
                SchemDB.TAB_CLASE, SchemDB.COL_CLASE_ID);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos el ID para el borrado físico
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (delete): " + e.getMessage());
        }
        return -1;
    }
}