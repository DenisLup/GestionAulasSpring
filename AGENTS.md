# Instrucciones para agentes

Lee estas indicaciones antes de trabajar en el proyecto y aplícalas en cada tarea. Sigue también las instrucciones específicas que dé el usuario para la tarea actual.

## Proyecto

- Proyecto Java con Spring Boot, Maven y Java 17.
- Mantén el código dentro del paquete `es.iesjuanbosco.gestionaulasspring` y sigue la organización existente (`controllers`, `entities` y `repositories`).
- Usa `AulaController` y `AulaRepository` como referencias para mantener el estilo del proyecto.
- La entidad `Profesor` contiene `id` autogenerado, `nombre`, `asignaturas`, `director` y `jefeEstudios`. `asignaturas` es una colección de valores persistida con `@ElementCollection`.

## API de profesores

- Usa `/profesores` para listar todos los profesores y crear uno; usa `/profesores/{id}` para buscar, actualizar o eliminar por identificador.
- El repositorio debe extender `JpaRepository<Profesor, Long>`.
- Mantén las respuestas HTTP coherentes: `200 OK` para lecturas y actualizaciones correctas, `201 Created` al crear, `204 No Content` al eliminar y `404 Not Found` si no existe el identificador solicitado.
- Al actualizar, conserva el identificador de la entidad existente y copia los campos editables del cuerpo de la petición.

## Datos JSON

- Guarda todos los archivos JSON de datos y ejemplos en la carpeta raíz `data/`, organizados por entidad (por ejemplo, `data/aulas.json` y `data/profesores.json`). No los dejes en la raíz del proyecto.
- Cada objeto JSON debe seguir los campos de su entidad.
- No incluyas `id` en los objetos de `data/profesores.json`: la base de datos lo genera automáticamente. Los datos de aula pueden incluir identificadores según su uso de ejemplo existente.
- Las asignaturas de los ejemplos de profesores deben ser propias del ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM), como Programación, Bases de Datos, Acceso a Datos, Desarrollo de Interfaces, Sistemas Informáticos o Programación Multimedia y Dispositivos Móviles.
- En `data/franjashorarias.json`, representa horas en formato ISO (`HH:mm:ss`) y fechas como `YYYY-MM-DD`. El horario actual tiene seis franjas reservables de 55 minutos por día entre las 08:30 y las 14:30; deja vacío el intervalo de recreo entre las 11:15 y las 11:45, sin crear una franja reservable para él. No uses 14:30 como hora de inicio ni 08:30 como hora de fin. Las fechas parten del 26 de octubre de 2026 y avanzan de forma consecutiva.
- Conserva JSON válido después de cualquier modificación.

## Ejecución y recarga

- Spring Boot DevTools ya está incluido en `pom.xml`. Detecta cambios en clases compiladas y reinicia el contexto de la aplicación; la recompilación de los fuentes corresponde al IDE.
- Para que IntelliJ compile al guardar mientras la aplicación está en ejecución, activa **Build project automatically** y `compiler.automake.allow.when.app.running` en el Registry de IntelliJ.
- No añadas dependencias duplicadas para DevTools.

## Documentación del proyecto

- Mantén `README.md` como documentación vigente del proyecto. Debe explicar cada entidad y los campos que contiene, la función de cada controlador, todos los endpoints implementados (incluidos filtros), métodos HTTP, rutas y respuestas relevantes, además de ejemplos JSON cuando ayuden a usar la API.
- Cada vez que se añada o cambie una entidad, repositorio, controlador o endpoint, actualiza el README en lugar de dejar documentación obsoleta o duplicada. Describe fielmente el comportamiento actual y señala las discrepancias conocidas entre la intención CRUD y el comportamiento del código.
- Completa este `AGENTS.md` cuando el usuario dé instrucciones de proyecto que deban mantenerse para tareas futuras. Conserva las instrucciones todavía vigentes, actualízalas cuando cambien las entidades o API, y no registres como reglas permanentes peticiones puntuales que ya se completaron.
