package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




// No la vamos a usar porque al final hemos hecho el DTO
public class FormateadorLocalDate {

    // Definimos el molde una sola vez para toda la aplicación.
    private static final DateTimeFormatter FORMATEADOR = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Recibimos (LocalDate localDate) y lo transformamos a String
    public static String aString(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(FORMATEADOR);
    }

    // Recibimos (String string) y lo parseamos de vuelta a LocalDate
    public static LocalDate aLocalDate(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        return LocalDate.parse(string, FORMATEADOR);
    }
}