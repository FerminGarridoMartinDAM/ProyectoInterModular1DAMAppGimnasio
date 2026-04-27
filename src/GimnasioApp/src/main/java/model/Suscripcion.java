package model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import model.enums.EstadoSuscripcion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {

    private int idSuscripcion;
    private int idPlan;   // FK al plan
    private int idSocio;  // FK al socio
    private EstadoSuscripcion estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Override
    public String toString() {
        return "Suscrip. [" + idSuscripcion + "] Socio ID: " + idSocio + " | Plan ID: " + idPlan + " | Estado: " + estado;
    }
}