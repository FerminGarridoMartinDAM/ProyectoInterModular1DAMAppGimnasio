package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Usuario;
import model.enums.EstadoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOPostgresImpl implements UsuarioDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public UsuarioDAOPostgresImpl() {
        // Al igual que en los hijos, pedimos la conexión única al Singleton
        connection = ConexionDB.conectar();
    }

    @Override
    public Usuario login(String email, String password) {
        // Buscamos en la tabla madre si existe la combinación de email y password
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_EMAIL, SchemDB.COL_USUARIO_PASSWORD);

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            // Si el cursor encuentra una fila, las credenciales son correctas
            if (resultSet.next()) {
                // Extraemos los datos para devolver el objeto Usuario que ha iniciado sesión
                int id = resultSet.getInt(SchemDB.COL_USUARIO_ID);

                // Aplicamos tu parseo de Nivel 2: Limpieza y conversión a Enum en una línea
                EstadoUsuario estado = EstadoUsuario.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase());

                String nombre = resultSet.getString(SchemDB.COL_USUARIO_NOMBRE);
                String apellido = resultSet.getString(SchemDB.COL_USUARIO_APELLIDO);
                String tel = resultSet.getString(SchemDB.COL_USUARIO_TELEFONO);

                // Devolvemos un objeto Usuario genérico con los datos de la tabla madre
                return new Usuario(id, estado, nombre, apellido, email, password, tel);
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (login): " + e.getMessage());
        }

        // Si no hay resultados o hay error, devolvemos null (acceso denegado)
        return null;
    }

    @Override
    public List<Usuario> selectAll() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        // Aquí solo miramos la tabla madre para ver todos los usuarios registrados,
        // sin importar si luego son socios, entrenadores o secretarios.
        String query = "SELECT * FROM " + SchemDB.TAB_USUARIO;

        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(SchemDB.COL_USUARIO_ID);
                EstadoUsuario estado = EstadoUsuario.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase());
                String nombre = resultSet.getString(SchemDB.COL_USUARIO_NOMBRE);
                String apellido = resultSet.getString(SchemDB.COL_USUARIO_APELLIDO);
                String email = resultSet.getString(SchemDB.COL_USUARIO_EMAIL);
                String password = resultSet.getString(SchemDB.COL_USUARIO_PASSWORD);
                String telefono = resultSet.getString(SchemDB.COL_USUARIO_TELEFONO);

                listaUsuarios.add(new Usuario(id, estado, nombre, apellido, email, password, telefono));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll usuarios): " + e.getMessage());
        }
        return listaUsuarios;
    }

    @Override
    public int update(Usuario usuario) {
        // Actualización general de los datos que están en la tabla madre
        String query = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ESTADO, SchemDB.COL_USUARIO_NOMBRE,
                SchemDB.COL_USUARIO_APELLIDO, SchemDB.COL_USUARIO_EMAIL,
                SchemDB.COL_USUARIO_PASSWORD, SchemDB.COL_USUARIO_TELEFONO, SchemDB.COL_USUARIO_ID);

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usuario.getEstado().name());
            preparedStatement.setString(2, usuario.getNombre());
            preparedStatement.setString(3, usuario.getApellido());
            preparedStatement.setString(4, usuario.getEmail());
            preparedStatement.setString(5, usuario.getPassword());
            preparedStatement.setString(6, usuario.getTelefono());
            preparedStatement.setInt(7, usuario.getIdUsuario());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ ERROR (update usuario): " + e.getMessage());
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        // ¡OJO! Este delete borrará al usuario de la tabla madre.
        // Si la base de datos está configurada con ON DELETE CASCADE, borrará al socio/entrenador automáticamente.
        // Si no, fallará por restricción de integridad si el usuario tiene un "hijo" en otra tabla.
        String query = String.format("DELETE FROM %s WHERE %s=?", SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ID);

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ ERROR (delete usuario): " + e.getMessage());
        }
        return -1;
    }
}