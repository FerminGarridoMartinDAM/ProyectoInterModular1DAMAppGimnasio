package model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import model.enums.EstadoSesion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {

    private int idSesion;
    private int idEntrenador; // FK al entrenador
    private int idClase;      // FK a la clase
    private EstadoSesion estado;
    private String sala;
    private LocalDateTime inicio;
    private LocalDateTime fin;

    @Override
    public String toString() {
        return "Sesion [" + idSesion + "] Sala: " + sala + " | Inicio: " + inicio + " | Estado: " + estado;
    }




    /*


     Para MANDAR a Supabase (Sacamos el dato convertido)

public Timestamp getInicioAsTimestamp() {
    return Timestamp.valueOf(this.inicio);
}

 Para RECIBIR de Supabase (Metemos el dato y lo convertimos al guardar)

public void setInicioFromTimestamp(Timestamp timestamp) {
    this.inicio = timestamp.toLocalDateTime();


  !!!TODO  Tambien he leido que hay unos MAPPERS, revisar.

}*/
}