

--Tablas fuertes sin fk

CREATE TABLE socio (
    id_socio SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15),
    fecha_alta DATE NOT NULL
);


CREATE TABLE plan (
    id_plan SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    precio_mensual DECIMAL(10,2) NOT NULL
);



CREATE TABLE entrenador (
    id_entrenador SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    especialidad VARCHAR(50)
);

CREATE TABLE clase (
    id_clase SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    aforo_max INT NOT NULL
);

--Tablas con pk y fk heredada

CREATE TABLE sesion (
    id_sesion SERIAL PRIMARY KEY,
    id_clase INT NOT NULL,
    id_entrenador INT NOT NULL,
    sala VARCHAR(50),
    inicio TIMESTAMP NOT NULL,
    fin TIMESTAMP NOT NULL,
    FOREIGN KEY (id_clase) REFERENCES clase(id_clase),
    FOREIGN KEY (id_entrenador) REFERENCES entrenador(id_entrenador)
);

CREATE TABLE suscripcion (
    id_suscripcion SERIAL PRIMARY KEY,
    id_socio INT NOT NULL,
    id_plan INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_socio) REFERENCES socio(id_socio),
    FOREIGN KEY (id_plan) REFERENCES plan(id_plan)
);

 -- Tabla intermedia 
 
CREATE TABLE reserva (
    id_socio INT NOT NULL,
    id_sesion INT NOT NULL,
    fecha_reserva TIMESTAMP NOT NULL,
    PRIMARY KEY (id_socio, id_sesion),
    FOREIGN KEY (id_socio) REFERENCES socio(id_socio),
    FOREIGN KEY (id_sesion) REFERENCES sesion(id_sesion)
);