package model.enums;

public enum EstadoReserva {
    RESERVADA, // El socio ha reservado
    ASISTIDA,  // El socio fue a la clase
    AUSENTE,   // El socio reservó pero no se presentó (para penalizaciones)
    CANCELADA  // El socio canceló la reserva a tiempo
}