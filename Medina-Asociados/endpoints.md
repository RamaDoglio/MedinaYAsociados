
# Endpoints - Medina & Asociados

---

## AuthController (`/api/auth`)

### `POST /api/auth/register`
**Roles:** Público

**Request body (RegisterDTO):**
```json
{
  "nombre": "string",
  "apellido": "string",
  "dni": "string",
  "direccion": {
    "calle": "string",
    "numeroCalle": "integer",
    "dpto": "string",
    "piso": "string",
    "localidad": "long",
    "provincia": "string (default: \"Córdoba\")"
  },
  "telefono": "string",
  "email": "string",
  "password": "string",
}
```

**Response `200 OK` (MensajeResponse):**
```json
{
  "mensaje": "string"
}
```

---

### `POST /api/auth/login`
**Roles:** Público

**Request body (LogInRequest):**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response `200 OK` (Response):**
```json
{
  "statusCode": "int",
  "message": "string",
  "token": "string",
  "roles": ["string"],
  "expirationTime": "string",
  "user": { "UsuarioDTO" },
  "booking": { "TurnoDTO" },
  "data": {}
}
```

---

### `POST /api/auth/logout`
**Roles:** Público (requiere header Authorization)

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK` (MensajeResponse):**
```json
{
  "mensaje": "string"
}
```

---

## AbogadoController (`/api/abogados`)

### `POST /api/abogados/{idUsuario}`
**Roles:** Administrador logueado

**Request body (AbogadoDTO):**
```json
{
  "matricula": "string",
  "especialidadesAbogado": ["long"]
}
```

**Response `200 OK` (AbogadoDTO):**
```json
{
  "idAbogado": "long",
  "idUsuario": "long",
  "matricula": "string",
  "especialidadesAbogado": ["long"]
}
```

---

### `GET /api/abogados`
**Roles:** Sin restricción, que el usuario este logueado

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<AbogadoDTO>):**
```json
{
  "content": [{ "AbogadoDTO" }],
  "totalElements": "long",
  "totalPages": "int",
  "number": "int",
  "size": "int"
}
```

---

### `GET /api/abogados/{id}`
**Roles:** Sin restricción

**Response `200 OK` (AbogadoDTO):**
```json
{
  "idAbogado": "long",
  "idUsuario": "long",
  "matricula": "string",
  "especialidadesAbogado": ["long"]
}
```

---

### `PATCH /api/abogados/{id}/matricula`
**Roles:** Admin

**Request body (AbogadoMatriculaDTO):**
```json
{
  "matricula": "string"
}
```

**Response `200 OK` (AbogadoDTO)**

---

### `PUT /api/abogados/{idAbogado}/especialidades`
**Roles:** Admin

**Request body (AbogadoEspecialidadesDTO):**
```json
{
  "especialidadesAbogado": ["long"]
}
```

**Response `200 OK` (AbogadoDTO)**

---

### `DELETE /api/abogados/{id}`
**Roles:** Admin

**Response `204 No Content`**

---

### `GET /api/abogados/especialidad/{idEspecialidad}`
**Roles:** Sin restricción, Que este logueado

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<AbogadoDTO>)**

---

### `GET /api/abogados/{idAbogado}/horarios-disponibles`
**Roles:** Sin restricción, Que este logueado

**Query params:** `?fecha=yyyy-MM-dd`

**Response `200 OK` (List<LocalTime>):**
```json
["HH:mm:ss", "HH:mm:ss"]
```

---

### `GET /api/abogados/{idAbogado}/disponibilidad`
**Roles:** Sin restricción, Que este logueado

**Query params:** `?fechaHora=yyyy-MM-ddTHH:mm:ss`

**Response `200 OK` (boolean)**

---

## TurnoController (`/api/turnos`)

### `POST /api/turnos`
**Roles:** Cliente, Que este logueado

**Request body (TurnoCreateRequest):**
```json
{
  "idCliente": "long",
  "idAbogado": "long",
  "idEspecialidad": "long",
  "horarioTurno": "yyyy-MM-ddTHH:mm:ss",
  "observacionesCliente": "string",
  "cobro": {
    "importeTotal": "float",
    "idEstado": "long"
  }
}
```

**Response `200 OK` (TurnoDTO):**
```json
{
  "idTurno": "long",
  "idEstado": "long",
  "idEspecialidad": "long",
  "idCobro": "long",
  "observacionesCliente": "string",
  "observacionesAbogado": "string",
  "horarioTurno": "yyyy-MM-ddTHH:mm:ss",
  "abogadoTurno": "long",
  "usuarioTurno": "long",
  "historialTurno": ["long"]
}
```

---

### `POST /api/turnos/{id}/pagar`
**Roles:** Dueño del turno (CLIENTE), logueado

**Response `200 OK` (String):**
```
"initPoint de Mercado Pago"
```

---

### `GET /api/turnos`
**Roles:** ADMIN logueado

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<Turno>):** entidad completa con relaciones

---

### `GET /api/turnos/{id}`
**Roles:** ADMIN logueado

**Response `200 OK` (Turno):** entidad completa

---

### `PUT /api/turnos/{id}` (Borrar)
**Roles:** Sin restricción

**Request body (Turno):**
```json
{
  "observacionesCliente": "string",
  ...
}
```

**Response `200 OK` (TurnoDTO)**

---

### `DELETE /api/turnos/{id}`(Borrar)
**Roles:** Sin restricción

**Response `204 No Content`**

---

### `PUT /api/turnos/{id}/reprogramar`
**Roles:** Si es Abogado/cliente participante, logueado

**Query params:** `?fecha=yyyy-MM-ddTHH:mm:ss`

**Response `200 OK` (TurnoDTO)**

---

### `POST /api/turnos/{id}/cancelar`
**Roles:** Si Abogado/cliente participante, logueado

**Response `200 OK` (TurnoDTO)**

---

### `POST /api/turnos/{id}/noAsistio`
**Roles:** ABOGADO (dueño del turno)

**Response `200 OK` (TurnoDTO)**

---

### `POST /api/turnos/{id}/enCurso`
**Roles:** ABOGADO (dueño del turno)

**Response `200 OK` (TurnoDTO)**

---

### `POST /api/turnos/{id}/finalizar`
**Roles:** ABOGADO (dueño del turno)

**Response `200 OK` (TurnoDTO)**

---

### `GET /api/turnos/cliente/{idCliente}`
**Roles:** CLIENTE (dueño)

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<TurnoListadoDTO>):**
```json
{
  "content": [{
    "idTurno": "long",
    "persona": "string",
    "fechaHora": "yyyy-MM-ddTHH:mm:ss",
    "estado": "string"
  }],
  "totalElements": "long",
  "totalPages": "int",
  "number": "int",
  "size": "int"
}
```

---

### `GET /api/turnos/abogado/{idAbogado}`
**Roles:** ABOGADO (dueño)

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<TurnoListadoDTO>)**

---

### `GET /api/turnos/{id}/detalle-cliente`
**Roles:** CLIENTE (dueño)

**Response `200 OK` (TurnoDetalleDTO):**
```json
{
  "idTurno": "long",
  "persona": "string",
  "dni": "string",
  "direccion": "string",
  "telefono": "string",
  "especialidad": "string",
  "fechaHora": "yyyy-MM-ddTHH:mm:ss",
  "observacionesCliente": "string",
  "observacionesAbogado": "string",
  "estado": "string"
}
```

---

### `GET /api/turnos/{id}/detalle-abogado`
**Roles:** ABOGADO (dueño)

**Response `200 OK` (TurnoDetalleDTO)**

---

### `PUT /api/turnos/{id}/observaciones-abogado`
**Roles:** ABOGADO (dueño)

**Request body (String):**
```
"texto de observaciones"
```

**Response `200 OK` (TurnoDTO)**

---

### `POST /api/turnos/offline`
**Roles:** ABOGADO (dueño del id)

**Request body (TurnoOfflineRequest):**
```json
{
  "idAbogado": "long",
  "idEspecialidad": "long",
  "horarioTurno": "yyyy-MM-ddTHH:mm:ss",
  "observacionesCliente": "string",
  "cliente": {
    "nombre": "string",
    "apellido": "string",
    "dni": "string",
    "telefono": "string",
    "email": "string",
    "direccion": {
      "calle": "string",
      "numeroCalle": "integer",
      "dpto": "string",
      "piso": "string",
      "localidad": "long",
      "provincia": "string"
    }
  }
}
```

**Response `201 Created` (TurnoDTO)**

---

### `POST /api/turnos/{id}/marcar-pagado`
**Roles:** Abogado (dueño)

**Response `200 OK` (TurnoDTO)**

---

## CobroController (`/api/cobros`) (Revisar)

### `POST /api/cobros`
**Roles:** Abogado (dueño) o ADMIN

**Request body (CobroDTO):**
```json
{
  "idTurno": "long",
  "importeTotal": "float",
  "idEstado": "long"
}
```

**Response `200 OK` (CobroDTO):**
```json
{
  "idCobro": "long",
  "idTurno": "long",
  "importeTotal": "float",
  "idEstado": "long"
}
```

---

### `GET /api/cobros/turno/{turnoId}`
**Roles:** Abogado/Cliente (dueño) o ADMIN

**Response `200 OK` (CobroDTO)**

---

### `GET /api/cobros/{id}` (Revisar)
**Roles:** Sin restricción

**Response `200 OK` (CobroDTO)**

---

### `PUT /api/cobros/{id}`(Revisar)
**Roles:** Sin restricción

**Request body (CobroDTO)**

**Response `200 OK` (CobroDTO)**

---

### `DELETE /api/cobros/{id}` (Borrar)
**Roles:** Sin restricción

**Response `204 No Content`**

---

## DetalleCobroController (`/api/detalle-cobros`)

### `GET /api/detalle-cobros/cobro/{cobroId}` (Revisar)
**Roles:** Sin restricción

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<DetalleCobroDTO>):**
```json
{
  "content": [{
    "idDetalleCobro": "long",
    "idCobro": "long",
    "fecha": "yyyy-MM-ddTHH:mm:ss",
    "descripcionCobro": "string",
    "subTotal": "float",
    "idTipoCobro": "long"
  }]
}
```

---

### `GET /api/detalle-cobros/{id}` (Revisar)
**Roles:** Sin restricción

**Response `200 OK` (DetalleCobroDTO)**

---

### `PUT /api/detalle-cobros/{id}` (Revisar)
**Roles:** Sin restricción

**Request body (DetalleCobroDTO):**
```json
{
  "fecha": "yyyy-MM-ddTHH:mm:ss",
  "descripcionCobro": "string",
  "subTotal": "float",
  "idTipoCobro": "long"
}
```

**Response `200 OK` (DetalleCobroDTO)**

---

### `DELETE /api/detalle-cobros/{id}` (Revisar)
**Roles:** Sin restricción

**Response `204 No Content`**

---

## EspecialidadController (`/api/especialidades`)

### `GET /api/especialidades`
**Roles:** Sin restricción, logueado

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<EspecialidadDTO>):**
```json
{
  "content": [{
    "idEspecialidad": "long",
    "nombreEspecialidad": "string",
    "descripcionEspecialidad": "string"
  }]
}
```

---

### `GET /api/especialidades/{id}`
**Roles:** Sin restricción, logueado

**Response `200 OK` (EspecialidadDTO):**
```json
{
  "idEspecialidad": "long",
  "nombreEspecialidad": "string",
  "descripcionEspecialidad": "string"
}
```

---

## UsuarioController (`/api/usuarios`)

### `POST /api/usuarios` (Revisar DTO entrante)
**Roles:** Sin restricción

**Request body (RegisterDTO):**
```json
{
  "nombre": "string",
  "apellido": "string",
  "dni": "string",
  "direccion": {
    "calle": "string",
    "numeroCalle": "integer",
    "dpto": "string",
    "piso": "string",
    "localidad": "long",
    "provincia": "string"
  },
  "telefono": "string",
  "email": "string",
  "password": "string"
}
```

**Response `200 OK` (MensajeResponse):**
```json
{
  "mensaje": "string"
}
```

---

### `GET /api/usuarios` (Revisar posiblemente borrar)
**Roles:** ABOGADO (dueño) o ADMIN (anotación hace referencia a `#idAbogado` inaccesible — posible bug)

**Query params:** `?page=0&size=10`

**Response `200 OK` (Page<UsuarioDTO>):**
```json
{
  "content": [{
    "idUsuario": "long",
    "nombre": "string",
    "apellido": "string",
    "dni": "string",
    "idDireccion": "long",
    "telefono": "string",
    "email": "string",
    "password": "string",
    "idRol": "long",
    "idTurnos": ["long"]
  }]
}
```

---

### `GET /api/usuarios/{id}`
**Roles:** Cliente (dueño) o ADMIN o Abogado, logueado

**Response `200 OK` (UsuarioDTO)**

---

### `PUT /api/usuarios/{id}` (Revisar, posiblemente borrar)
**Roles:** ADMIN

**Request body (UsuarioDTO)**

**Response `200 OK` (UsuarioDTO)**

---

### `GET /api/usuarios/buscar-por-dni`
**Roles:** ABOGADO o ADMIN

**Query params:** `?dni=string&page=0&size=10`

**Response `200 OK` (Page<UsuarioDTO>)**

---

### `GET /api/usuarios/{id}/detalle`
**Roles:** ADMIN, ABOGADO, o CLIENTE (dueño)

**Response `200 OK` (Response):**
```json
{
  "statusCode": "int",
  "message": "string",
  "data": {
    "idUsuario": "long",
    "nombre": "string",
    "apellido": "string",
    "dni": "string",
    "telefono": "string",
    "email": "string",
    "direccion": { "DireccionDTO" },
    "localidad": { "LocalidadDTO" },
    "turnosPorEstado": [{ "nombre": "string", "cantidad": "long" }]
  }
}
```

---

### `DELETE /api/usuarios/{id}` (Revisar posiblemente borrar)
**Roles:** ADMIN

**Response `204 No Content`**

---

## ConfigController (`/config`)

### `GET /config/precio-turno`
**Roles:** ADMIN, logueado

**Response `200 OK` (double):**
```
1500.0
```

---

### `PUT /config/precio-turno`
**Roles:** ADMIN, logueado

**Request body (double):**
```
2500.0
```

**Response `200 OK` (String):**
```
"Precio de turno actualizado a: 2500.0"
```
