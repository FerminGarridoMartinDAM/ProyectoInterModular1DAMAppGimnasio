package model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Secretario extends Usuario {



    private String turno;

    @Override
    public String toString() {
        return super.toString() + " | [Secretario -> Turno: " + turno + "]";
    }
}