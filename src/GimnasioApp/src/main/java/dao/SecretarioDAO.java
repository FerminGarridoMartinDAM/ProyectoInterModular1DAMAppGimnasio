package dao;

import model.Secretario;
import java.util.List;

public interface SecretarioDAO {
    int insert(Secretario secretario);
    List<Secretario> selectAll();
    int update(Secretario secretario);
    int delete(int id);
    Secretario selectById(int id);
}