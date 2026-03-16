# Creación de la Base de Datos en Supabase y Gestión desde pgAdmin

### 1. ¿Dónde vive nuestra Base de Datos? (Supabase)
Para este proyecto, he decidido que la base de datos no esté encerrada en mi ordenador local. La he subido a la nube usando **Supabase** (PostgreSQL 15). De esta manera, el programa Java se conecta a internet para funcionar, igual que hacen las aplicaciones reales.

Las credenciales que conectan nuestro código con este servidor en la nube son:
* **Host (La dirección web):** `aws-1-eu-west-1.pooler.supabase.com`
* **Puerto:** `5432`
* **Usuario Maestro:** `postgres.ubgynnvqllswogxuovvw`
* **Contraseña:** `ProyectoIntermodularDAM2026!`

### 2. ¿Cómo la controlamos? (Gestión desde pgAdmin 4)
Aunque los datos viven en Supabase, entrar a su web cada vez que quiero hacer un cambio es muy lento. Por eso, he vinculado ese servidor de la nube con el programa **pgAdmin 4** que tengo instalado en mi ordenador. 
pgAdmin actúa como mi "mando a distancia". Desde ahí he ejecutado el archivo `01_creacion_tablas.sql` para crear toda la estructura de golpe, y es donde comprobaré visualmente que los datos que enviemos desde Java se están guardando bien.

### 3. El puente con nuestro código Java
Para que Java pueda comunicarse con la base de datos PostgreSQL en la nube, hemos añadido al archivo `pom.xml` la dependencia del **Driver JDBC**. Este driver es simplemente el "traductor" que permite que nuestras clases Java envíen comandos SQL reales a través de internet.

### 4. Decisiones clave en el diseño de las tablas
Como se puede ver detalladamente en las notas aclaratorias de los diagramas adjuntos en esta carpeta (ver `Diagrama modelo Entidad Relacion.drawio`), he adaptado la estructura para evitar fallos lógicos:

* **SESIÓN y SUSCRIPCIÓN:** Se han convertido en entidades propias con su propio ID para permitir que se repitan en el tiempo (por ejemplo, que el mismo entrenador dé la misma clase en distintos días sin que el sistema dé error).
* **RESERVA:** Se mantiene como tabla intermedia pura (sin ID propio) para que la base de datos bloquee duplicados y no permita que un socio reserve dos veces exactamente la misma sesión.