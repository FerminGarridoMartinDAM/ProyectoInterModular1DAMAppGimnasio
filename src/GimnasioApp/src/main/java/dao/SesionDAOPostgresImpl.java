package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Sesion;
import model.enums.EstadoSesion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SesionDAOPostgresImpl implements SesionDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public SesionDAOPostgresImpl() {
        // Pedimos la conexión al Singleton al instanciar el DAO
        connection = ConexionDB.getConexion();
    }

    @Override
    public int insert(Sesion sesion) {
        String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                SchemDB.TAB_SESION,
                SchemDB.COL_SESION_ENTRENADOR, SchemDB.COL_SESION_CLASE,
                SchemDB.COL_SESION_ESTADO, SchemDB.COL_SESION_SALA,
                SchemDB.COL_SESION_INICIO, SchemDB.COL_SESION_FIN);

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos del objeto al SQL
            preparedStatement.setInt(1, sesion.getIdEntrenador());
            preparedStatement.setInt(2, sesion.getIdClase());
            preparedStatement.setString(3, sesion.getEstado().name());
            preparedStatement.setString(4, sesion.getSala());

            // Convertimos LocalDateTime de Java a Timestamp de SQL porque Supabase no entiende LocalDateTime
            //Para este programa podia haberlo hecho directamente en el model Sesion ,por ejemplo en el getter,  pero para seguir buenas practicas y dejar el model limpoio lo he hecho aqui. Lo dejo comentado en el model.
            preparedStatement.setTimestamp(5, Timestamp.valueOf(sesion.getInicio()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(sesion.getFin()));

            // Ejecutamos y retornamos el número de filas afectadas
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Sesion> selectAll() {
        List<Sesion> listaSesiones = new ArrayList<>();
        String query = "SELECT * FROM " + SchemDB.TAB_SESION;

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Ejecutamos la consulta de lectura
            resultSet = preparedStatement.executeQuery();

            // Recorremos los resultados mapeando cada columna
            while (resultSet.next()) {
                int id = resultSet.getInt(SchemDB.COL_SESION_ID);
                int idEntrenador = resultSet.getInt(SchemDB.COL_SESION_ENTRENADOR);
                int idClase = resultSet.getInt(SchemDB.COL_SESION_CLASE);
                EstadoSesion estado = EstadoSesion.valueOf(resultSet.getString(SchemDB.COL_SESION_ESTADO).trim().toUpperCase());
               // String estadoStr = resultSet.getString(SchemDB.COL_SESION_ESTADO); El mismo parseo que en las otras clases
                String sala = resultSet.getString(SchemDB.COL_SESION_SALA);

                // Convertimos el Timestamp que nos da Supabase de vuelta al LocalDateTime que usa Java

                LocalDateTime inicio = resultSet.getTimestamp(SchemDB.COL_SESION_INICIO).toLocalDateTime();
                LocalDateTime fin = resultSet.getTimestamp(SchemDB.COL_SESION_FIN).toLocalDateTime();

                listaSesiones.add(new Sesion(id, idEntrenador, idClase, estado, sala, inicio, fin));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        }
        return listaSesiones;
    }

    @Override
    public int update(Sesion sesion) {
        String query = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_SESION,
                SchemDB.COL_SESION_ENTRENADOR, SchemDB.COL_SESION_CLASE,
                SchemDB.COL_SESION_ESTADO, SchemDB.COL_SESION_SALA,
                SchemDB.COL_SESION_INICIO, SchemDB.COL_SESION_FIN,
                SchemDB.COL_SESION_ID);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos actualizados convirtiendo de nuevo a Timestamp para la BD
            preparedStatement.setInt(1, sesion.getIdEntrenador());
            preparedStatement.setInt(2, sesion.getIdClase());
            preparedStatement.setString(3, sesion.getEstado().name());
            preparedStatement.setString(4, sesion.getSala());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(sesion.getInicio()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(sesion.getFin()));
            preparedStatement.setInt(7, sesion.getIdSesion());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (update): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        String query = String.format("DELETE FROM %s WHERE %s=?",
                SchemDB.TAB_SESION, SchemDB.COL_SESION_ID);
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