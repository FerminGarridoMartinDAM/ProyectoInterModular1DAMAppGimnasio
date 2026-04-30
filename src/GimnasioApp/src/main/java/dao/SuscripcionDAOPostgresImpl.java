package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Suscripcion;
import model.enums.EstadoSuscripcion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuscripcionDAOPostgresImpl implements SuscripcionDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public SuscripcionDAOPostgresImpl() {
        // Pedimos la conexión al Singleton al instanciar el DAO
        connection = ConexionDB.getConexion();
    }

    @Override
    public int insert(Suscripcion suscripcion) {
        String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                SchemDB.TAB_SUSCRIPCION,
                SchemDB.COL_SUSC_PLAN, SchemDB.COL_SUSC_SOCIO,
                SchemDB.COL_SUSC_ESTADO, SchemDB.COL_SUSC_INICIO, SchemDB.COL_SUSC_FIN);

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos del objeto al SQL usando los nombres de tus variables (idPlan, idSocio, etc)
            preparedStatement.setInt(1, suscripcion.getIdPlan());
            preparedStatement.setInt(2, suscripcion.getIdSocio());
            preparedStatement.setString(3, suscripcion.getEstado().name());

            // CONVERSIÓN: Pasamos de LocalDate (Java) a Date (SQL)
            // Usamos Date.valueOf() porque Java usa LocalDate pero en Supabase/Postgres es un tipo DATE (sin hora)
            preparedStatement.setDate(4, Date.valueOf(suscripcion.getFechaInicio()));
            preparedStatement.setDate(5, Date.valueOf(suscripcion.getFechaFin()));

            // Ejecutamos y retornamos el número de filas afectadas
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Suscripcion> selectAll() {
        List<Suscripcion> listaSuscripciones = new ArrayList<>();
        String query = "SELECT * FROM " + SchemDB.TAB_SUSCRIPCION;

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Ejecutamos la consulta de lectura
            resultSet = preparedStatement.executeQuery();

            // Recorremos los resultados mapeando cada columna
            while (resultSet.next()) {
                int id = resultSet.getInt(SchemDB.COL_SUSC_ID);
                int idPlan = resultSet.getInt(SchemDB.COL_SUSC_PLAN);
                int idSocio = resultSet.getInt(SchemDB.COL_SUSC_SOCIO);
                EstadoSuscripcion estado = EstadoSuscripcion.valueOf(resultSet.getString(SchemDB.COL_SUSC_ESTADO).trim().toUpperCase());
                // String estadoStr = resultSet.getString(SchemDB.COL_SUSC_ESTADO); El mismo parseo que en las otras clasese

                // CONVERSIÓN: Pasamos de Date (SQL) a LocalDate (Java)
                // Usamos toLocalDate() para que nuestro modelo moderno de Java pueda leer el tipo DATE de la base de datos
                LocalDate inicio = resultSet.getDate(SchemDB.COL_SUSC_INICIO).toLocalDate();
                LocalDate fin = resultSet.getDate(SchemDB.COL_SUSC_FIN).toLocalDate();

                listaSuscripciones.add(new Suscripcion(id, idPlan, idSocio, estado, inicio, fin));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        }
        return listaSuscripciones;
    }

    @Override
    public int update(Suscripcion suscripcion) {
        String query = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_SUSCRIPCION,
                SchemDB.COL_SUSC_PLAN, SchemDB.COL_SUSC_SOCIO,
                SchemDB.COL_SUSC_ESTADO, SchemDB.COL_SUSC_INICIO, SchemDB.COL_SUSC_FIN,
                SchemDB.COL_SUSC_ID);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos actualizados convirtiendo a Date para SQL
            preparedStatement.setInt(1, suscripcion.getIdPlan());
            preparedStatement.setInt(2, suscripcion.getIdSocio());
            preparedStatement.setString(3, suscripcion.getEstado().name());
            preparedStatement.setDate(4, Date.valueOf(suscripcion.getFechaInicio()));
            preparedStatement.setDate(5, Date.valueOf(suscripcion.getFechaFin()));
            preparedStatement.setInt(6, suscripcion.getIdSuscripcion());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (update): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        String query = String.format("DELETE FROM %s WHERE %s=?",
                SchemDB.TAB_SUSCRIPCION, SchemDB.COL_SUSC_ID);
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