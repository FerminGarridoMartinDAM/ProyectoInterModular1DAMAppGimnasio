# XML y Esquema de Validación - Catálogo Gym APP

## 1. ¿Qué información tiene el XML?
Este archivo (`catalogo_gimnasio.xml`) es básicamente una exportación de los datos que tenemos en la base de datos para que otros sistemas los puedan leer. He estructurado la información así:
- Planes de suscripción: Con su ID, el nombre del plan, cuánto cuesta al mes y si está ACTIVO o INACTIVO ahora mismo.
- Clases del catálogo: He metido las actividades que ofrecemos, como Zumba o Yoga, junto con las plazas máximas que tiene cada una.

## 2. ¿Cómo funciona la validación con el XSD?
Para que no haya fallos si alguien intenta importar estos datos, he creado el `catalogo_esquema.xsd`. Este archivo obliga a que el XML cumpla unas reglas fijas:
- Tipos de datos: He configurado que los precios sean decimales y que el aforo sea siempre un número entero.
- Nombres fijos: He bloqueado el nombre del gym con el atributo "fixed" para que sea siempre "Gym APP Prometeo". Si alguien lo cambia, el validador da error.
- Estados controlados: El estado de los planes solo puede ser "ACTIVO" o "INACTIVO", así coincide exactamente con los Enums que tengo en el código Java.
- Mínimos obligatorios: El sistema no da por bueno el XML si no tiene al menos un plan y una clase dentro del catálogo.

En esta misma carpeta he dejado las capturas de pantalla donde se ve que el validador da el OK y también otra donde salta el error cuando intento cambiar el nombre del gimnasio o meto un formato incorrecto. 

## 3. Integración en el programa
Este XML no es solo un archivo de relleno; está pensado para ser una función de la aplicación que nos ayude a recoger y a pasarle datos al programa de forma segura. Al usar el esquema XSD, nos aseguramos de que cualquier dato que entre o salga de la App sea correcto y no tenga errores de formato que puedan romper el sistema.