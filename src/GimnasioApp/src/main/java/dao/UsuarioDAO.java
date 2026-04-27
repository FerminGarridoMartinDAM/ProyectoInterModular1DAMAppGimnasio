package dao;

import model.Usuario;
import java.util.List;

public interface UsuarioDAO {
    //El metodo login para saber que usuario entra en la app
    Usuario login(String email, String password);

    List<Usuario> selectAll();
    int update(Usuario usuario);
    int delete(int id);
}