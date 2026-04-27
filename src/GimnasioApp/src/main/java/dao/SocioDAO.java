package dao;

import model.Socio;
import java.util.List;

public interface SocioDAO {
    int insert(Socio socio);
    List<Socio> selectAll();
    int update(Socio socio);
    int delete(int id);
}