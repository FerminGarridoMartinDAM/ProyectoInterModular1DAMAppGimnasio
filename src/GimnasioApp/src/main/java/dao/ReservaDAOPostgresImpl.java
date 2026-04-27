package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Reserva;
import model.enums.EstadoReserva;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOPostgresImpl implements ReservaDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public ReservaDAOPostgresImpl() {
        // Pedimos la conexión al Singleton al instanciar el DAO
        connection = ConexionDB.conectar();
    }

    @Override
    public int insert(Reserva reserva) {
        String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                SchemDB.TAB_RESERVA,
                SchemDB.COL_RES_SOCIO, SchemDB.COL_RES_SESION,
                SchemDB.COL_RES_ESTADO, SchemDB.COL_RES_FECHA);

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos del objeto al SQL
            preparedStatement.setInt(1, reserva.getIdSocio());
            preparedStatement.setInt(2, reserva.getIdSesion());
            preparedStatement.setString(3, reserva.getEstado().name());

            // Convertimos LocalDateTime de Java a Timestamp de SQL porque en Supabase no existe LocalDateTime
            // Usamos valueOf para que el driver de la base de datos pueda entender la fecha
            preparedStatement.setTimestamp(4, Timestamp.valueOf(reserva.getFechaReserva()));

            // Ejecutamos y retornamos el número de filas afectadas
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Reserva> selectAll() {
        List<Reserva> listaReservas = new ArrayList<>();
        String query = "SELECT * FROM " + SchemDB.TAB_RESERVA;

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Ejecutamos la consulta de lectura
            resultSet = preparedStatement.executeQuery();

            // Recorremos los resultados mapeando cada columna
            while (resultSet.next()) {
                int idSocio = resultSet.getInt(SchemDB.COL_RES_SOCIO);
                int idSesion = resultSet.getInt(SchemDB.COL_RES_SESION);
                EstadoReserva estado = EstadoReserva.valueOf(resultSet.getString(SchemDB.COL_RES_ESTADO).trim().toUpperCase());
               // String estadoStr = resultSet.getString(SchemDB.COL_RES_ESTADO); CAmbiado por el parseo

                // Convertimos el Timestamp que nos devuelve Supabase al LocalDateTime que usa Java
                // toLocalDateTime() es el adaptador que permite que nuestro modelo lea el tiempo de la BD
                LocalDateTime fecha = resultSet.getTimestamp(SchemDB.COL_RES_FECHA).toLocalDateTime();

                listaReservas.add(new Reserva(idSocio, idSesion,estado, fecha));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        }
        return listaReservas;
    }

    @Override
    public int update(Reserva reserva) {
        // En una tabla de unión, el WHERE suele usar ambos IDs para identificar la fila única
        String query = String.format("UPDATE %s SET %s=? WHERE %s=? AND %s=?",
                SchemDB.TAB_RESERVA, SchemDB.COL_RES_ESTADO,
                SchemDB.COL_RES_SOCIO, SchemDB.COL_RES_SESION);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Actualizamos el estado de la reserva
            preparedStatement.setString(1, reserva.getEstado().name());
            preparedStatement.setInt(2, reserva.getIdSocio());
            preparedStatement.setInt(3, reserva.getIdSesion());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (update): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int delete(int idSocio, int idSesion) {
        String query = String.format("DELETE FROM %s WHERE %s=? AND %s=?",
                SchemDB.TAB_RESERVA, SchemDB.COL_RES_SOCIO, SchemDB.COL_RES_SESION);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos ambos IDs para borrar la reserva específica
            preparedStatement.setInt(1, idSocio);
            preparedStatement.setInt(2, idSesion);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (delete): " + e.getMessage());
        }
        return -1;
    }
}