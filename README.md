# Gestión de Aulas

Aplicación desarrollada con Spring Boot para gestionar aulas, profesores, franjas horarias y reservas. La API REST usa JSON y, en local, está disponible en `http://localhost:8080`.

## Frontend

El frontend está en `frontend/` y se sirve desde la misma aplicación Spring Boot, por lo que comparte origen con la API y no requiere configuración CORS. Inicia el backend desde la raíz del proyecto y abre `http://localhost:8080`.

La interfaz permite consultar, crear y eliminar aulas, profesores, franjas horarias y reservas. Para crear una reserva, primero deben existir los registros asociados. El botón **Actualizar** vuelve a cargar los datos desde la API.

## Entidades

### Aula

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | Identificador generado automáticamente. |
| `tieneOrdenadores` | `boolean` | Indica si el aula dispone de ordenadores. |
| `capacidad` | `int` | Número de personas que caben en el aula. |

### Profesor

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | Identificador generado automáticamente. |
| `nombre` | `String` | Nombre del profesor. |
| `asignaturas` | `List<String>` | Asignaturas que imparte; se persisten como colección de valores. |
| `director` | `boolean` | Indica si es director. |
| `jefeEstudios` | `boolean` | Indica si es jefe de estudios. |

En las peticiones de creación no es necesario enviar `id`.

### FranjaHoraria

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | Identificador generado automáticamente. |
| `horaInicio` | `LocalTime` | Hora de inicio de la franja. |
| `horaFin` | `LocalTime` | Hora de finalización de la franja. |
| `dia` | `LocalDate` | Fecha de la franja. |
| `reservas` | `List<Reserva>` | Reservas asociadas; se ignora en la serialización JSON. |

### Reserva

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | Identificador generado automáticamente. |
| `profesor` | `Profesor` | Profesor asociado a la reserva (`ManyToOne`). |
| `aula` | `Aula` | Aula asociada a la reserva (`ManyToOne`). |
| `franjaHoraria` | `FranjaHoraria` | Franja horaria asociada a la reserva (`ManyToOne`). |

## Repositorios y controladores

- `AulaRepository` extiende `JpaRepository<Aula, Long>` y añade consultas por disponibilidad de ordenadores y capacidad.
- `AulaController` expone operaciones para listar y filtrar aulas, consultar por ID, crear, actualizar y eliminar.
- `ProfesorRepository` extiende `JpaRepository<Profesor, Long>` y proporciona las operaciones de persistencia para profesores.
- `ProfesorController` expone operaciones para listar, consultar por ID, crear, actualizar y eliminar profesores.
- `FranjaHorariaRepository` extiende `JpaRepository<FranjaHoraria, Long>` y proporciona las operaciones de persistencia para franjas horarias.
- `FranjaHorariaController` expone operaciones para listar, consultar por ID, crear, actualizar y eliminar franjas horarias.
- `ReservaRepository` extiende `JpaRepository<Reserva, Long>` y proporciona las operaciones de persistencia para reservas.
- `ReservaController` expone operaciones para listar, consultar por ID, crear, actualizar y eliminar reservas.

## Endpoints

### Aulas

| Método | Ruta | Función |
|---|---|---|
| `GET` | `/aulas` | Lista todas las aulas. |
| `GET` | `/aulas?tieneOrdenadores=true` | Filtra por disponibilidad de ordenadores (`true` o `false`). Si se omite el parámetro, lista todas. |
| `GET` | `/aulas?capacidad=30` | Filtra por capacidad exacta. |
| `GET` | `/aulas/{id}` | Busca un aula por su identificador. |
| `POST` | `/aulas` | Crea un aula. Responde `201 Created`. |
| `PUT` | `/aulas/{id}` | Actualiza un aula existente. Responde `200 OK` si encuentra el ID o `404 Not Found` si no existe. |
| `DELETE` | `/aulas/{id}` | Solicita la eliminación del aula. |

Ejemplo de cuerpo para `POST /aulas` o `PUT /aulas/{id}`:

```json
{
  "tieneOrdenadores": true,
  "capacidad": 30
}
```

### Profesores

| Método | Ruta | Función |
|---|---|---|
| `GET` | `/profesores` | Lista todos los profesores. |
| `GET` | `/profesores?jefeEstudios=true` | Filtra profesores por el valor booleano de `jefeEstudios`. Usa `true` o `false`. |
| `GET` | `/profesores/{id}` | Busca un profesor por su identificador. Responde `404 Not Found` si no existe. |
| `POST` | `/profesores` | Crea un profesor. Solo puede existir una persona con cada cargo y un profesor no puede ser director y jefe de estudios a la vez. Responde `201 Created`, `400 Bad Request` si se solicitan ambos cargos y `409 Conflict` si el cargo ya está ocupado. |
| `PUT` | `/profesores/{id}` | Actualiza un profesor existente. Responde `200 OK` o `404 Not Found` si no existe. |
| `DELETE` | `/profesores/{id}` | Elimina un profesor existente. Responde `204 No Content` o `404 Not Found` si no existe. |

Ejemplo de cuerpo para `POST /profesores` o `PUT /profesores/{id}`:

```json
{
  "nombre": "Marta Lopez",
  "asignaturas": ["Programacion", "Bases de Datos"],
  "director": false,
  "jefeEstudios": false
}
```

En el frontend los cargos se eligen con opciones mutuamente excluyentes. Si el POST se rechaza por un cargo ya ocupado o por seleccionar ambos cargos, se muestra una alerta.

### Franjas horarias

| Método | Ruta | Función |
|---|---|---|
| `GET` | `/franjashorarias` | Lista todas las franjas horarias. |
| `GET` | `/franjashorarias/{id}` | Busca una franja por su identificador. Responde `404 Not Found` si no existe. |
| `POST` | `/franjashorarias` | Crea una franja. Responde `201 Created`. |
| `PUT` | `/franjashorarias/{id}` | Actualiza sus horas y fecha, conservando el identificador de la ruta. Responde `200 OK` o `404 Not Found`. |
| `DELETE` | `/franjashorarias/{id}` | Elimina una franja. Responde `204 No Content` o `404 Not Found` si no existe. |

Ejemplo de cuerpo para crear o actualizar una franja horaria:

```json
{
  "horaInicio": "08:30:00",
  "horaFin": "09:25:00",
  "dia": "2026-10-26"
}
```

### Reservas

| Método | Ruta | Función |
|---|---|---|
| `GET` | `/reservas` | Lista todas las reservas. |
| `GET` | `/reservas/{id}` | Busca una reserva por su identificador. Responde `404 Not Found` si no existe. |
| `POST` | `/reservas` | Crea una reserva si la fecha de su franja no es anterior a hoy y esta no está ya reservada. Responde `201 Created`, `400 Bad Request` para fechas pasadas o datos incompletos, `404 Not Found` si la franja no existe y `409 Conflict` si ya está ocupada. |
| `PUT` | `/reservas/{id}` | Actualiza las entidades asociadas, conservando el identificador de la ruta. Responde `200 OK` o `404 Not Found`. |
| `DELETE` | `/reservas/{id}` | Elimina una reserva. Responde `204 No Content` o `404 Not Found` si no existe. |

Para crear o actualizar una reserva, envía las entidades asociadas con sus identificadores existentes:

```json
{
  "profesor": { "id": 1 },
  "aula": { "id": 1 },
  "franjaHoraria": { "id": 1 }
}
```

Cada franja horaria solo puede asociarse a una reserva. La comprobación de disponibilidad se valida en el controlador y mediante una restricción única en la base de datos para evitar reservas duplicadas incluso ante peticiones simultáneas. La fecha comprobada es `dia` de la entidad `FranjaHoraria`.

## Observaciones sobre la implementación actual

- `AulaController.deleteById` intenta eliminar el aula y después devuelve `404 Not Found` incluso si la eliminación se realiza. Este comportamiento debería corregirse para devolver `204 No Content` cuando corresponda.
- `AulaController.update` comprueba que existe el ID de la ruta, pero guarda el objeto recibido en el cuerpo en vez de la entidad recuperada. Para que la actualización apunte al mismo registro, el `id` del cuerpo debe corresponder al de la ruta; conviene corregir el controlador para copiar los campos editables sobre la entidad recuperada.

## Datos de ejemplo

Los datos JSON de ejemplo se guardan en la carpeta `data/`:

- `data/aulas.json` contiene aulas de ejemplo.
- `data/profesores.json` contiene profesores y asignaturas relacionadas con DAM. Omite `id`, que se genera automáticamente.
- `data/franjashorarias.json` contiene seis franjas reservables de 55 minutos por día entre las 08:30 y las 14:30, para siete días consecutivos desde el 26 de octubre de 2026. El recreo de 11:15 a 11:45 queda como intervalo libre y no aparece como franja reservable.
- `data/reservas.json` contiene reservas de ejemplo con los datos completos de los profesores, aulas y franjas horarias asociados; los identificadores deben corresponder a registros existentes.
