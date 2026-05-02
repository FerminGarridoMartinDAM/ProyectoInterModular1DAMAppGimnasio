package dao;

import database.ConexionDB;
import database.SchemDB;
import model.Socio;
import model.enums.EstadoUsuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SocioDAOPostgresImpl implements SocioDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public SocioDAOPostgresImpl() {
        connection = ConexionDB.getConexion();
    }

    @Override
    public int insert(Socio socio) {
        // Consultas para ambas tablas
        String queryUsuario = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ESTADO, SchemDB.COL_USUARIO_NOMBRE,
                SchemDB.COL_USUARIO_APELLIDO, SchemDB.COL_USUARIO_EMAIL,
                SchemDB.COL_USUARIO_PASSWORD, SchemDB.COL_USUARIO_TELEFONO);

        String querySocio = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                SchemDB.TAB_SOCIO, SchemDB.COL_USUARIO_ID, SchemDB.COL_SOCIO_FECHA_ALTA);

        try {
            // Iniciamos una Transacción manual luego lo tenemos que activar otra vez.
            connection.setAutoCommit(false);

            // 1. Preparamos el insert del Usuario pidiendo que nos devuelva el ID generado
            preparedStatement = connection.prepareStatement(queryUsuario, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, socio.getEstado().name());
            preparedStatement.setString(2, socio.getNombre());
            preparedStatement.setString(3, socio.getApellido());
            preparedStatement.setString(4, socio.getEmail());
            preparedStatement.setString(5, socio.getPassword());
            preparedStatement.setString(6, socio.getTelefono());

            int filasUsuario = preparedStatement.executeUpdate();

            // 2. Recuperamos el ID que la base de datos le acaba de asignar al usuario
            //El ResultSet siempre empieza apuntando a "la nada" (antes de la primera fila).
            // Al hacer .next(), movemos el cursor a la primera fila donde está nuestro ID.
            //Usamos un if en lugar de un while porque sabemos que solo hemos insertado un usuario, así que solo hay un ID que leer.
            resultSet = preparedStatement.getGeneratedKeys();
            int idGenerado = -1; // Como las bases de datos empiezan por 1 empezamos en -1 por seguridad.
            if (resultSet.next()) {
                idGenerado = resultSet.getInt(1); // Le estamos diciendo: "De esta fila, dame el número entero que hay en la columna 1". Esa primera columna contiene el preciado id_usuario.
            }

            // Si el usuario se insertó bien y tenemos el ID, insertamos el Socio
            if (filasUsuario > 0 && idGenerado != -1) {

                // Actualizamos el objeto Java con su nuevo ID
                socio.setIdUsuario(idGenerado);

                // Preparamos el insert del Socio usando el ID que acabamos de obtener
                preparedStatement = connection.prepareStatement(querySocio);
                preparedStatement.setInt(1, idGenerado);
                preparedStatement.setDate(2, Date.valueOf(socio.getFechaAlta())); // Conversión LocalDate a Date SQL

                preparedStatement.executeUpdate();

                //  Confirmamos que TODO ha ido bien y guardamos en la base de datos definitivamente
                connection.commit();
                return 1;

            } else {
                // FALLO CONTROLADO (El Control+Z): Si el usuario se insertó en el Paso 1, pero no pudimos recuperar su ID,
                // no podemos hacer el Paso 2. Para no dejar un "Usuario fantasma" en la BD, deshacemos el Paso 1.
                connection.rollback();
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("❌ ERROR (insert socio): " + e.getMessage());
            try {
                // Si se cae la conexión o hay un error de sintaxis en medio del proceso,
                // el bloque catch lo atrapa y manda la orden de deshacer cualquier cambio a medias.
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ ERROR crítico deshaciendo cambios: " + ex.getMessage());
            }
        } finally {
            try {
                // Restauramos el commit automático (Autocommit = true).
                // Esto es vital porque compartimos la misma conexión (Singleton) con otros DAOs.
                // Si la dejamos en false, los inserts de otras clases se quedarían "en el aire" sin guardarse.
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public List<Socio> selectAll() {
        List<Socio> listaSocios = new ArrayList<>();

         /*  INNER JOIN
         En lugar de hacer una consulta a 'socio' y luego lanzar un 'SELECT * FROM usuario'
         dentro de un bucle por cada socio que encontremos ,
         usamos INNER JOIN. Esto le pide a la base de datos que pegue ambas tablas temporalmente
         donde coincidan los IDs, y nos devuelva una "súper tabla" con todos los datos de golpe.*/

        String query = String.format("SELECT * FROM %s u INNER JOIN %s s ON u.%s = s.%s",
                SchemDB.TAB_USUARIO, SchemDB.TAB_SOCIO,
                SchemDB.COL_USUARIO_ID, SchemDB.COL_USUARIO_ID);

        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                //  Recogemos los atributos genéricos del padre (tabla usuario)
                int id = resultSet.getInt(SchemDB.COL_USUARIO_ID);


                // String estadoStr = resultSet.getString(SchemDB.COL_USUARIO_ESTADO);
                EstadoUsuario estado = EstadoUsuario.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase()); // Parseo directamente aqui , me resulta mas facil de ver que luego al crear socio y añadirlo a lista.
                String nombre = resultSet.getString(SchemDB.COL_USUARIO_NOMBRE);
                String apellido = resultSet.getString(SchemDB.COL_USUARIO_APELLIDO);
                String email = resultSet.getString(SchemDB.COL_USUARIO_EMAIL);
                String password = resultSet.getString(SchemDB.COL_USUARIO_PASSWORD);
                String telefono = resultSet.getString(SchemDB.COL_USUARIO_TELEFONO);

                //  Recogemos el atributo específico del hijo (tabla socio)
                // Usamos toLocalDate() como "adaptador" entre el Date de SQL y nuestro modelo en Java
                LocalDate fechaAlta = resultSet.getDate(SchemDB.COL_SOCIO_FECHA_ALTA).toLocalDate();

                //  Ensamblamos el objeto usando el constructor manual con super() que creamos en la clase Socio.
                // Este constructor se encarga de repartir los datos internamente entre la clase Padre e Hija.
                listaSocios.add(new Socio(id, estado, nombre, apellido, email, password, telefono, fechaAlta));
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectAll socios): " + e.getMessage());
        }
        return listaSocios;
    }

    @Override
    public Socio selectById(int id) {
        Socio socioEncontrado = null;

        /* GUÍA DE ESTUDIO: SELECT BY ID CON HERENCIA
         *  Hacemos el mismo INNER JOIN que en selectAll para juntar las dos tablas.
         *  Añadimos la cláusula WHERE apuntando a la Clave Primaria (u.id_usuario = ?).
         * Al buscar por Clave Primaria, garantizamos que el resultado será 1 fila o ninguna.
         */
        String query = String.format("SELECT * FROM %s u INNER JOIN %s s ON u.%s = s.%s WHERE u.%s = ?",
                SchemDB.TAB_USUARIO, SchemDB.TAB_SOCIO,
                SchemDB.COL_USUARIO_ID, SchemDB.COL_USUARIO_ID,
                SchemDB.COL_USUARIO_ID);

        try {
            preparedStatement = connection.prepareStatement(query);
            // Inyectamos el ID que queremos buscar
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            // Usamos 'if' en lugar de 'while'. Como buscamos por ID, si hay coincidencia
            // solo habrá un resultado. No hace falta un bucle.
            if (resultSet.next()) {

                // --- DATOS DEL PADRE (Usuario) ---
                EstadoUsuario estado = EstadoUsuario.valueOf(resultSet.getString(SchemDB.COL_USUARIO_ESTADO).trim().toUpperCase());
                String nombre = resultSet.getString(SchemDB.COL_USUARIO_NOMBRE);
                String apellido = resultSet.getString(SchemDB.COL_USUARIO_APELLIDO);
                String email = resultSet.getString(SchemDB.COL_USUARIO_EMAIL);
                String password = resultSet.getString(SchemDB.COL_USUARIO_PASSWORD);
                String telefono = resultSet.getString(SchemDB.COL_USUARIO_TELEFONO);

                // --- DATOS DEL HIJO (Socio) ---
                LocalDate fechaAlta = resultSet.getDate(SchemDB.COL_SOCIO_FECHA_ALTA).toLocalDate();

                // Construimos el objeto completo y lo guardamos en nuestra variable
                socioEncontrado = new Socio(id, estado, nombre, apellido, email, password, telefono, fechaAlta);
            }
        } catch (SQLException e) {
            System.out.println("❌ ERROR (selectById socio): " + e.getMessage());
        }

        // Si lo encuentra, devuelve el objeto lleno. Si no existe ese ID, devuelve null.
        return socioEncontrado;
    }


    @Override
    public int update(Socio socio) {
        // Para actualizar también necesitamos preparar dos consultas porque los datos están divididos
        String queryUsuario = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?",
                SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ESTADO, SchemDB.COL_USUARIO_NOMBRE,
                SchemDB.COL_USUARIO_APELLIDO, SchemDB.COL_USUARIO_EMAIL,
                SchemDB.COL_USUARIO_PASSWORD, SchemDB.COL_USUARIO_TELEFONO, SchemDB.COL_USUARIO_ID);

        String querySocio = String.format("UPDATE %s SET %s=? WHERE %s=?",
                SchemDB.TAB_SOCIO, SchemDB.COL_SOCIO_FECHA_ALTA, SchemDB.COL_USUARIO_ID);

        try {
            // INICIO TRANSACCIÓN
            connection.setAutoCommit(false);

            // 1. Cargamos y ejecutamos la actualización del Padre
            preparedStatement = connection.prepareStatement(queryUsuario);
            preparedStatement.setString(1, socio.getEstado().name());
            preparedStatement.setString(2, socio.getNombre());
            preparedStatement.setString(3, socio.getApellido());
            preparedStatement.setString(4, socio.getEmail());
            preparedStatement.setString(5, socio.getPassword());
            preparedStatement.setString(6, socio.getTelefono());
            preparedStatement.setInt(7, socio.getIdUsuario()); // El WHERE para saber a quién actualizamos
            preparedStatement.executeUpdate();

            // 2. Cargamos y ejecutamos la actualización del Hijo
            preparedStatement = connection.prepareStatement(querySocio);
            preparedStatement.setDate(1, Date.valueOf(socio.getFechaAlta()));
            preparedStatement.setInt(2, socio.getIdUsuario()); // El mismo WHERE
            preparedStatement.executeUpdate();

            // Si llegamos a esta línea sin errores, guardamos los dos Updates
            connection.commit();
            return 1;

        } catch (SQLException e) {
            System.out.println("❌ ERROR (update socio): " + e.getMessage());
            try {
                // Si el Update de Socio falla (ej. formato de fecha inválido), deshacemos el Update de Usuario
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
            }
        } finally {
            try {
                // Volvemos a dejar la conexión en su estado habitual
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
        return -1;
    }


    @Override
    public int delete(int id) {
        // EL ORDEN ES VITAL EN BASES DE DATOS RELACIONALES: Igual en las tablas secretario y entrenador
        // La tabla 'secretario' tiene una Foreign Key que depende de la tabla 'usuario'.
        //Error común: Intentar borrar primero al padre. La BD te lanzará un error de "violación de clave foránea"
        // porque no puedes borrar un padre si tiene hijos dependiendo de él.
        //Solución: Primero borramos al hijo dependiente (Socio), y cuando el padre quede liberado, lo borramos a él (Usuario).

        String querySocio = String.format("DELETE FROM %s WHERE %s=?", SchemDB.TAB_SOCIO, SchemDB.COL_USUARIO_ID);
        String queryUsuario = String.format("DELETE FROM %s WHERE %s=?", SchemDB.TAB_USUARIO, SchemDB.COL_USUARIO_ID);

        try {
            // INICIO TRANSACCIÓN
            connection.setAutoCommit(false);

            // Borramos primero al hijo (Socio)
            preparedStatement = connection.prepareStatement(querySocio);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            // Ahora que el padre no tiene ataduras, lo borramos (Usuario)
            preparedStatement = connection.prepareStatement(queryUsuario);
            preparedStatement.setInt(1, id);
            int filas = preparedStatement.executeUpdate(); // Guardamos cuántas filas se han borrado para el return

            // TODO OK: Confirmamos la eliminación de ambos
            connection.commit();
            return filas;

        } catch (SQLException e) {
            System.out.println("❌ ERROR (delete socio): " + e.getMessage());
            try {
                // Si borramos al hijo, pero falla el borrado del padre, hacemos Control+Z y el hijo "resucita"
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
            }
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
        return -1;


    }
}