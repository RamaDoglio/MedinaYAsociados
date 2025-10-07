# Tareas

- [x] Revisar porque los id de los abogados es el mismo que el de usuario. Ej: idUsuario:8, idAbogado:8
- [x] Revisar porque los id no se asignan en orden ascendente ej: Ante una peticion correcta se asigna el idUsuario=1 y luego de hacer una peticion incorrecta y luego de una correcta se asigna el idUsuario=3
- [x] Al actualizar un abogado, Enviar solo el id del abogado y las especialidades que se quieran agregar o eliminar, actualmente se envia toda la entidad

## Service y Controller Faltantes

- [x] especialidad
- [x] Estado
- [x] Historial Turno
- [x] Horario Turno
- [ ] Localidad
- [x] TipoCobro
- [ ] Turno
- [x] Permiso
- [x] Rol

# Notas Copilot

## Crear Service + Controller:

1. Usuario: Registro, actualización, activación, seguridad básica.
2. Abogado: Gestión de matrícula, especialidades, disponibilidad.
3. Turno (si existe): Reserva, cancelación, cambios de estado, validaciones de agenda.
4. Cobro: Estados de pago (seña vs total), validaciones de montos.
5. DetalleCobro (solo si se consulta/edita aparte del Cobro; si no, manejar dentro de CobroService).
6. Especialidad: Si el frontend permite administrar la lista (ABM). Si es casi fija, solo Service opcional o precarga.

## Solo Service (sin Controller público) si:

- Estado: Si son catálogos internos filtrados por ámbito y no cambian mucho. Si el frontend debe listarlos dinámicamente -> agregar Controller de solo lectura.
- TipoCobro: Igual que Estado (catálogo). Controller solo lectura opcional.

## No crear (o embebido en otros):

- Tablas de relación (ej: Abogado_Especialidad si fuera intermedia explícita).
- Value objects (ej: direcciones, montos desglosados) si los tienes.

## Regla rápida:

- Tiene reglas de negocio propias o transitions: sí (Service).
- El frontend lo gestiona directo: Controller.
- Solo lookup/catálogo estable: precargar o Controller GET-only.