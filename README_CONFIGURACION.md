# Plataforma SaaS - Guía de Configuración e Instalación

## Descripción
Esta es una plataforma SaaS completa desarrollada con Spring Boot, JPA, Hibernate Envers y PostgreSQL. Permite que los usuarios se registren, seleccionen un plan (Basic, Premium, Enterprise) y gestionen sus suscripciones.

## Características Principales
✅ Autenticación y registro de usuarios
✅ Elección de planes (Basic, Premium, Enterprise) con auditoría completa
✅ Gestión de suscripciones
✅ Generación automática de facturas
✅ Auditoría con Hibernate Envers (historial de cambios)
✅ Herencia de tablas para diferentes tipos de pago (Tarjeta, PayPal, Transferencia)
✅ Vistas funcionales con Thymeleaf

## Requisitos Previos
- Java 21 o superior
- PostgreSQL 12 o superior
- Maven (incluido como wrapper)

## Configuración de la Base de Datos

### 1. Crear la base de datos PostgreSQL

Abre PgAdmin4 o ejecuta en psql:

```sql
CREATE DATABASE "ProyectoSpring" 
    WITH ENCODING 'UTF8' 
    OWNER Franco;
```

### 2. Crear el usuario (si no existe)
```sql
CREATE USER Franco WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE "ProyectoSpring" TO Franco;
```

### 3. Verificar conexión
La aplicación creará automáticamente todas las tablas al iniciar.

## Pasos para Ejecutar la Aplicación

### Opción 1: Usar Maven Wrapper (Recomendado)
```bash
cd "c:\Users\franc\OneDrive\Desktop\ProyectoSaS"
.\mvnw.cmd spring-boot:run
```

### Opción 2: Compilar y ejecutar
```bash
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

## Acceder a la Aplicación

1. Abre tu navegador e ingresa a: `http://localhost:8080`
2. Serás redirigido automáticamente al login

## Flujo de Uso

### Crear una Nueva Cuenta
1. Haz clic en "Crear cuenta" en la página de login
2. Completa el formulario con:
   - Nombre
   - Apellido
   - Email
   - Contraseña
   - Plan (BASIC, PREMIUM, ENTERPRISE)
3. Haz clic en "Crear Cuenta"
4. Se te iniciará sesión automáticamente

### Ver tu Panel de Control
- Una vez logueado, verás un mensaje: "Hola [nombre], tu plan es: [PLAN]"
- Podrás cambiar tu plan en la sección "Cambiar Plan"

### Cerrar Sesión
- Haz clic en "Cerrar Sesión" en el dashboard

## Estructura de Entidades

### Usuario
- id (Long)
- nombre (String)
- apellido (String)
- email (String, unique)
- password (String)
- fechaRegistro (LocalDateTime)
- Relaciones: Perfil, Suscripción

### Perfil
- id (Long)
- usuario_id (FK)
- telefono (String, nullable)
- empresa (String, nullable)
- pais (String, nullable)
- fechaCreacion (LocalDateTime)
- fechaActualizacion (LocalDateTime)

### Suscripción
- id (Long)
- usuario_id (FK)
- tipoPlan (Enum: BASIC, PREMIUM, ENTERPRISE)
- estado (Enum: ACTIVA, CANCELADA, MOROSA, SUSPENDIDA)
- fechaInicio (LocalDateTime)
- fechaProximoRenovacion (LocalDateTime)
- fechaCancelacion (LocalDateTime, nullable)

### Plan
- id (Long)
- nombre (String, unique)
- precio (Double)
- descripcion (String)
- limiteUsuarios (Integer)
- limiteProyectos (Integer)

### Factura
- id (Long)
- usuario_id (FK)
- suscripcion_id (FK)
- monto (BigDecimal)
- numeroFactura (String, unique, auto-generado)
- estado (String: PENDIENTE, PAGADA)
- fechaEmision (LocalDateTime)
- fechaVencimiento (LocalDateTime)

### Pago (Base con herencia JOINED)
- id (Long)
- factura_id (FK)
- tipoPago (Enum: TARJETA_CREDITO, PAYPAL, TRANSFERENCIA)
- monto (BigDecimal)
- estado (String)
- fechaPago (LocalDateTime)

### PagoTarjeta (Extiende Pago)
- numeroTarjeta (String)
- titular (String)
- fechaVencimiento (String)

### PagoPayPal (Extiende Pago)
- cuentaPayPal (String)
- transactionId (String)

### PagoTransferencia (Extiende Pago)
- numeroConversacion (String)
- banco (String)
- titularCuenta (String)

## Auditoría con Hibernate Envers

Todas las entidades están anotadas con `@Audited`, lo que permite rastrear:
- Quién cambió de plan
- Cuándo se realizó el cambio
- Historial completo de cambios

Las tablas de auditoría se crean automáticamente con el sufijo `_aud`:
- usuarios_aud
- suscripciones_aud
- facturas_aud
- etc.

## Propiedades de Configuración (application.properties)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ProyectoSpring
spring.datasource.username=Franco
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```

## Solución de Problemas

### Error de Conexión a BD
- Verifica que PostgreSQL está ejecutándose
- Valida las credenciales en application.properties
- Confirma que la base de datos "ProyectoSpring" existe

### Tablas no se crean automáticamente
- Asegúrate de que `spring.jpa.hibernate.ddl-auto=update` está en application.properties
- Reinicia la aplicación

### Puerto 8080 ocupado
- Cambia el puerto en application.properties: `server.port=8081`

## Prueba Rápida

1. Registra un usuario con email: `usuario@prueba.com`, contraseña: `1234`, plan: BASIC
2. Inicia sesión con esas credenciales
3. Verás: "Hola [nombre], tu plan es: BASIC"
4. Intenta cambiar a Premium

## Notas Importantes

- Las contraseñas se guardan en texto plano (en producción, usar bcrypt)
- Las facturas se generan automáticamente al crear una suscripción
- El sistema calcula automáticamente la próxima fecha de renovación (1 mes desde la suscripción)
- Todos los datos se persisten en PostgreSQL

## Dependencias Principales

- Spring Boot 4.0.2
- Spring Data JPA
- Hibernate Envers
- PostgreSQL 42.7.1
- Thymeleaf
- Lombok

## Autor
Franco

## Fecha
Febrero 5, 2026
