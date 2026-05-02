package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Plan;
import model.enums.EstadoPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDAOPostgresImpl implements PlanDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public PlanDAOPostgresImpl() {
        // Pedimos la conexión al Singleton al instanciar el DAO
        connection = ConexionDB.getConexion();
    }

    @Override
    public int insert(Plan plan) {
        String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                SchemDB.TAB_PLAN, SchemDB.COL_PLAN_NOMBRE, SchemDB.COL_PLAN_PRECIO, SchemDB.COL_PLAN_ESTADO);

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos del objeto al SQL
            preparedStatement.setString(1, plan.getNombre());
            preparedStatement.setDouble(2, plan.getPrecioMensual());
            preparedStatement.setString(3, plan.getEstado().name());

            // Ejecutamos y retornamos el número de filas afectadas
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Plan> selectAll() {
        List<Plan> listaPlanes = new ArrayList<>();
        String query = "SELECT * FROM " + SchemDB.TAB_PLAN;

        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Ejecutamos la consulta de lectura
            resultSet = preparedStatement.executeQuery();

            // Recorremos los resultados mapeando cada columna
            while (resultSet.next()) {
                int id = resultSet.getInt(SchemDB.COL_PLAN_ID);
                String nombre = resultSet.getString(SchemDB.COL_PLAN_NOMBRE);
                double precio = resultSet.getDouble(SchemDB.COL_PLAN_PRECIO);
                EstadoPlan estado = EstadoPlan.valueOf(resultSet.getString(SchemDB.COL_PLAN_ESTADO).trim().toUpperCase());
             //En claseDAOPostgres selectALL esta explicado este parseo por si lo quiero revisar

                listaPlanes.add(new Plan(id, estado, nombre, precio));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        }
        return listaPlanes;
    }

    @Override
    public Plan selectById(int id) {
        Plan planEncontrado = null;
        /* - Usamos el constructor vacío de Lombok y rellenamos por Setters
         * El BigDecimal: usamos 'getDouble' para el precio. los double a veces tienen fallos
         *    de redondeo (ej. 2.999999999). Si este software fuera para un banco
         *    o facturación avanzada, usaríamos 'resultSet.getBigDecimal()' y la
         *    clase 'java.math.BigDecimal' en nuestro modelo en lugar de 'double'.
        */

        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                SchemDB.TAB_PLAN, SchemDB.COL_PLAN_ID);
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                planEncontrado = new Plan();
                //  Rellenamos datos numéricos y de texto
                planEncontrado.setIdPlan(resultSet.getInt(SchemDB.COL_PLAN_ID));
                planEncontrado.setNombre(resultSet.getString(SchemDB.COL_PLAN_NOMBRE));
                planEncontrado.setPrecioMensual(resultSet.getDouble(SchemDB.COL_PLAN_PRECIO));
                // Parseamos el Enum asegurándonos de limpiar el String
                planEncontrado.setEstado(EstadoPlan.valueOf(resultSet.getString(SchemDB.COL_PLAN_ESTADO).trim().toUpperCase()));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectById plan): " + e.getMessage());
        }

        return planEncontrado;
    }

    @Override
    public int update(Plan plan) {
        String query = String.format("UPDATE %s SET %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_PLAN, SchemDB.COL_PLAN_NOMBRE, SchemDB.COL_PLAN_PRECIO,
                SchemDB.COL_PLAN_ESTADO, SchemDB.COL_PLAN_ID);
        try {
            // Preparamos la consulta
            preparedStatement = connection.prepareStatement(query);

            // Mapeamos los datos actualizados
            preparedStatement.setString(1, plan.getNombre());
            preparedStatement.setDouble(2, plan.getPrecioMensual());
            preparedStatement.setString(3, plan.getEstado().name());
            preparedStatement.setInt(4, plan.getIdPlan());

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ ERROR (update): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int actualizarEstado(int id, EstadoPlan nuevoEstado) {
        String query = String.format("UPDATE %s SET %s=? WHERE %s=?",
                SchemDB.TAB_PLAN, SchemDB.COL_PLAN_ESTADO, SchemDB.COL_PLAN_ID);
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
                SchemDB.TAB_PLAN, SchemDB.COL_PLAN_ID);
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