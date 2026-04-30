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
                EstadoClase estado = EstadoClase.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase());
                //String estadoStr = resultSet.getString(SchemDB.COL_CLASE_ESTADO);

                listaClases.add(new Clase(id, estado, nombre, aforo));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll): " + e.getMessage());
        }
        return listaClases;
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