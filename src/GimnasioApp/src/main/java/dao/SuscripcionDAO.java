package dao;

import model.Suscripcion;
import java.util.List;

public interface SuscripcionDAO {
    int insert(Suscripcion suscripcion);
    List<Suscripcion> selectAll();
    int update(Suscripcion suscripcion);
    int delete(int id);
}