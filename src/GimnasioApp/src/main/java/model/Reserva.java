package model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import model.enums.EstadoReserva;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // En tu tabla relacional, esta entidad es la unión de dos FK
    private int idSocio;
    private int idSesion;
   //TABLA INTERMEDIA
    private int idSocio;  //FK
    private int idSesion; //FK
    private EstadoReserva estado;
    private LocalDateTime fechaReserva;

    @Override
    public String toString() {
        return "Reserva -> Socio ID: " + idSocio + " | Sesion ID: " + idSesion + " | Estado: " + estado;
    }
}