package model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Entrenador extends Usuario {

    private String especialidad;

    @Override
    public String toString() {
        return super.toString() + " | [Entrenador -> Especialidad: " + especialidad + "]";
    }
}