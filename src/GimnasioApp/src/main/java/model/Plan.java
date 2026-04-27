package model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import model.enums.EstadoPlan;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    private int idPlan;
    private EstadoPlan estado;
    private String nombre;
    private double precioMensual; // En Java para  DECIMAL(10,2) se suele usar  double ; En entornos bancarios se suele usar BigDecimal

    @Override
    public String toString() {
        return "Plan [" + idPlan + "] " + nombre + " - " + precioMensual + "€ | Estado: " + estado;
    }
}