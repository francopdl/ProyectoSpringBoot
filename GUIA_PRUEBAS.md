# 🧪 Guía de Prueba - Plataforma SaaS

## Pre-requisitos
- PostgreSQL ejecutándose con BD "ProyectoSpring"
- Aplicación ejecutándose en http://localhost:8080
- Firefox, Chrome o cualquier navegador moderno

## Prueba 1: Registro de Usuario

### Pasos:
1. Accede a http://localhost:8080
2. Te redirige automáticamente a http://localhost:8080/login
3. Haz clic en **"Crear cuenta"**
4. Completa el formulario:
   - **Nombre**: Juan
   - **Apellido**: Pérez
   - **Email**: juan@ejemplo.com
   - **Contraseña**: Contraseña123
   - **Plan**: BASIC
5. Haz clic en **"Crear Cuenta"**

### Resultado Esperado:
✅ Se crea el usuario en la BD
✅ Se crea automáticamente un Perfil vacío
✅ Se crea una Suscripción con tipoPlan = BASIC
✅ Se genera una Factura con monto $9.99
✅ Te inicia sesión automáticamente
✅ Ves el dashboard con mensaje: **"Hola Juan, tu plan es: BASIC"**
✅ En la tabla `usuarios_aud` se registra la creación

---

## Prueba 2: Verificar Datos en BD

### Conexión a PostgreSQL (PgAdmin4)
```
Host: localhost
Usuario: Franco
Contraseña: 1234
BD: ProyectoSpring
```

### Ejecutar Queries:

#### Ver usuario creado:
```sql
SELECT * FROM usuarios WHERE email = 'juan@ejemplo.com';
```
**Esperado**: 1 registro con nombre='Juan', apellido='Pérez'

#### Ver perfil:
```sql
SELECT * FROM perfil WHERE usuario_id = 1;
```
**Esperado**: 1 registro

#### Ver suscripción:
```sql
SELECT * FROM suscripciones WHERE usuario_id = 1;
```
**Esperado**: 1 registro con tipoPlan='BASIC', estado='ACTIVA'

#### Ver factura:
```sql
SELECT * FROM facturas WHERE usuario_id = 1;
```
**Esperado**: 1 registro con monto=9.99, estado='PENDIENTE'

#### Ver auditoría (registro de cambios):
```sql
SELECT * FROM usuarios_aud WHERE id = 1;
```
**Esperado**: 1 registro con los datos del usuario

---

## Prueba 3: Login con Usuario Creado

### Pasos:
1. Cierra la sesión (click en "Cerrar Sesión")
2. Serás redirigido a /login
3. Completa:
   - **Email**: juan@ejemplo.com
   - **Contraseña**: Contraseña123
4. Haz clic en **"Iniciar Sesión"**

### Resultado Esperado:
✅ Login exitoso
✅ Redirige a /dashboard
✅ Ves nuevamente: **"Hola Juan, tu plan es: BASIC"**

---

## Prueba 4: Cambiar de Plan

### Pasos:
1. En el dashboard, en la sección **"Cambiar Plan"**
2. Selecciona: **PREMIUM** ($29.99/mes)
3. Haz clic en **"Cambiar Plan"**

### Resultado Esperado:
✅ Se actualiza la suscripción
✅ El mensaje ahora dice: **"Hola Juan, tu plan es: PREMIUM"**
✅ En `suscripciones_aud` se registra el cambio:
```sql
SELECT * FROM suscripciones_aud WHERE id = 1 ORDER BY rev;
-- Mostrará 2 filas: una con BASIC (rev 1) y otra con PREMIUM (rev 2)
```

### Auditoría en Detalle:
```sql
SELECT 
    s.id,
    s.usuario_id,
    s.tipoPlan,
    s.estado,
    sa.rev,
    r.revtstmp
FROM suscripciones_aud sa
JOIN suscripciones s ON s.id = sa.id
JOIN revisions r ON r.rev = sa.rev
WHERE s.usuario_id = 1
ORDER BY sa.rev;
```

---

## Prueba 5: Otro Usuario (Plan Premium)

### Pasos:
1. Cierra sesión
2. Crea otro usuario:
   - **Nombre**: María
   - **Apellido**: García
   - **Email**: maria@ejemplo.com
   - **Contraseña**: Segura456
   - **Plan**: PREMIUM

### Resultado Esperado:
✅ Nuevo usuario creado
✅ Dashboard: **"Hola María, tu plan es: PREMIUM"**
✅ Factura de $29.99 generada

---

## Prueba 6: Tercer Usuario (Plan Enterprise)

### Pasos:
1. Cierra sesión
2. Crea otro usuario:
   - **Nombre**: Carlos
   - **Apellido**: López
   - **Email**: carlos@ejemplo.com
   - **Contraseña**: Admin789
   - **Plan**: ENTERPRISE

### Resultado Esperado:
✅ Usuario con plan más alto
✅ Dashboard: **"Hola Carlos, tu plan es: ENTERPRISE"**
✅ Factura de $99.99 generada

---

## Prueba 7: Validar Integridad de Datos

### Ver todos los usuarios:
```sql
SELECT id, nombre, apellido, email FROM usuarios ORDER BY fechaRegistro;
```
**Esperado**: 3 usuarios (Juan, María, Carlos)

### Ver todas las suscripciones:
```sql
SELECT u.nombre, s.tipoPlan, s.estado FROM suscripciones s
JOIN usuarios u ON s.usuario_id = u.id
ORDER BY s.usuario_id;
```
**Esperado**: 
- Juan -> PREMIUM (cambió de BASIC)
- María -> PREMIUM
- Carlos -> ENTERPRISE

### Ver todas las facturas:
```sql
SELECT u.nombre, f.monto, f.estado FROM facturas f
JOIN usuarios u ON f.usuario_id = u.id
ORDER BY f.usuario_id;
```

---

## Prueba 8: Probar Validaciones

### Intentar registro con email duplicado:
1. Intenta registrarte con email: juan@ejemplo.com
2. **Resultado esperado**: Error "El email ya está registrado"

### Intentar login con contraseña incorrecta:
1. Intenta login con: juan@ejemplo.com / PasswordIncorrecta
2. **Resultado esperado**: Error "Email o contraseña incorrectos"

---

## Prueba 9: Ver Historial de Auditoría Completo

```sql
-- Ver TODAS las revisiones
SELECT rev, revtstmp FROM revisions ORDER BY rev;

-- Ver qué cambió en cada revisión
SELECT * FROM usuarios_aud WHERE rev IN (SELECT rev FROM revisions) ORDER BY rev;
SELECT * FROM suscripciones_aud WHERE rev IN (SELECT rev FROM revisions) ORDER BY rev;

-- Historial específico de cambios de plan de Juan
SELECT 
    u.nombre,
    sa.tipoPlan AS 'Plan',
    r.revtstmp AS 'Fecha/Hora del Cambio',
    sa.rev AS 'Número de Revisión'
FROM suscripciones_aud sa
JOIN usuarios u ON sa.usuario_id = u.id
JOIN revisions r ON sa.rev = r.rev
WHERE u.email = 'juan@ejemplo.com'
ORDER BY sa.rev;
```

---

## Prueba 10: Verificar Herencia de Pago (Opcional)

Si quisieras probar los tipos de pago (para desarrollo futuro):

```sql
-- Crear un pago de tarjeta (requeriría API adicional)
-- Actualmente los pagos se crearían mediante un servicio PagoService

-- Ver estructura de herencia
SELECT table_name FROM information_schema.tables 
WHERE table_name LIKE 'pagos%' 
ORDER BY table_name;
-- Esperado: pagos, pagos_tarjeta, pagos_paypal, pagos_transferencia
```

---

## Checklist de Validación

- [ ] PostgreSQL conectado correctamente
- [ ] Base de datos "ProyectoSpring" existe
- [ ] Aplicación compila sin errores
- [ ] Aplicación inicia en http://localhost:8080
- [ ] Puedes registrar usuarios
- [ ] Puedes iniciar sesión
- [ ] El dashboard muestra el nombre y plan correcto
- [ ] Puedes cambiar de plan
- [ ] Las facturas se generan automáticamente
- [ ] El historial de auditoría registra los cambios
- [ ] Las validaciones funcionan (email duplicado, contraseña incorrecta)

---

## Troubleshooting

### "Error de conexión a BD"
- Verifica que PostgreSQL está ejecutándose
- Verifica que la BD "ProyectoSpring" existe
- Verifica las credenciales en application.properties

### "Port 8080 already in use"
- Cambia en application.properties: `server.port=8081`

### "Tabla no encontrada"
- Asegúrate de que `spring.jpa.hibernate.ddl-auto=update`
- Reinicia la aplicación

### "Las tablas _aud no aparecen"
- Son creadas automáticamente por Hibernate Envers
- Intenta cambiar un plan y luego búscalas

---

**¡Diviértete probando la plataforma SaaS!** 🎉
