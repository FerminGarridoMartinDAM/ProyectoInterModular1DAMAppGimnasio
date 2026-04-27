package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Entrenador;
import model.enums.EstadoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDAOPostgresImpl implements EntrenadorDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public EntrenadorDAOPostgresImpl() {
        connection = ConexionDB.conectar();
    }

    @Override
    public int insert(Entrenador entrenador) {
        String queryUsuario = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ESTADO, SchemDB.COL_USUARIO_NOMBRE,
                SchemDB.COL_USUARIO_APELLIDO, SchemDB.COL_USUARIO_EMAIL,
                SchemDB.COL_USUARIO_PASSWORD, SchemDB.COL_USUARIO_TELEFONO);

        String queryEntrenador = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                SchemDB.TAB_ENTRENADOR, SchemDB.COL_USUARIO_ID, SchemDB.COL_ENTRENADOR_ESPECIALIDAD);

        try {
            connection.setAutoCommit(false); // Iniciamos transacción

            // 1. Insertar en la tabla madre (Usuario)
            preparedStatement = connection.prepareStatement(queryUsuario, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entrenador.getEstado().name());
            preparedStatement.setString(2, entrenador.getNombre());
            preparedStatement.setString(3, entrenador.getApellido());
            preparedStatement.setString(4, entrenador.getEmail());
            preparedStatement.setString(5, entrenador.getPassword());
            preparedStatement.setString(6, entrenador.getTelefono());

            int filasUsuario = preparedStatement.executeUpdate();

            // 2. Recuperar el ID generado por la base de datos
            resultSet = preparedStatement.getGeneratedKeys();
            int idGenerado = -1;
            if (resultSet.next()) {
                idGenerado = resultSet.getInt(1);
            }

            if (filasUsuario > 0 && idGenerado != -1) {
                entrenador.setIdUsuario(idGenerado);

                // 3. Insertar en la tabla hija (Entrenador) usando el ID de la madre
                preparedStatement = connection.prepareStatement(queryEntrenador);
                preparedStatement.setInt(1, idGenerado);
                preparedStatement.setString(2, entrenador.getEspecialidad());

                preparedStatement.executeUpdate();

                connection.commit(); // Todo OK, guardamos
                return 1;
            } else {
                connection.rollback(); // Algo falló, deshacemos
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert entrenador): " + e.getMessage());
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { }
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (SQLException e) { }
        }
        return -1;
    }

    @Override
    public List<Entrenador> selectAll() {
        List<Entrenador> listaEntrenadores = new ArrayList<>();
        // JOIN para obtener los datos de ambas tablas en una sola consulta
        String query = String.format("SELECT * FROM %s u INNER JOIN %s e ON u.%s = e.%s",
                SchemDB.TAB_USUARIO, SchemDB.TAB_ENTRENADOR,
                SchemDB.COL_USUARIO_ID, SchemDB.COL_USUARIO_ID);

        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(SchemDB.COL_USUARIO_ID);


                // String estadoStr = resultSet.getString(SchemDB.COL_USUARIO_ESTADO); Lo dejo comentado para que se vea en todas las clases el parseo.
                EstadoUsuario estado = EstadoUsuario.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase());
                String nombre = resultSet.getString(SchemDB.COL_USUARIO_NOMBRE);
                String apellido = resultSet.getString(SchemDB.COL_USUARIO_APELLIDO);
                String email = resultSet.getString(SchemDB.COL_USUARIO_EMAIL);
                String password = resultSet.getString(SchemDB.COL_USUARIO_PASSWORD);
                String telefono = resultSet.getString(SchemDB.COL_USUARIO_TELEFONO);
                String especialidad = resultSet.getString(SchemDB.COL_ENTRENADOR_ESPECIALIDAD);

                // Aquí usamos el constructor manual que debes crear en model.Entrenador
                listaEntrenadores.add(new Entrenador(id, estado, nombre, apellido, email, password, telefono, especialidad));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll entrenadores): " + e.getMessage());
        }
        return listaEntrenadores;
    }

    @Override
    public int update(Entrenador entrenador) {
        String queryUsuario = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ESTADO, SchemDB.COL_USUARIO_NOMBRE,
                SchemDB.COL_USUARIO_APELLIDO, SchemDB.COL_USUARIO_EMAIL,
                SchemDB.COL_USUARIO_PASSWORD, SchemDB.COL_USUARIO_TELEFONO, SchemDB.COL_USUARIO_ID);

        String queryEntrenador = String.format("UPDATE %s SET %s=? WHERE %s=?",
                SchemDB.TAB_ENTRENADOR, SchemDB.COL_ENTRENADOR_ESPECIALIDAD, SchemDB.COL_USUARIO_ID);

        try {
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(queryUsuario);
            preparedStatement.setString(1, entrenador.getEstado().name());
            preparedStatement.setString(2, entrenador.getNombre());
            preparedStatement.setString(3, entrenador.getApellido());
            preparedStatement.setString(4, entrenador.getEmail());
            preparedStatement.setString(5, entrenador.getPassword());
            preparedStatement.setString(6, entrenador.getTelefono());
            preparedStatement.setInt(7, entrenador.getIdUsuario());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(queryEntrenador);
            preparedStatement.setString(1, entrenador.getEspecialidad());
            preparedStatement.setInt(2, entrenador.getIdUsuario());
            preparedStatement.executeUpdate();

            connection.commit();
            return 1;
        } catch (SQLException e) {
            System.out.println("❌ ERROR (update entrenador): " + e.getMessage());
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { }
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (SQLException e) { }
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        // Borramos en cascada manual (primero hija, luego madre)
        String queryEntrenador = String.format("DELETE FROM %s WHERE %s=?", SchemDB.TAB_ENTRENADOR, SchemDB.COL_USUARIO_ID);
        String queryUsuario = String.format("DELETE FROM %s WHERE %s=?", SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ID);

        try {
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(queryEntrenador);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(queryUsuario);
            preparedStatement.setInt(1, id);
            int filas = preparedStatement.executeUpdate();

            connection.commit();
            return filas;
        } catch (SQLException e) {
            System.out.println("❌ ERROR (delete entrenador): " + e.getMessage());
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { }
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (SQLException e) { }
        }
        return -1;
    }
}