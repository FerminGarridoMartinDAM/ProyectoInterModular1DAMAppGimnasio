package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.SchemDB;
import dto.GimnasioMapsJsonDTO;
import dto.GimnasioObjetosJavaDTO;
import model.*;
import model.enums.EstadoUsuario;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* ========================================================================================
 * GUÍA DE ESTUDIO: EL MOTOR DE BACKUP (GESTOR ADUANERO)
 * ========================================================================================
 * Esta clase es la "Aduana" de nuestro programa. Controla la frontera entre dos entornos:
 *
 * 1. INTERACCIÓN CON ARCHIVOS (GimnasioMapsJsonDTO):
 *    Solo entiende texto plano (JSON). Aquí usamos nuestro 'GimnasioMapsJsonDTO' (una clase
 *    cuya estructura interna son listas de Maps genéricos vacíos o llenos, exactamente
 *    igual a lo que recibiríamos al atacar una API REST). Esto permite que Gson pueda
 *    escribir y leer sin que le afecte la "Amnesia de Java" (Type Erasure).
 *
 * 2. INTERACCIÓN CON JAVA (GimnasioObjetosJavaDTO):
 *    Entiende de clases, polimorfismo y fechas inteligentes. Aquí usamos nuestro
 *    'GimnasioObjetosJavaDTO' (una clase que agrupa listas de objetos instanciados reales) para
 *    que el Controlador y los DAOs puedan trabajar cómodamente con tipado fuerte.
 * ======================================================================================== */
public class GestorBackup {

    // =========================================================================
    // 1. EL ESCRITOR (Método Privado separado para mayor limpieza)
    // =========================================================================
    /*
     * Recibe el DTO estructurado en Maps crudos (GimanasioMapsJsonDTO) ya preparado y se lo
     * entrega a Gson para que lo imprima físicamente en el disco duro.
     */
    private void escribirArchivoJson(GimnasioMapsJsonDTO gimnasioMapsJsonDTO, String rutaDestino) {
        // Configuramos Gson con Pretty Printing para que el JSON sea legible por humanos
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(rutaDestino);
            // Gson lee la estructura exacta del DTO y genera el archivo
            gson.toJson(gimnasioMapsJsonDTO, fileWriter);
            System.out.println("✅ BACKUP GLOBAL EXITOSO EN: " + rutaDestino);

        } catch (IOException e) {
            System.out.println("❌ ERROR crítico al guardar el backup: " + e.getMessage());
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("❌ Error al cerrar el fileWriter.");
                }
            }
        }
    }

    // =========================================================================
    // 2. EXPORTAR: DE OBJETOS VIVOS A TEXTO CRUDO (Preparar la salida)
    // =========================================================================
    /*
     * El Controlador nos manda las listas de objetos reales (vivos).
     * Nuestro trabajo aquí es DESARMAR esos objetos y meter sus datos en Maps genéricos para
     * evitar la amnesia de Java (Type Erasure) y solucionar el LocalDate,
     * y las mete en el DTO de Maps.
     */
    public void exportarBackupBD(
            List<Usuario> listaUsuarios,
            List<Clase> listaClases,
            List<Sesion> listaSesiones,
            List<Plan> listaPlanes,
            List<Suscripcion> listaSuscripciones,
            List<Reserva> listaReservas) {

        // 1. Preparamos las listas temporales para nuestros Maps crudos
        List<Map<String, Object>> usuariosMapeados = new ArrayList<>();
        List<Map<String, Object>> clasesMapeadas = new ArrayList<>();
        List<Map<String, Object>> sesionesMapeadas = new ArrayList<>();
        List<Map<String, Object>> planesMapeados = new ArrayList<>();
        List<Map<String, Object>> suscripcionesMapeadas = new ArrayList<>();
        List<Map<String, Object>> reservasMapeadas = new ArrayList<>();

        // --- 2. BUCLE USUARIOS (Despiece y Polimorfismo) ---
        for (Usuario usuario : listaUsuarios) {
            Map<String, Object> datosUsuario = new HashMap<>();

            // Atributos comunes de la clase padre
            datosUsuario.put(SchemDB.COL_USUARIO_ID, usuario.getIdUsuario());
            datosUsuario.put(SchemDB.COL_USUARIO_NOMBRE, usuario.getNombre());
            datosUsuario.put(SchemDB.COL_USUARIO_APELLIDO, usuario.getApellido());
            datosUsuario.put(SchemDB.COL_USUARIO_EMAIL, usuario.getEmail());
            datosUsuario.put(SchemDB.COL_USUARIO_PASSWORD, usuario.getPassword());
            datosUsuario.put(SchemDB.COL_USUARIO_TELEFONO, usuario.getTelefono());
            datosUsuario.put(SchemDB.COL_USUARIO_ESTADO, usuario.getEstado().name());

            // Delegamos en métodos auxiliares para los atributos de las clases hijas
            if (usuario instanceof Socio) {
                procesarDatosSocio((Socio) usuario, datosUsuario);
            } else if (usuario instanceof Entrenador) {
                procesarDatosEntrenador((Entrenador) usuario, datosUsuario);
            } else if (usuario instanceof Secretario) {
                procesarDatosSecretario((Secretario) usuario, datosUsuario);
            }
            usuariosMapeados.add(datosUsuario);
        }

        // --- 3. BUCLE CLASES ---
        for (Clase clase : listaClases) {
            Map<String, Object> datos = new HashMap<>();
            datos.put(SchemDB.COL_CLASE_ID, clase.getIdClase());
            datos.put(SchemDB.COL_CLASE_NOMBRE, clase.getNombre());
            datos.put(SchemDB.COL_CLASE_ESTADO, clase.getEstado().name());
            datos.put(SchemDB.COL_CLASE_AFORO, clase.getAforoMax());
            clasesMapeadas.add(datos);
        }

        // --- 4. BUCLE SESIONES ---
        for (Sesion sesion : listaSesiones) {
            Map<String, Object> datos = new HashMap<>();
            datos.put(SchemDB.COL_SESION_ID, sesion.getIdSesion());
            datos.put(SchemDB.COL_SESION_ENTRENADOR, sesion.getIdEntrenador());
            datos.put(SchemDB.COL_SESION_CLASE, sesion.getIdClase());
            datos.put(SchemDB.COL_SESION_ESTADO, sesion.getEstado().name());
            datos.put(SchemDB.COL_SESION_SALA, sesion.getSala());
            // Fechas convertidas a String crudo para Gson
            datos.put(SchemDB.COL_SESION_INICIO, sesion.getInicio().toString());
            datos.put(SchemDB.COL_SESION_FIN, sesion.getFin().toString());
            sesionesMapeadas.add(datos);
        }

        // --- 5. BUCLE PLANES ---
        for (Plan plan : listaPlanes) {
            Map<String, Object> datos = new HashMap<>();
            datos.put(SchemDB.COL_PLAN_ID, plan.getIdPlan());
            datos.put(SchemDB.COL_PLAN_ESTADO, plan.getEstado().name());
            datos.put(SchemDB.COL_PLAN_NOMBRE, plan.getNombre());
            datos.put(SchemDB.COL_PLAN_PRECIO, plan.getPrecioMensual());
            planesMapeados.add(datos);
        }

        // --- 6. BUCLE SUSCRIPCIONES ---
        for (Suscripcion sub : listaSuscripciones) {
            Map<String, Object> datos = new HashMap<>();
            datos.put(SchemDB.COL_SUSC_ID, sub.getIdSuscripcion());
            datos.put(SchemDB.COL_SUSC_PLAN, sub.getIdPlan());
            datos.put(SchemDB.COL_SUSC_SOCIO, sub.getIdSocio());
            datos.put(SchemDB.COL_SUSC_ESTADO, sub.getEstado().name());
            datos.put(SchemDB.COL_SUSC_INICIO, sub.getFechaInicio().toString());
            datos.put(SchemDB.COL_SUSC_FIN, sub.getFechaFin().toString());
            suscripcionesMapeadas.add(datos);
        }

        // --- 7. BUCLE RESERVAS ---
        for (Reserva reserva : listaReservas) {
            Map<String, Object> datos = new HashMap<>();
            datos.put(SchemDB.COL_RES_SOCIO, reserva.getIdSocio());
            datos.put(SchemDB.COL_RES_SESION, reserva.getIdSesion());
            datos.put(SchemDB.COL_RES_ESTADO, reserva.getEstado().name());
            datos.put(SchemDB.COL_RES_FECHA, reserva.getFechaReserva().toString());
            reservasMapeadas.add(datos);
        }

        // 8. Construimos el DTO Externo: Insertamos las listas de Maps simulando la estructura de una API
        GimnasioMapsJsonDTO gimnasioMapsJsonDTO = new GimnasioMapsJsonDTO(
                usuariosMapeados,
                clasesMapeadas,
                sesionesMapeadas,
                planesMapeados,
                suscripcionesMapeadas,
                reservasMapeadas
        );

        // 9. Arrancamos el motor de escritura
        escribirArchivoJson(gimnasioMapsJsonDTO, "src/main/resources/backup_gimnasio.json");
    }

    // --- Métodos Auxiliares de Despiece (Evitando engordar el bucle principal) ---

    private void procesarDatosSocio(Socio socio, Map<String, Object> datosUsuario) {
        datosUsuario.put(SchemDB.COL_SOCIO_FECHA_ALTA, socio.getFechaAlta().toString());
        datosUsuario.put("tipo_rol", "SOCIO");
    }

    private void procesarDatosEntrenador(Entrenador entrenador, Map<String, Object> datosUsuario) {
        datosUsuario.put(SchemDB.COL_ENTRENADOR_ESPECIALIDAD, entrenador.getEspecialidad());
        datosUsuario.put("tipo_rol", "ENTRENADOR");
    }

    private void procesarDatosSecretario(Secretario secretario, Map<String, Object> datosUsuario) {
        datosUsuario.put(SchemDB.COL_SECRETARIO_TURNO, secretario.getTurno());
        datosUsuario.put("tipo_rol", "SECRETARIO");
    }

    // =========================================================================
    // 3. IMPORTAR: DE TEXTO CRUDO A OBJETOS VIVOS (Lectura y Resurrección)
    // =========================================================================
    /*
     * El proceso inverso. Gson lee el archivo y nos devuelve la clase 'GimnasioMapsJsonDTO'
     * (funciona exactamente como cuando consumimos el JSON de una API externa).
     * Nosotros iteramos esos Maps crudos, hacemos los 'new Clase()' correspondientes
     * usando LocalDate.parse() para revivir las fechas, y finalmente empaquetamos
     * todo en la clase 'GimnasioObjetosJavaDTO' para el Controlador.
     */
    public GimnasioObjetosJavaDTO importarBackup() {
        Gson gson = new Gson();
        String ruta = "src/main/resources/backup_gimnasio.json";

        // 1. Preparamos el DTO final de objetos instanciados. Si el archivo no existe,
        // devolvemos esto vacío para evitar NullPointerExceptions en el Controlador.
        GimnasioObjetosJavaDTO dtoVivo = new GimnasioObjetosJavaDTO();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(ruta);

            // 2. LEER DE GSON: Al usar nuestro GimnasioMapsJsonDTO, Gson usa Reflexión y
            // nos rellena las listas de Maps sin necesidad de TypeTokens horribles.
            GimnasioMapsJsonDTO gimnasioMapsJsonDTO = gson.fromJson(fileReader, GimnasioMapsJsonDTO.class);

            if (gimnasioMapsJsonDTO != null) {

                // --- 3. RECONSTRUIR USUARIOS ---
                if (gimnasioMapsJsonDTO.getTablaUsuarios() != null) {
                    List<Usuario> usuariosRecuperados = new ArrayList<>();

                    for (Map<String, Object> datos : gimnasioMapsJsonDTO.getTablaUsuarios()) {
                        // Gson lee los números como Double genéricos, forzamos la bajada a int
                        int id = ((Double) datos.get(SchemDB.COL_USUARIO_ID)).intValue();
                        String nombre = (String) datos.get(SchemDB.COL_USUARIO_NOMBRE);
                        String apellido = (String) datos.get(SchemDB.COL_USUARIO_APELLIDO);
                        String email = (String) datos.get(SchemDB.COL_USUARIO_EMAIL);
                        String pass = (String) datos.get(SchemDB.COL_USUARIO_PASSWORD);
                        String tel = (String) datos.get(SchemDB.COL_USUARIO_TELEFONO);
                        EstadoUsuario estado = EstadoUsuario.valueOf((String) datos.get(SchemDB.COL_USUARIO_ESTADO));

                        // Leemos la etiqueta que pusimos al exportar para saber a qué hijo llamar
                        String rol = (String) datos.get("tipo_rol");

                        if (rol.equals("SOCIO")) {
                            usuariosRecuperados.add(reconstruirSocio(datos, id, nombre, apellido, email, pass, tel, estado));
                        } else if (rol.equals("ENTRENADOR")) {
                            usuariosRecuperados.add(reconstruirEntrenador(datos, id, nombre, apellido, email, pass, tel, estado));
                        } else if (rol.equals("SECRETARIO")) {
                            usuariosRecuperados.add(reconstruirSecretario(datos, id, nombre, apellido, email, pass, tel, estado));
                        }
                    }
                    dtoVivo.setUsuarios(usuariosRecuperados);
                }

                // --- 4. RECONSTRUIR CLASES ---
                if (gimnasioMapsJsonDTO.getTablaClases() != null) {
                    List<Clase> clasesRecuperadas = new ArrayList<>();
                    for (Map<String, Object> datos : gimnasioMapsJsonDTO.getTablaClases()) {
                        clasesRecuperadas.add(reconstruirClase(datos));
                    }
                    dtoVivo.setClases(clasesRecuperadas);
                }

                // --- 5. RECONSTRUIR SESIONES ---
                if (gimnasioMapsJsonDTO.getTablaSesiones() != null) {
                    List<Sesion> sesionesRecuperadas = new ArrayList<>();
                    for (Map<String, Object> datos : gimnasioMapsJsonDTO.getTablaSesiones()) {
                        sesionesRecuperadas.add(reconstruirSesion(datos));
                    }
                    dtoVivo.setSesiones(sesionesRecuperadas);
                }

                // --- 6. RECONSTRUIR PLANES ---
                if (gimnasioMapsJsonDTO.getTablaPlanes() != null) {
                    List<Plan> planesRecuperados = new ArrayList<>();
                    for (Map<String, Object> datos : gimnasioMapsJsonDTO.getTablaPlanes()) {
                        planesRecuperados.add(reconstruirPlan(datos));
                    }
                    dtoVivo.setPlanes(planesRecuperados);
                }

                // --- 7. RECONSTRUIR SUSCRIPCIONES ---
                if (gimnasioMapsJsonDTO.getTablaSuscripciones() != null) {
                    List<Suscripcion> suscripcionesRecuperadas = new ArrayList<>();
                    for (Map<String, Object> datos : gimnasioMapsJsonDTO.getTablaSuscripciones()) {
                        suscripcionesRecuperadas.add(reconstruirSuscripcion(datos));
                    }
                    dtoVivo.setSuscripciones(suscripcionesRecuperadas);
                }

                // --- 8. RECONSTRUIR RESERVAS ---
                if (gimnasioMapsJsonDTO.getTablaReservas() != null) {
                    List<Reserva> reservasRecuperadas = new ArrayList<>();
                    for (Map<String, Object> datos : gimnasioMapsJsonDTO.getTablaReservas()) {
                        reservasRecuperadas.add(reconstruirReserva(datos));
                    }
                    dtoVivo.setReservas(reservasRecuperadas);
                }
            }

        } catch (IOException e) {
            System.out.println("❌ ERROR al leer el backup global: " + e.getMessage());
        } finally {
            // Liberamos el recurso de lectura del sistema operativo
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    System.out.println("❌ Error al cerrar el fileReader.");
                }
            }
        }

        // Entregamos el DTO vivo, listo para el Controlador
        return dtoVivo;
    }

    // --- Métodos Auxiliares de Reconstrucción (Reviviendo Fechas y Objetos) ---

    private Socio reconstruirSocio(Map<String, Object> datos, int id, String nombre, String apellido, String email, String pass, String tel, EstadoUsuario estado) {
        String fechaString = (String) datos.get(SchemDB.COL_SOCIO_FECHA_ALTA);
        LocalDate fecha = LocalDate.parse(fechaString); // ¡Magia! Revivimos el texto a objeto inteligente
        return new Socio(id, estado, nombre, apellido, email, pass, tel, fecha);
    }

    private Entrenador reconstruirEntrenador(Map<String, Object> datos, int id, String nombre, String apellido, String email, String pass, String tel, EstadoUsuario estado) {
        String especialidad = (String) datos.get(SchemDB.COL_ENTRENADOR_ESPECIALIDAD);
        return new Entrenador(id, estado, nombre, apellido, email, pass, tel, especialidad);
    }

    private Secretario reconstruirSecretario(Map<String, Object> datos, int id, String nombre, String apellido, String email, String pass, String tel, EstadoUsuario estado) {
        String turno = (String) datos.get(SchemDB.COL_SECRETARIO_TURNO);
        return new Secretario(id, estado, nombre, apellido, email, pass, tel, turno);
    }

    private Clase reconstruirClase(Map<String, Object> datos) {
        int id = ((Double) datos.get(SchemDB.COL_CLASE_ID)).intValue();
        String nombre = (String) datos.get(SchemDB.COL_CLASE_NOMBRE);
        model.enums.EstadoClase estado = model.enums.EstadoClase.valueOf((String) datos.get(SchemDB.COL_CLASE_ESTADO));
        int aforo = ((Double) datos.get(SchemDB.COL_CLASE_AFORO)).intValue();
        return new Clase(id, estado, nombre, aforo);
    }

    private Sesion reconstruirSesion(Map<String, Object> datos) {
        int id = ((Double) datos.get(SchemDB.COL_SESION_ID)).intValue();
        int idEntrenador = ((Double) datos.get(SchemDB.COL_SESION_ENTRENADOR)).intValue();
        int idClase = ((Double) datos.get(SchemDB.COL_SESION_CLASE)).intValue();
        model.enums.EstadoSesion estado = model.enums.EstadoSesion.valueOf((String) datos.get(SchemDB.COL_SESION_ESTADO));
        String sala = (String) datos.get(SchemDB.COL_SESION_SALA);

        // Revivimos los LocalDateTime usando parse()
        LocalDateTime inicio = LocalDateTime.parse((String) datos.get(SchemDB.COL_SESION_INICIO));
        LocalDateTime fin = LocalDateTime.parse((String) datos.get(SchemDB.COL_SESION_FIN));

        return new Sesion(id, idEntrenador, idClase, estado, sala, inicio, fin);
    }

    private Plan reconstruirPlan(Map<String, Object> datos) {
        int id = ((Double) datos.get(SchemDB.COL_PLAN_ID)).intValue();
        model.enums.EstadoPlan estado = model.enums.EstadoPlan.valueOf((String) datos.get(SchemDB.COL_PLAN_ESTADO));
        String nombre = (String) datos.get(SchemDB.COL_PLAN_NOMBRE);
        double precio = (Double) datos.get(SchemDB.COL_PLAN_PRECIO); // Mantenemos el double puro
        return new Plan(id, estado, nombre, precio);
    }

    private Suscripcion reconstruirSuscripcion(Map<String, Object> datos) {
        int idSusc = ((Double) datos.get(SchemDB.COL_SUSC_ID)).intValue();
        int idPlan = ((Double) datos.get(SchemDB.COL_SUSC_PLAN)).intValue();
        int idSocio = ((Double) datos.get(SchemDB.COL_SUSC_SOCIO)).intValue();
        model.enums.EstadoSuscripcion estado = model.enums.EstadoSuscripcion.valueOf((String) datos.get(SchemDB.COL_SUSC_ESTADO));

        LocalDate inicio = LocalDate.parse((String) datos.get(SchemDB.COL_SUSC_INICIO));
        LocalDate fin = LocalDate.parse((String) datos.get(SchemDB.COL_SUSC_FIN));

        return new Suscripcion(idSusc, idPlan, idSocio, estado, inicio, fin);
    }

    private Reserva reconstruirReserva(Map<String, Object> datos) {
        int idSocio = ((Double) datos.get(SchemDB.COL_RES_SOCIO)).intValue();
        int idSesion = ((Double) datos.get(SchemDB.COL_RES_SESION)).intValue();
        model.enums.EstadoReserva estado = model.enums.EstadoReserva.valueOf((String) datos.get(SchemDB.COL_RES_ESTADO));

        LocalDateTime fecha = LocalDateTime.parse((String) datos.get(SchemDB.COL_RES_FECHA));

        return new Reserva(idSocio, idSesion, estado, fecha);
    }
}