package model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import model.enums.EstadoClase;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clase {

    private int idClase;
    private EstadoClase estado;
    private String nombre;
    private int aforoMax;

    @Override
    public String toString() {
        return "Clase [" + idClase + "] " + nombre + " (Aforo: " + aforoMax + ") | Estado: " + estado;
    }
}