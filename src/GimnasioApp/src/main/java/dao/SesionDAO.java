package dao;

import model.Sesion;
import java.util.List;

public interface SesionDAO {
    int insert(Sesion sesion);
    List<Sesion> selectAll();
    int update(Sesion sesion);
    int delete(int id);
    Sesion selectById(int id);
}