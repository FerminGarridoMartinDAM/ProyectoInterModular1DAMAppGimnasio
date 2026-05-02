package dao;

import model.Entrenador;
import java.util.List;

public interface EntrenadorDAO {
    int insert(Entrenador entrenador);
    List<Entrenador> selectAll();
    int update(Entrenador entrenador);
    int delete(int id);
    Entrenador selectById(int id);
}