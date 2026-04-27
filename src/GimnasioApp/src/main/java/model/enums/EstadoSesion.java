package model.enums;

public enum EstadoSesion {
    PROGRAMADA, // La sesión está en el calendario futuro
    REALIZADA,  // La sesión ya ocurrió y el entrenador la impartió
    CANCELADA   // La sesión se ha cancelado
}