/*
package controller;

import model.Usuario;
import model.Admin;
import model.enums.EstadoUsuario;

import java.util.List;
import java.util.Scanner;

/* ========================================================================================
 * 🧠 GUÍA DE ESTUDIO: EL PODER DE LOS GENÉRICOS (<T>) Y EL PATRÓN TEMPLATE
 * ========================================================================================
 * ¿Qué es la <T>?
 * 'T' viene de "Type" (Tipo). Es un comodín, una etiqueta en blanco o un "molde".
 * Le dice al compilador de Java: "Esta clase va a trabajar con un objeto específico,
 * pero no te diré cuál es hasta que una clase Hija decida heredar de mí".
 *
 * ¿Por qué usamos <T extends Usuario>? (Bounded Type)
 * No queremos que <T> sea absolutamente CUALQUIER cosa (como un String o un Integer).
 * Al poner 'extends Usuario', creamos un límite de seguridad. Garantizamos que el molde
 * solo aceptará clases de nuestra jerarquía (Socio, Entrenador, Secretario).
 * Esto le permite a esta clase Padre usar métodos como .setNombre() o .setEstado()
 * tranquilamente, porque sabe que la <T> siempre será algún tipo de Usuario.
 *
 * ----------------------------------------------------------------------------------------
 * ❌ EL PROBLEMA DEL NIVEL BÁSICO: ¿Por qué no usar simplemente 'Usuario'?
 * Si los métodos de esta clase trabajaran con la clase 'Usuario' (ej: List<Usuario>),
 * cuando el Hijo (GestionSociosController) recuperara un registro, recibiría un 'Usuario'
 * genérico. Perdería su identidad de 'Socio' y no podríamos acceder a sus atributos
 * específicos (como la fecha de alta o la especialidad) sin hacer conversiones manuales.
 *
 * ❌ LA TRAMPA MORTAL: ¿Por qué no usar 'Object' (el ancestro de todo Java)?
 * 1. Pérdida del Type Safety (Seguridad de Tipos): Si usamos Object, podríamos meter un
 *    texto ("Hola") en una base de datos de Socios por error y el IDE no nos avisaría.
 * 2. ClassCastException: Estaríamos obligados a forzar a Java a adivinar qué hay dentro
 *    de la caja haciendo "Casting": Socio s = (Socio) lista.get(0); lo cual es muy
 *    propenso a hacer que el programa explote en tiempo de ejecución.
 *
 * ----------------------------------------------------------------------------------------
 * ✅ EL NEXT LEVEL: La Magia de la <T> (Type Erasure / Buscar y Reemplazar)
 * Cuando la clase Hija firma el contrato así:
 * public class GestionSociosController extends GestionUsuariosControllerBase<Socio>
 *
 * En ese instante, Java hace un "Buscar y Reemplazar" invisible en tiempo de compilación.
 * Borra todas las letras 'T' de esta clase Padre y escribe la palabra 'Socio'.
 *
 * RESULTADO: El Padre escribe el código repetitivo una sola vez, pero el Hijo recibe
 * un código 100% adaptado a su clase, con autocompletado en el IDE y blindado contra
 * errores humanos. Es el equilibrio perfecto entre reutilización y seguridad.
 * =======================================================================================




*/
/* ========================================================================================
 * GUÍA DE ESTUDIO: PATRÓN TEMPLATE METHOD CON GENÉRICOS (<T>)
 * ========================================================================================
 * <T extends Usuario> asegura que T solo puede ser un Socio, Entrenador o Secretario.
 * El Padre escribe todo el flujo de trabajo una sola vez, y los hijos solo
 * aportan los detalles específicos (DAO y datos propios).
 * ========================================================================================
 *//*

public abstract class GestionUsuariosControllerBase<T extends Usuario> {

    protected Scanner teclado;
    protected Usuario usuarioActual;

    public GestionUsuariosControllerBase(Scanner teclado, Usuario usuarioActual) {
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    // ==================================================================
    // 🕳️ LOS "HUECOS" (Contratos que el hijo debe firmar y rellenar)
    // ==================================================================
    protected abstract String getNombrePlural();
    protected abstract String getNombreSingular();

    // El hijo le dará al Padre su DAO específico a través de estos métodos
    protected abstract List<T> obtenerTodosDelDAO();
    protected abstract T buscarPorIdEnDAO(int id);
    protected abstract int insertarEnDAO(T entidad);
    protected abstract int actualizarEnDAO(T entidad);
    protected abstract int borrarFisicamenteEnDAO(int id);

    // Detalles visuales y propios del hijo
    protected abstract void imprimirCabeceraTabla();
    protected abstract void imprimirDatosFila(T entidad);
    protected abstract T instanciarEntidad(); // El hijo hace 'new Socio()'
    protected abstract void pedirDatosEspecificos(T entidad); // El hijo pide la Fecha o Especialidad

    // ==================================================================
    // ⚙️ EL MOTOR CENTRAL (Flujo inamovible programado por el Padre)
    // ==================================================================
    public void mostrar() {
        int opcionMenu = -1;
        do {
            System.out.println("\n--- GESTIÓN DE " + getNombrePlural() + " ---");
            System.out.println("1. Mostrar lista de todos");
            System.out.println("2. Alta de nuevo registro");
            System.out.println("3. Dar de baja (Desactivar)");
            if (usuarioActual instanceof Admin) {
                System.out.println("4. Borrado permanente");
            }
            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());
                switch (opcionMenu) {
                    case 1: mostrarTodos(); break;
                    case 2: alta(); break;
                    case 3: bajaLogica(); break;
                    case 4:
                        if (usuarioActual instanceof Admin) {
                            borradoPermanente();
                        } else {
                            System.out.println("❌ Error: Opción no válida.");
                        }
                        break;
                    case 0: break;
                    default: System.out.println("❌ Error: Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Introduce un número válido.");
            }
        } while (opcionMenu != 0);
    }

    // --- ACCIONES GENERALES ---
    private void mostrarTodos() {
        System.out.println("\n--- LISTADO DE " + getNombrePlural() + " ---");
        List<T> lista = obtenerTodosDelDAO();

        if (lista.isEmpty()) {
            System.out.println("ℹ️ No hay ningún registro en este momento.");
        } else {
            imprimirCabeceraTabla();
            for (T entidad : lista) {
                imprimirDatosFila(entidad);
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    private void alta() {
        System.out.println("\n--- ALTA DE NUEVO " + getNombreSingular() + " ---");

        // 1. El Padre pide al hijo que cree la caja vacía
        T nuevaEntidad = instanciarEntidad();

        // 2. El Padre rellena lo que sabe (heredado de Usuario)
        System.out.print("Nombre: "); nuevaEntidad.setNombre(teclado.nextLine().trim());
        System.out.print("Apellido: "); nuevaEntidad.setApellido(teclado.nextLine().trim());
        System.out.print("Email: "); nuevaEntidad.setEmail(teclado.nextLine().trim());
        System.out.print("Contraseña temporal: "); nuevaEntidad.setPassword(teclado.nextLine().trim());
        System.out.print("Teléfono: "); nuevaEntidad.setTelefono(teclado.nextLine().trim());
        nuevaEntidad.setEstado(EstadoUsuario.ACTIVO);

        // 3. El Padre se detiene y pide al hijo que pregunte sus datos raros
        pedirDatosEspecificos(nuevaEntidad);

        // 4. El Padre le pasa la entidad llena al hijo para que la guarde en su DAO
        if (insertarEnDAO(nuevaEntidad) > 0) {
            System.out.println("✅ " + getNombreSingular() + " registrado con éxito.");
        } else {
            System.out.println("❌ Hubo un problema al registrar en la base de datos.");
        }
    }

    private void bajaLogica() {
        System.out.println("\n--- BAJA (DESACTIVACIÓN) ---");
        System.out.print("Introduce el ID a dar de baja: ");
        try {
            int id = Integer.parseInt(teclado.nextLine().trim());
            T entidadABorrar = buscarPorIdEnDAO(id); // Llamada al DAO del hijo

            if (entidadABorrar != null) {
                if (entidadABorrar.getEstado() == EstadoUsuario.INACTIVO) {
                    System.out.println("⚠️ Ya estaba dado de baja previamente.");
                } else {
                    entidadABorrar.setEstado(EstadoUsuario.INACTIVO);
                    actualizarEnDAO(entidadABorrar); // Llamada al DAO del hijo
                    System.out.println("✅ Desactivado correctamente (Baja lógica).");
                }
            } else {
                System.out.println("❌ No se encontró ningún registro con ese ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de ID incorrecto.");
        }
    }

    private void borradoPermanente() {
        System.out.println("\n--- ☢️ BORRADO PERMANENTE ☢️ ---");
        System.out.print("Introduce el ID a ELIMINAR del sistema: ");
        try {
            int id = Integer.parseInt(teclado.nextLine().trim());
            System.out.print("⚠️ ¿Estás completamente seguro? Esta acción no se puede deshacer. (S/N): ");

            if (teclado.nextLine().trim().equalsIgnoreCase("S")) {
                if (borrarFisicamenteEnDAO(id) > 0) {
                    System.out.println("✅ Borrado físicamente de la base de datos.");
                } else {
                    System.out.println("❌ No se pudo borrar (¿Quizás no existe o tiene dependencias?)");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de ID incorrecto.");
        }
    }
}*/
