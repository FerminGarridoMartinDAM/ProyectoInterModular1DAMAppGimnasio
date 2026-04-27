package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Entrenador;
import model.Secretario;
import model.Socio;
import model.Usuario;
import model.enums.EstadoUsuario;

import java.sql.*;
import java.time.LocalDate;
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
        // Comprobamos si las credenciales existen en la tabla madre
        String queryUsuario = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_EMAIL, SchemDB.COL_USUARIO_PASSWORD);

        try {
            preparedStatement = connection.prepareStatement(queryUsuario);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Extraemos los datos  del usuario para devolver el objeto Usuario que ha iniciado sesión
                int id = resultSet.getInt(SchemDB.COL_USUARIO_ID);
                EstadoUsuario estado = EstadoUsuario.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase());
                String nombre = resultSet.getString(SchemDB.COL_USUARIO_NOMBRE);
                String apellido = resultSet.getString(SchemDB.COL_USUARIO_APELLIDO);
                String tel = resultSet.getString(SchemDB.COL_USUARIO_TELEFONO);

                //TODO esto hay que hacerlo porque la clase Usuario es abstract


                // Ahora que sabemos que existe y tenemos su ID, averiguamos qué "hijo" es. (Polimorfismo)

                // ¿Es un Socio? buscamos su id el la tabla socio
                String querySocio = String.format("SELECT %s FROM %s WHERE %s = ?",
                        SchemDB.COL_SOCIO_FECHA_ALTA, SchemDB.TAB_SOCIO, SchemDB.COL_USUARIO_ID);
                PreparedStatement psSocio = connection.prepareStatement(querySocio);
                psSocio.setInt(1, id);
                ResultSet rsSocio = psSocio.executeQuery();

                if (rsSocio.next()) {
                    // Si taba en la tabla socio. Cogemos su fecha y creamos el objeto socio.
                    LocalDate fechaAlta = rsSocio.getDate(SchemDB.COL_SOCIO_FECHA_ALTA).toLocalDate();
                    return new Socio(id, estado, nombre, apellido, email, password, tel, fechaAlta);
                }

                // ¿Es un Entrenador? buscamos su id el la tabla entrenador
                String queryEntrenador = String.format("SELECT %s FROM %s WHERE %s = ?",
                        SchemDB.COL_ENTRENADOR_ESPECIALIDAD, SchemDB.TAB_ENTRENADOR, SchemDB.COL_USUARIO_ID);
                PreparedStatement psEntrenador = connection.prepareStatement(queryEntrenador);
                psEntrenador.setInt(1, id);
                ResultSet rsEntrenador = psEntrenador.executeQuery();

                if (rsEntrenador.next()) {
                    String especialidad = rsEntrenador.getString(SchemDB.COL_ENTRENADOR_ESPECIALIDAD);
                    return new Entrenador(id, estado, nombre, apellido, email, password, tel, especialidad);
                }

                // ¿Es un Secretario? buscamos su id el la tabla secretario
                String querySecretario = String.format("SELECT %s FROM %s WHERE %s = ?",
                        SchemDB.COL_SECRETARIO_TURNO, SchemDB.TAB_SECRETARIO, SchemDB.COL_USUARIO_ID);
                PreparedStatement psSecretario = connection.prepareStatement(querySecretario);
                psSecretario.setInt(1, id);
                ResultSet rsSecretario = psSecretario.executeQuery();

                if (rsSecretario.next()) {
                    String turno = rsSecretario.getString(SchemDB.COL_SECRETARIO_TURNO);
                    return new Secretario(id, estado, nombre, apellido, email, password, tel, turno);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (login): " + e.getMessage());
        }

        return null; // Si no está en ninguna tabla o la contraseña es incorrecta
    }

            // Tenia problemas con el selectALL porque no podia crear un new Usuario al ser abstrac , (lo dejo comentado despues)
        // Entonces lo que he hecho es usar los DAOS para usar el selectALL de cada DAO y aladri a listaUsuarios.
    @Override
    public List<Usuario> selectAll() {
        List<Usuario> listaTodosLosUsuarios = new ArrayList<>();


        // Traemos a todos los Socios y los metemos en la lista general
        SocioDAO socioDAO = new SocioDAOPostgresImpl();
        listaTodosLosUsuarios.addAll(socioDAO.selectAll());

        // Traemos a todos los Entrenadores y los metemos en la lista general
        EntrenadorDAO entrenadorDAO = new EntrenadorDAOPostgresImpl();
        listaTodosLosUsuarios.addAll(entrenadorDAO.selectAll());

        // Traemos a todos los Secretarios y los metemos en la lista general
        SecretarioDAO secretarioDAO = new SecretarioDAOPostgresImpl();
        listaTodosLosUsuarios.addAll(secretarioDAO.selectAll());

        // Gracias al Polimorfismo, una List<Usuario> acepta sin problemas objetos de tipo Socio, Entrenador y Secretario.

        return listaTodosLosUsuarios;
    }


   /* @Override
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
    }*/

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