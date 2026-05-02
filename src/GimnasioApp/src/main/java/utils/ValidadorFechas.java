package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/* ========================================================================================
 * VALIDADOR DE FECHAS
 * ========================================================================================
 * Clase utilitaria para estandarizar la entrada de fechas en todo el sistema y
 * proteger la base de datos de parseos incorrectos que generarían excepciones SQL.
 *Nivel actual: Uso de DateTimeFormatter estricto + Bucle de reintento manual.
 * ========================================================================================
 */


// ---  FECHA y HORA ---
public class ValidadorFechas {
    public static LocalDateTime pedirFechaHora(Scanner teclado, String mensajeFiltro) {

        // Patrón estricto requerido por nuestra lógica de negocio para las sesiones
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fecha = null;
        boolean fechaValida = false;

        do {
            System.out.print(mensajeFiltro + " (Formato DD/MM/AAAA HH:MM): ");

            try {
                // Sanitizamos la entrada con trim() antes de parsear para evitar
                // que espacios invisibles accidentales rompan el formateador.
                String textoUsuario = teclado.nextLine().trim();
                fecha = LocalDateTime.parse(textoUsuario, formato);

                fechaValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato incorrecto. Ejemplo válido: 04/03/2026 18:00");
            }
        } while (!fechaValida);

        return fecha;
    }

    // --- SOLO FECHA ---
    public static LocalDate pedirSoloFecha(Scanner teclado, String mensajeFiltro) {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = null;
        boolean fechaValida = false;

        do {
            System.out.print(mensajeFiltro + " (Formato DD/MM/AAAA): ");

            try {
                // Usamos LocalDate ya que para suscripciones/altas la hora es irrelevante
                fecha = LocalDate.parse(teclado.nextLine().trim(), formato);

                fechaValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato incorrecto. Ejemplo válido: 25/10/2026");
            }
        } while (!fechaValida);

        return fecha;
    }
}