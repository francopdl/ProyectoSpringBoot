# 🏗️ Arquitectura de la Plataforma SaaS

## 📐 Diagrama General

```
┌─────────────────────────────────────────────────────────────────┐
│                      CLIENTE (NAVEGADOR)                        │
│                    (Vistas HTML Thymeleaf)                      │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT (localhost:8080)                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           HTTP CONTROLLERS (REST + MVC)                  │  │
│  │  ┌─────────────────────┬──────────────────────────────┐  │  │
│  │  │  AuthController     │   DashboardController        │  │  │
│  │  │  /login (GET, POST) │   /dashboard (GET, POST)     │  │  │
│  │  │  /registro (GET)    │   /dashboard/cambiar-plan    │  │  │
│  │  │  /logout (GET)      │                              │  │  │
│  │  └─────────────────────┴──────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                 │                               │
│                                 ▼                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              BUSINESS LOGIC (SERVICES)                   │  │
│  │  ┌──────────────┬──────────────┬──────────────────────┐  │  │
│  │  │   Usuario    │ Suscripción  │   Factura            │  │  │
│  │  │   Service    │   Service    │   Service            │  │  │
│  │  └──────────────┴──────────────┴──────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                 │                               │
│                                 ▼                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │            DATA ACCESS LAYER (REPOSITORIES)              │  │
│  │  ┌──────────┬──────────┬──────────┬──────────┬────────┐  │  │
│  │  │ Usuario  │ Perfil   │ Suscripción│ Factura │ Pago   │  │  │
│  │  │  Repo    │   Repo   │   Repo   │   Repo   │ Repo   │  │  │
│  │  └──────────┴──────────┴──────────┴──────────┴────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                 │                               │
└─────────────────────────────────┼───────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    HIBERNATE + JPA (ORM)                        │
├─────────────────────────────────────────────────────────────────┤
│  Mapea Objetos Java ↔ Tablas Relacionales                       │
│  Genera SQL automáticamente                                     │
│  Envers: Auditoría automática de cambios                        │
└─────────────────────────────────┬───────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│              PostgreSQL Database (ProyectoSpring)               │
├─────────────────────────────────────────────────────────────────┤
│  TABLAS DE DATOS:                TABLAS DE AUDITORÍA:           │
│  ├── usuarios          →         ├── usuarios_aud              │
│  ├── perfil            →         ├── perfil_aud                │
│  ├── suscripciones     →         ├── suscripciones_aud         │
│  ├── planes            →         ├── planes_aud                │
│  ├── facturas          →         ├── facturas_aud              │
│  ├── pagos             →         ├── pagos_aud                 │
│  ├── pagos_tarjeta     →         ├── pagos_tarjeta_aud         │
│  ├── pagos_paypal      →         ├── pagos_paypal_aud          │
│  └── pagos_transferencia→        ├── pagos_transferencia_aud   │
│                                   ├── revisions                │
│                                   └── revisions_aud            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de Datos - Registro de Usuario

```
USUARIO INGRESA A /registro
           │
           ▼
RELLENA FORMULARIO
├── Nombre: "Juan"
├── Apellido: "Pérez"
├── Email: "juan@ejemplo.com"
├── Contraseña: "Pass123"
└── Plan: "BASIC"
           │
           ▼
POST /registro
           │
           ▼
AuthController.procesarRegistro()
           │
           ├─→ UsuarioService.registrarUsuario()
           │   ├─→ Valida email único
           │   ├─→ Crea Usuario (JPA)
           │   ├─→ Genera fechaRegistro (LocalDateTime.now())
           │   └─→ INSERT INTO usuarios (...)
           │
           ├─→ PerfilService.crearPerfil()
           │   ├─→ Crea Perfil asociado
           │   └─→ INSERT INTO perfil (usuario_id, ...)
           │
           ├─→ SuscripcionService.crearSuscripcion()
           │   ├─→ Crea Suscripcion con tipoPlan = BASIC
           │   ├─→ estado = ACTIVA (default)
           │   ├─→ fechaInicio = now()
           │   ├─→ fechaProximoRenovacion = now() + 1 mes
           │   └─→ INSERT INTO suscripciones (...)
           │
           └─→ HttpSession.setAttribute()
               ├─→ usuarioId = 1
               ├─→ nombreUsuario = "Juan"
               └─→ email = "juan@ejemplo.com"
                   │
                   ▼
REDIRECT /dashboard
           │
           ▼
USUARIO VE DASHBOARD
"Hola Juan, tu plan es: BASIC"
```

---

## 🔄 Flujo de Datos - Cambiar Plan

```
USUARIO SELECCIONA NUEVO PLAN en dashboard
           │
           ▼
POST /dashboard/cambiar-plan (nuevoTipoPlan="PREMIUM")
           │
           ▼
DashboardController.cambiarPlan()
           │
           ├─→ Obtiene usuarioId de Session
           │
           ├─→ SuscripcionService.actualizarPlan()
           │   ├─→ Obtiene Suscripcion actual (BASIC)
           │   ├─→ Actualiza tipoPlan a PREMIUM
           │   ├─→ HIBERNATE ENVERS REGISTRA:
           │   │   ├─→ Crea revisión en tabla revisions
           │   │   ├─→ Guarda datos ANTES en suscripciones_aud
           │   │   └─→ Guarda datos DESPUÉS en suscripciones
           │   │
           │   └─→ UPDATE suscripciones SET tipoPlan='PREMIUM' WHERE usuario_id=1
           │
           └─→ REDIRECT /dashboard
                   │
                   ▼
USUARIO VE DASHBOARD ACTUALIZADO
"Hola Juan, tu plan es: PREMIUM"
```

---

## 📊 Estructura de Entidades

```
USUARIO (1:1)
├── id: Long @Id @GeneratedValue
├── email: String @Column(unique=true)
├── nombre: String
├── apellido: String
├── password: String
├── fechaRegistro: LocalDateTime @CreationTimestamp
├── Relación: @OneToOne(mappedBy="usuario")
│   ├── Perfil (1:1)
│   │   ├── id: Long
│   │   ├── telefono: String (nullable)
│   │   ├── empresa: String (nullable)
│   │   ├── pais: String (nullable)
│   │   ├── fechaCreacion: LocalDateTime
│   │   └── fechaActualizacion: LocalDateTime
│   │
│   └── Suscripcion (1:1)
│       ├── id: Long
│       ├── tipoPlan: Enum[BASIC, PREMIUM, ENTERPRISE]
│       ├── estado: Enum[ACTIVA, CANCELADA, MOROSA]
│       ├── fechaInicio: LocalDateTime
│       ├── fechaProximoRenovacion: LocalDateTime
│       ├── fechaCancelacion: LocalDateTime (nullable)
│       │
│       └── Relación: @OneToMany(mappedBy="suscripcion")
│           └── Factura (1:N)
│               ├── id: Long
│               ├── monto: BigDecimal
│               ├── numeroFactura: String (unique, auto-generado)
│               ├── estado: String[PENDIENTE, PAGADA]
│               ├── fechaEmision: LocalDateTime
│               ├── fechaVencimiento: LocalDateTime
│               │
│               └── Relación: @OneToMany(mappedBy="factura")
│                   └── Pago (1:N - HERENCIA JOINED)
│                       ├── id: Long
│                       ├── tipoPago: Enum[TARJETA, PAYPAL, TRANSFERENCIA]
│                       ├── monto: BigDecimal
│                       ├── estado: String[COMPLETADO]
│                       ├── fechaPago: LocalDateTime
│                       │
│                       ├── PagoTarjeta extends Pago
│                       │   ├── numeroTarjeta: String
│                       │   ├── titular: String
│                       │   └── fechaVencimiento: String
│                       │
│                       ├── PagoPayPal extends Pago
│                       │   ├── cuentaPayPal: String
│                       │   └── transactionId: String
│                       │
│                       └── PagoTransferencia extends Pago
│                           ├── numeroConversacion: String
│                           ├── banco: String
│                           └── titularCuenta: String

PLAN (Independiente)
├── id: Long
├── nombre: String (unique)
├── precio: Double
├── descripcion: String
├── limiteUsuarios: Integer
└── limiteProyectos: Integer
```

---

## 🔐 Auditoría - Historial de Cambios

```
CAMBIO EN SUSCRIPCIÓN
           │
           ▼
HIBERNATE ENVERS REGISTRA
           │
           ├─→ Crea entrada en tabla REVISIONS
           │   ├── rev: Integer (número de revisión)
           │   └── revtstmp: Long (timestamp)
           │
           ├─→ Crea/Actualiza entrada en tabla SUSCRIPCIONES_AUD
           │   ├── id: Long (mismo del registro original)
           │   ├── usuario_id: Long
           │   ├── tipoPlan: String (valor ANTES del cambio)
           │   ├── estado: String
           │   └── rev: Integer (FK a REVISIONS)
           │
           └─→ Actualiza SUSCRIPCIONES con nuevo valor
               ├── tipoPlan: String (nuevo valor)
               └── (trigger para auditoría)

RESULTADO:
┌─ SUSCRIPCIONES (tabla actual)
│  id=1, tipoPlan='PREMIUM', estado='ACTIVA'
│
└─ SUSCRIPCIONES_AUD (historial)
   ├── Rev 1: tipoPlan='BASIC', estado='ACTIVA'
   ├── Rev 2: tipoPlan='PREMIUM', estado='ACTIVA'
   └── Rev 3: tipoPlan='ENTERPRISE', estado='ACTIVA'
```

---

## 🌐 Endpoints y Vistas

```
HTTP REQUEST
      │
      ├─→ GET /
      │   └─→ Redirect /login
      │
      ├─→ GET /login
      │   └─→ login.html (formulario)
      │
      ├─→ POST /login
      │   ├─→ Valida credenciales
      │   ├─→ Si OK: crea sesión, Redirect /dashboard
      │   └─→ Si FAIL: muestra error
      │
      ├─→ GET /registro
      │   └─→ registro.html (formulario con planes)
      │
      ├─→ POST /registro
      │   ├─→ Crea Usuario
      │   ├─→ Crea Perfil
      │   ├─→ Crea Suscripción
      │   ├─→ Crea Sesión
      │   └─→ Redirect /dashboard
      │
      ├─→ GET /dashboard
      │   └─→ dashboard.html (con nombre, plan, opciones)
      │
      ├─→ POST /dashboard/cambiar-plan
      │   ├─→ Actualiza Suscripción
      │   ├─→ Envers registra el cambio
      │   └─→ Redirect /dashboard
      │
      └─→ GET /logout
          ├─→ Invalida sesión
          └─→ Redirect /login
```

---

## 🔧 Stack Tecnológico

```
FRONTEND
├── HTML5 (sin estilos CSS)
├── Thymeleaf (plantillas dinámicas)
└── HTTP Forms (POST, GET)
     │
     ▼
BACKEND
├── Java 21
├── Spring Boot 4.0.2
├── Spring Data JPA
├── Hibernate 7.2.1
├── Hibernate Envers (auditoría)
└── Apache Tomcat (servidor embebido)
     │
     ▼
DATABASE
├── PostgreSQL 12+
├── 18 tablas relacionales
└── 9 tablas de auditoría (_aud)
```

---

## 🗄️ Relaciones en BD

```
USUARIOS (1) ←─→ (1) PERFIL
    │
    └─→ (1) SUSCRIPCIONES ←─→ (N) FACTURAS
                                    │
                                    └─→ (N) PAGOS
                                         │
                                         ├─→ PAGOS_TARJETA
                                         ├─→ PAGOS_PAYPAL
                                         └─→ PAGOS_TRANSFERENCIA

PLANES (tabla independiente, referenciada por SUSCRIPCIONES)
```

---

## ⚙️ Ciclo de Vida de una Suscripción

```
CREAR USUARIO
      │
      ▼
@PrePersist en Usuario
├── fechaRegistro = LocalDateTime.now()
      │
      ▼
INSERT INTO usuarios
      │
      ▼
CREAR SUSCRIPCIÓN
@PrePersist en Suscripción
├── fechaInicio = LocalDateTime.now()
├── fechaProximoRenovacion = fechaInicio.plusMonths(1)
├── estado = EstadoSuscripcion.ACTIVA (default)
      │
      ▼
INSERT INTO suscripciones
      │
      ▼
HIBERNATE ENVERS REGISTRA
├── INSERT en revisions
├── INSERT en suscripciones_aud (con rev=1)
      │
      ▼
USUARIO ACTIVO CON SUSCRIPCIÓN ACTIVA
```

---

## 📈 Escalabilidad

La arquitectura es escalable porque:
1. **Separación de capas**: Controllers → Services → Repositories
2. **ORM Hibernate**: Abstrae la BD
3. **Auditoría centralizada**: Envers maneja todo automáticamente
4. **Herencia JPA**: Fácil agregar nuevos tipos de pago
5. **Enums**: Valores controlados y seguros

Futuras extensiones posibles:
- REST API JSON (en lugar de HTML forms)
- Pagos reales (integración con Stripe)
- Notificaciones por email
- Dashboard administrativo
- Reportes avanzados
- Soporte para multi-tenancy

---

**Arquitectura diseñada para ser robusta, auditable y escalable.** ✨
