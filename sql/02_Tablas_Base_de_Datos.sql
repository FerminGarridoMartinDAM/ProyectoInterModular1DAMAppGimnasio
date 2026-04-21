

-- Se ha añadido el delete en cascade para que si eliminas a un usuario, PostgreSQL buscará automáticamente a ese usuario en las tablas socio, suscripcion y reserva y lo borrará de todas partes al instante. Esto evita que se queden "datos fantasma" en la base de datos y te ahorra tener que hacer 4 consultas DELETE seguidas en Java.


-- ==============================================================================
-- 1. LIMPIEZA PREVIA (Borrar tablas viejas en orden inverso para evitar errores)
-- ==============================================================================
DROP TABLE IF EXISTS reserva CASCADE;
DROP TABLE IF EXISTS suscripcion CASCADE;
DROP TABLE IF EXISTS sesion CASCADE;
DROP TABLE IF EXISTS clase CASCADE;
DROP TABLE IF EXISTS plan CASCADE;
DROP TABLE IF EXISTS socio CASCADE;
DROP TABLE IF EXISTS entrenador CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;



-- ==============================================================================
-- 2. TABLA PADRE (La superclase)
-- ==============================================================================
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(15)
);

-- ==============================================================================
-- 3. TABLAS HIJAS (Herencia 1 a 1)
-- ==============================================================================
CREATE TABLE socio (
    id_usuario INT PRIMARY KEY,
    fecha_alta DATE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE entrenador (
    id_usuario INT PRIMARY KEY,
    especialidad VARCHAR(50),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- ==============================================================================
-- 4. TABLAS DE CATÁLOGO (Independientes)
-- ==============================================================================
CREATE TABLE plan (
    id_plan SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    precio_mensual DECIMAL(10,2) NOT NULL
);

CREATE TABLE clase (
    id_clase SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    aforo_max INT NOT NULL
);

-- ==============================================================================
-- 5. TABLAS DE NEGOCIO (Con dependencias)
-- ==============================================================================
CREATE TABLE suscripcion (
    id_suscripcion SERIAL PRIMARY KEY,
    id_plan INT NOT NULL,
    id_socio INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_plan) REFERENCES plan(id_plan),
    FOREIGN KEY (id_socio) REFERENCES socio(id_usuario) ON DELETE CASCADE
);

CREATE TABLE sesion (
    id_sesion SERIAL PRIMARY KEY,
    id_entrenador INT NOT NULL,
    id_clase INT NOT NULL,
    sala VARCHAR(50),
    inicio TIMESTAMP NOT NULL,
    fin TIMESTAMP NOT NULL,
    FOREIGN KEY (id_entrenador) REFERENCES entrenador(id_usuario),
    FOREIGN KEY (id_clase) REFERENCES clase(id_clase)
);

-- ==============================================================================
-- 6. TABLA INTERMEDIA PURA (Relación N:M)
-- ==============================================================================
CREATE TABLE reserva (
    id_socio INT NOT NULL,
    id_sesion INT NOT NULL,
    fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_socio, id_sesion),
    FOREIGN KEY (id_socio) REFERENCES socio(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_sesion) REFERENCES sesion(id_sesion) ON DELETE CASCADE
);