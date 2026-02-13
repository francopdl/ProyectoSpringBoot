# ProyectoSaS - Sistema de Gestión de Suscripciones SaaS

## 🎯 Descripción General

ProyectoSaS es una aplicación Spring Boot completa para la gestión de suscripciones SaaS con funcionalidades avanzadas incluyendo:

- ✅ **Renovación automática de suscripciones**
- ✅ **Cálculo de impuestos dinámico por país**
- ✅ **Gestión completa del ciclo de vida de suscripciones**
- ✅ **Filtrado avanzado de facturas (por fecha, monto, estado)**
- ✅ **Vistas dinámicas de facturación**
- ✅ **Panel de auditoría administrativo con historial de cambios**
- ✅ **Sistema de autenticación con sesiones**

## 🏗️ Arquitectura del Proyecto

```
ProyectoSaS/
├── src/main/java/com/example/ProyectoSaS/
│   ├── Models/                  # Entidades JPA con auditoría Envers
│   │   ├── Usuario.java
│   │   ├── Suscripcion.java
│   │   ├── Factura.java
│   │   ├── Pago.java
│   │   ├── Plan.java
│   │   └── Perfil.java
│   ├── Services/                # Servicios de negocio
│   │   ├── UsuarioService.java
│   │   ├── SuscripcionService.java
│   │   ├── FacturaService.java
│   │   ├── TaxService.java     # ⭐ Nuevo: Cálculo de impuestos
│   │   └── SubscriptionRenewalService.java  # ⭐ Nuevo: Renovación automática
│   ├── Repositories/            # Acceso a datos JPA
│   │   ├── UsuarioRepository.java
│   │   ├── SuscripcionRepository.java
│   │   ├── FacturaRepository.java  # ⭐ Extendido: Métodos de filtrado
│   │   └── Otros...
│   ├── TaskController/
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   ├── FacturacionController.java  # ⭐ Nuevo
│   │   └── AuditoriaController.java    # ⭐ Nuevo
│   └── ProyectoSaSApplication.java
├── src/main/resources/
│   ├── application.properties    # Configuración de BD y scheduling
│   └── templates/
│       ├── login.html
│       ├── registro.html
│       ├── dashboard.html        # ⭐ Actualizado
│       ├── facturacion.html      # ⭐ Nuevo
│       ├── detalle-factura.html  # ⭐ Nuevo
│       ├── auditoria.html        # ⭐ Nuevo
│       ├── detalle-auditoria.html # ⭐ Nuevo
│       ├── historial-usuario.html # ⭐ Nuevo
│       └── reporte-auditoria.html # ⭐ Nuevo
└── pom.xml
```

## ✨ Nuevas Características Implementadas

### 1. **Servicio de Cálculo de Impuestos (TaxService)**

Calcula impuestos dinámicamente basado en el país del usuario.

**Países soportados y tasas IVA:**
- España (ES): 21%
- Alemania (DE): 19%
- Francia (FR): 20%
- Italia (IT): 22%
- Portugal (PT): 23%
- Reino Unido (EN): 20%
- México (MX): 16%
- Argentina (AR): 21%
- Colombia (CO): 19%
- Chile (CL): 19%
- Estados Unidos (US): 0%
- Canadá (CA): 5%

**Métodos principales:**
```java
// Obtener tasa de impuesto para un país
taxService.obtenerTasaImpuesto(String pais);

// Calcular monto del impuesto
taxService.calcularMontoImpuesto(BigDecimal montoBase, String pais);

// Calcular total incluyendo impuestos
taxService.calcularTotalConImpuesto(BigDecimal montoBase, String pais);
```

### 2. **Servicio de Renovación Automática (SubscriptionRenewalService)**

Ejecuta automáticamente cada día a las 2:00 AM para renovar suscripciones vencidas.

**Funcionalidades:**
- Chequea suscripciones activas vencidas
- Genera facturas de renovación automáticamente
- Calcula impuestos según país del usuario
- Actualiza fecha de próxima renovación
- Maneja excepciones y proporciona logging

**Métodos principales:**
```java
// Renovar automáticamente (ejecuta diariamente)
subscriptionRenewalService.renovarSuscripcionesVencidas();

// Renovar suscripción específica
subscriptionRenewalService.renovarSuscripcion(Suscripcion suscripcion);

// Cancelar suscripción
subscriptionRenewalService.cancelarSuscripcion(Long usuarioId);

// Obtener estado de renovación
subscriptionRenewalService.obtenerEstadoRenovacion(Long suscripcionId);
```

### 3. **Modelo Factura Extendido**

Se agregaron campos para cálculo de impuestos:
```java
private BigDecimal porcentajeImpuesto;  // % de IVA
private BigDecimal montoImpuesto;       // Monto del impuesto
private BigDecimal totalConImpuesto;    // Total con impuestos incluidos
```

### 4. **Repositorio FacturaRepository Extendido**

Métodos de filtrado avanzado usando JPA @Query:

```java
// Filtrar por rango de fechas
findByUsuarioIdAndFechaRange(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

// Filtrar por rango de montos
findByUsuarioIdAndMontoRange(Long usuarioId, BigDecimal min, BigDecimal max);

// Filtrar por estado
findByUsuarioIdAndEstado(Long usuarioId, String estado);

// Filtrar por múltiples criterios
findByUsuarioIdEstadoAndFechaRange(Long usuarioId, String estado, LocalDateTime inicio, LocalDateTime fin);

// Obtener todas las facturas ordenadas
findAllFacturasOrdenadas();

// Filtrar por estado global
findByEstado(String estado);
```

### 5. **Controlador de Facturación (FacturacionController)**

Proporciona vistas y funcionalidades de facturación:

**Endpoints:**
- `GET /facturacion` - Ver todas las facturas del usuario
- `POST /facturacion/filtrar-fechas` - Filtrar por rango de fechas
- `POST /facturacion/filtrar-montos` - Filtrar por rango de montos
- `POST /facturacion/filtrar-estado` - Filtrar por estado
- `GET /facturacion/{facturaId}` - Ver detalle de una factura

**Características:**
- Tabla interactiva con todas las facturas
- Desglose de impuestos por país
- Cálculo automático de totales
- Filtros dinámicos
- Vista de detalle con información completa

### 6. **Controlador de Auditoría (AuditoriaController)**

Panel administrativo para auditoría del sistema:

**Endpoints:**
- `GET /admin/auditoria` - Panel principal de auditoría
- `POST /admin/auditoria/filtrar-fechas` - Filtrar registros por fechas
- `POST /admin/auditoria/filtrar-operacion` - Filtrar por tipo de operación
- `GET /admin/auditoria/factura/{facturaId}` - Ver detalles de auditoría de factura
- `GET /admin/auditoria/usuario/{usuarioId}` - Ver historial de usuario
- `GET /admin/auditoria/reporte` - Generar reporte completo

**Características:**
- Vista completa de todas las operaciones del sistema
- Estadísticas en tiempo real
- Filtrado por fecha y tipo de operación
- Historial detallado de cada usuario
- Reportes generables e imprimibles

### 7. **Vistas Thymeleaf Dinámicas**

#### **facturacion.html**
- Tabla con todas las facturas del usuario
- Desglose de impuestos calculados
- Tres filtros independientes (fecha, monto, estado)
- Tarjetas de estadísticas
- Información del usuario segmentada

#### **detalle-factura.html**
- Vista completa de una factura individual
- Detalles del usuario emisor
- Desglose económico completo
- Información fiscal por país
- Opción para imprimir factura

#### **auditoria.html**
- Panel administrativo de auditoría
- Table con todas las operaciones del sistema
- Estadísticas generales (operaciones activas, pendientes)
- Filtros por fecha y tipo de operación
- Acceso a detalles y historial por usuario

#### **detalle-auditoria.html**
- Información completa de una operación
- Datos del usuario asociado
- Historial de eventos en timeline
- Registro de cambios de estado

#### **historial-usuario.html**
- Historial de actividades de un usuario específico
- Tabla de facturas asociadas
- Estadísticas (pagadas, pendientes)
- Enlaces a detalles individuales

#### **reporte-auditoria.html**
- Reporte ejecutivo del sistema
- Estadísticas generales
- Análisis de cumplimiento
- Recomendaciones
- Imprimible en PDF

#### **dashboard.html** (Actualizado)
- Bienvenida personalizada
- Información del plan actual
- Cards de acceso rápido a:
  - Facturación
  - Panel de Auditoría
  - Gestión de Planes
- Sesión y logout

## 🚀 Instalación y Configuración

### Requisitos Previos
- Java 21+
- Maven 3.8+
- PostgreSQL 10+
- Git

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone <url-repositorio>
cd ProyectoSaS
```

2. **Configurar la base de datos**

En `application.properties`, actualizar:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ProyectoSpring
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

Crear la base de datos PostgreSQL:
```sql
CREATE DATABASE ProyectoSpring;
```

3. **Compilar y ejecutar**

```bash
# Limpiar y compilar
./mvnw clean install -DskipTests

# Ejecutar aplicación
./mvnw spring-boot:run
```

4. **Acceder a la aplicación**

- **Login:** `http://localhost:8080/login`
- **Registro:** `http://localhost:8080/registro`
- **Dashboard:** `http://localhost:8080/dashboard`

## 📊 Flujo de Operación

### Proceso de Facturación:
1. Usuario se registra y selecciona plan
2. Sistema crea suscripción activa
3. Se genera factura inicial con impuestos calculados
4. Se establece fecha de próxima renovación (30 días después)
5. Cada día a las 2 AM:
   - Sistema verifica suscripciones vencidas
   - Genera automat. factura de renovación
   - Calcula impuestos según país
   - Actualiza fecha de próxima renovación
6. Usuario puede ver y filtrar todas sus facturas
7. Administrador puede auditar todas las operaciones

### Estructura de Base de Datos

El sistema utiliza:
- **Tablas de negocio:** usuarios, suscripciones, facturas, etc.
- **Tablas de auditoría (Envers):** usuarios_aud, suscripciones_aud, facturas_aud, etc.
- **Tabla de revisiones:** revinfo (metadatos de cambios)

## 🔒 Seguridad

- Autenticación mediante email/contraseña
- Control de sesiones con HttpSession
- Auditoría completa de cambios (Hibernate Envers)
- Validación de permisos administrativos
- Las vistas de facturación/auditoría requieren sesión activa

## 📈 Características Técnicas Avanzadas

### Herramientas y Librerías Utilizadas:
- **Spring Boot 4.0.2** - Framework web
- **Spring Data JPA** - Acceso a datos
- **Hibernate Envers** - Auditoría automática
- **PostgreSQL** - Base de datos
- **Thymeleaf** - Motor de templates
- **Lombok** - Generación automática de código
- **Maven** - Gestor de dependencias

### Patrones de Diseño Implementados:
- **MVC** - Separación de concerns
- **Repository** - Abstracción de persistencia
- **Service Layer** - Lógica de negocio centralizada
- **Dependency Injection** - Inyección de dependencias
- **Scheduled Tasks** - Tareas programadas

## 📝 Ejemplos de Uso

### Filtrar facturas por período
```java
List<Factura> facturas = facturaService.obtenerFacturasPorUsuarioYFecha(
    usuarioId, 
    LocalDateTime.of(2026, 1, 1, 0, 0, 0),
    LocalDateTime.of(2026, 1, 31, 23, 59, 59)
);
```

### Calcular impuestos para un cliente español
```java
BigDecimal monto = new BigDecimal("100.00");
BigDecimal impuesto = taxService.calcularMontoImpuesto(monto, "ES");
// Resultado: 21.00 (21% de IVA)

BigDecimal total = taxService.calcularTotalConImpuesto(monto, "ES");
// Resultado: 121.00
```

### Renovar suscripción manualmente
```java
Optional<Suscripcion> suscripccion = suscripcionRepository.findByUsuarioId(usuarioId);
if (sucs.isPresent()) {
    subscriptionRenewalService.renovarSuscripcion(suscripcion.get());
}
```

## 🎯 Funcionalidades Futuras

- [ ] Integración con métodos de pago (PayPal, Stripe)
- [ ] Notificaciones por email
- [ ] Descuentos y cupones
- [ ] Reportes personalizados
- [ ] Exportación a PDF/Excel
- [ ] API REST para terceros
- [ ] Dashboard analítico avanzado
- [ ] Soporte multi-idioma

## 👥 Contribuir

Para contribuir al proyecto:
1. Fork el repositorio
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo licencia MIT. Ver archivo `LICENSE` para más detalles.

## 📧 Contacto

Para preguntas o sugerencias, contacta a: franco@proyectosaas.com

---

**Última actualización:** Febrero 2026
**Versión:** 1.5.0 (Con renovación automática y cálculo de impuestos)
