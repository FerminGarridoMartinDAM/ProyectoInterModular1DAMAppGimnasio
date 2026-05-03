package utils;

import java.util.Scanner;

/* ========================================================================================
 * UTILIDADES DE LECTURA POR CONSOLA
 * ========================================================================================
 * OBJETIVO: Centralizar la captura de datos por teclado y gestionar los errores de formato.
 *
 * - Contiene métodos 'static' para ser invocados directamente por la clase,
 *   funcionando como herramientas globales para los controladores.
 * ========================================================================================
 */
public class LectorConsola {

    /*

     * Solicita un ID numérico por consola permitiendo salir del metodo que antes solo se podia o termianndo el proceso o forzando NumberFormatException.
     *
     * teclado Scanner para capturar la entrada.
     * mensaje Texto a mostrar al usuario antes de pedir el dato.
     * retorna El ID válido (>0), 0 si el usuario cancela, o -1 si el formato es incorrecto.
     */
    public static int leerIdConCancelacion(Scanner teclado, String mensaje) {
        System.out.print(mensaje + " (o pulsa 0 para cancelar): ");

        try {
            int id = Integer.parseInt(teclado.nextLine().trim());

            if (id == 0) {
                System.out.println("Operación cancelada. Volviendo al menú...");
            }

            return id;

        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato incorrecto. Debes introducir un número.");
            return -1;
        }
    }
}