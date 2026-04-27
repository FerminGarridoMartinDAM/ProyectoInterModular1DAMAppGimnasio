package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Plan;
import model.enums.EstadoPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDAOPostgresImpl implements PlanDAO {

    @Override
    public boolean insert(Plan plan) {
        String sql = "INSERT INTO " + SchemDB.TAB_PLAN + " (" +
                SchemDB.COL_PLAN_NOMBRE + ", " +
                SchemDB.COL_PLAN_PRECIO + ", " +
                SchemDB.COL_PLAN_ESTADO + ") VALUES (?, ?, ?)";

        Connection conexion = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            // Pedimos la conexión al Singleton
            conexion = ConexionDB.conectar();

            // Preparamos la consulta
            pstmt = conexion.prepareStatement(sql);

            // Mapeamos los datos del objeto al SQL
            pstmt.setString(1, plan.getNombre());
            pstmt.setDouble(2, plan.getPrecioMensual());
            pstmt.setString(3, plan.getEstado().name());

            int filas = pstmt.executeUpdate();
            exito = filas > 0;

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert): " + e.getMessage());
        } finally {
            // Limpiamos el PreparedStatement pero dejamos la conexión abierta
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    @Override
    public List<Plan> selectAll() {
        List<Plan> listaPlanes = new ArrayList<>();
        String sql = "SELECT * FROM " + SchemDB.TAB_PLAN;

        Connection conexion = null;
        PreparedStatement pstmt = null;
        ResultSet resulSet = null;

        try {
            conexion = ConexionDB.conectar();
            pstmt = conexion.prepareStatement(sql);

            // Ejecutamos la consulta de lectura
            resulSet = pstmt.executeQuery();

            // Recorremos los resultados
            while (resulSet.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(resulSet.getInt(SchemDB.COL_PLAN_ID));
                plan.setNombre(resulSet.getString(SchemDB.COL_PLAN_NOMBRE));
                plan.setPrecioMensual(resulSet.getDouble(SchemDB.COL_PLAN_PRECIO));

                // Convertimos el String de la BD de vuelta al Enum de Java
                plan.setEstado(EstadoPlan.valueOf(resulSet.getString(SchemDB.COL_PLAN_ESTADO)));

                listaPlanes.add(plan);
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        } finally {
            try { if (resulSet != null) resulSet.close(); if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return listaPlanes;
    }

    @Override
    public boolean update(Plan plan) {
        String sql = "UPDATE " + SchemDB.TAB_PLAN + " SET " +
                SchemDB.COL_PLAN_NOMBRE + "=?, " +
                SchemDB.COL_PLAN_PRECIO + "=?, " +
                SchemDB.COL_PLAN_ESTADO + "=? WHERE " + SchemDB.COL_PLAN_ID + "=?";

        Connection conexion = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            conexion = ConexionDB.conectar();
            pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, plan.getNombre());
            pstmt.setDouble(2, plan.getPrecioMensual());
            pstmt.setString(3, plan.getEstado().name());
            pstmt.setInt(4, plan.getIdPlan());

            exito = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ ERROR (update): " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    @Override
    public boolean actualizarEstado(int id, EstadoPlan nuevoEstado) {
        // Actualiza únicamente la columna estado (ideal para borrados lógicos o reactivaciones)
        String sql = "UPDATE " + SchemDB.TAB_PLAN + " SET " + SchemDB.COL_PLAN_ESTADO + "=? WHERE " + SchemDB.COL_PLAN_ID + "=?";

        Connection conexion = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            conexion = ConexionDB.conectar();
            pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, nuevoEstado.name());
            pstmt.setInt(2, id);

            exito = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ ERROR (actualizarEstado): " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }

    @Override
    public boolean delete(int id) {
        // Borrado físico y permanente de la base de datos
        String sql = "DELETE FROM " + SchemDB.TAB_PLAN + " WHERE " + SchemDB.COL_PLAN_ID + "=?";

        Connection conexion = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            conexion = ConexionDB.conectar();
            pstmt = conexion.prepareStatement(sql);
            pstmt.setInt(1, id);

            exito = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ ERROR (delete): " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return exito;
    }
}