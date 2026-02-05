# ⚡ INSTRUCCIONES DE EJECUCIÓN - Plataforma SaaS

## 🎯 Objetivo
Ejecutar la plataforma SaaS completamente funcional en tu computadora local.

---

## 📋 Requisitos Previos (Antes de Ejecutar)

### 1. PostgreSQL Instalado
✓ Versión 12 o superior
✓ Usuario: Franco
✓ Contraseña: 1234

### 2. Crear la Base de Datos

**Opción A: Usando PgAdmin4**
1. Abre PgAdmin4
2. Clic derecho en Databases → Create → Database
3. **Database name**: ProyectoSpring
4. **Owner**: Franco
5. Haz clic en Save

**Opción B: Usando terminal psql**
```bash
psql -U postgres
CREATE DATABASE "ProyectoSpring" OWNER Franco;
\q
```

**Opción C: Usando SQL en PgAdmin4**
```sql
CREATE DATABASE "ProyectoSpring" 
    WITH ENCODING 'UTF8' 
    OWNER Franco;
```

### 3. Java 21 o Superior
Verifica ejecutando:
```bash
java -version
```

---

## ✅ Pasos para Ejecutar la Aplicación

### Paso 1: Navegar a la Carpeta del Proyecto
```bash
cd "c:\Users\franc\OneDrive\Desktop\ProyectoSaS"
```

### Paso 2: Ejecutar con Maven Wrapper (RECOMENDADO)
```bash
.\mvnw.cmd spring-boot:run
```

**Esperado**: Verás en la consola:
```
Tomcat started on port(s): 8080 (http)
Started ProyectoSaSApplication in 15.234 seconds
```

### Paso 3: Abrir en el Navegador
1. Abre tu navegador (Firefox, Chrome, Edge, etc.)
2. Ve a: **http://localhost:8080**
3. Serás redirigido automáticamente a **http://localhost:8080/login**

---

## 📝 Uso de la Aplicación

### Primera Vez - Crear Cuenta

1. **Haz clic en "Crear cuenta"**

2. **Completa el formulario:**
   - Nombre: (tu nombre)
   - Apellido: (tu apellido)
   - Email: (un email válido)
   - Contraseña: (cualquier contraseña)
   - Plan: (elige uno: BASIC, PREMIUM o ENTERPRISE)

3. **Haz clic en "Crear Cuenta"**

4. **¡Listo!** Verás:
   ```
   Hola [tu nombre], tu plan es: [TU PLAN]
   ```

### Usar la Aplicación

- **Ver tu plan**: En el dashboard
- **Cambiar plan**: Selecciona otro plan y haz clic en "Cambiar Plan"
- **Cerrar sesión**: Haz clic en "Cerrar Sesión"

---

## 🛑 Detener la Aplicación

Para detener la aplicación presiona:
```
Ctrl + C
```

---

## 🔧 Troubleshooting

### Problema 1: "Port 8080 already in use"
**Solución**: Edita `src/main/resources/application.properties`
```properties
server.port=8081  # O cualquier puerto disponible
```

### Problema 2: "Connection refused to PostgreSQL"
**Solución**: 
1. Verifica que PostgreSQL está ejecutándose
2. Verifica que la BD "ProyectoSpring" existe
3. Verifica las credenciales en application.properties:
   - Usuario: Franco
   - Contraseña: 1234

### Problema 3: "No se crea la tabla"
**Solución**: Verifica en `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Problema 4: El login no funciona
**Solución**: 
1. Verifica que creaste la cuenta correctamente
2. Asegúrate de usar el email y contraseña exactos

---

## 📊 Verificar Datos en la BD

Mientras la aplicación está ejecutándose, puedes verificar que los datos se guardan:

### Opción 1: Usar PgAdmin4
1. Conecta a ProyectoSpring
2. Expande: Databases → ProyectoSpring → Schemas → public → Tables
3. Verás tablas como: usuarios, suscripciones, facturas, etc.

### Opción 2: Usar SQL en PgAdmin4 Query Tool
```sql
SELECT * FROM usuarios;
SELECT * FROM suscripciones;
SELECT * FROM facturas;
```

---

## 📚 Documentación Adicional

En la carpeta del proyecto encontrarás:

1. **README_CONFIGURACION.md** - Configuración detallada
2. **RESUMEN_IMPLEMENTACION.md** - Resumen técnico completo
3. **GUIA_PRUEBAS.md** - Guía paso a paso para probar todas las funcionalidades

---

## 🎓 Estructura del Código

```
src/main/java/com/example/ProyectoSaS/
├── controllers/
│   ├── AuthController.java (login, registro, logout)
│   └── DashboardController.java (panel de usuario)
├── models/
│   ├── Usuario.java
│   ├── Perfil.java
│   ├── Suscripcion.java
│   ├── Plan.java
│   ├── Factura.java
│   └── Pago.java (con herencia)
├── repositories/
│   ├── UsuarioRepository.java
│   ├── SuscripcionRepository.java
│   └── ...
├── Services/
│   ├── UsuarioService.java
│   ├── SuscripcionService.java
│   └── ...
├── enums/
│   ├── EstadoSuscripcion.java
│   ├── TipoPlan.java
│   └── TipoPago.java
└── config/
    └── DataInitializer.java

src/main/resources/
├── application.properties (configuración BD)
└── templates/
    ├── login.html
    ├── registro.html
    └── dashboard.html
```

---

## 🔐 Características Implementadas

✅ **Autenticación**: Login y registro de usuarios
✅ **Gestión de Planes**: BASIC, PREMIUM, ENTERPRISE
✅ **Suscripciones**: Crear, ver, cambiar plan
✅ **Facturas**: Generación automática
✅ **Auditoría**: Historial completo de cambios (Envers)
✅ **Base de Datos**: PostgreSQL con 18 tablas
✅ **JPA Avanzado**: Enums, herencia, relaciones
✅ **Vistas**: HTML funcionales sin estilos

---

## 💡 Notas Importantes

1. Las contraseñas se guardan en texto plano (en producción usar bcrypt)
2. Los datos persisten en PostgreSQL
3. El historial de auditoría se registra automáticamente
4. Puedes cambiar de plan desde el dashboard
5. Las facturas se generan automáticamente al registrarse

---

## 📞 Soporte Rápido

| Problema | Solución |
|----------|----------|
| No inicia | Verifica que PostgreSQL está ejecutándose |
| Puerto ocupado | Cambia el puerto en application.properties |
| BD vacía | Crea la BD "ProyectoSpring" |
| Datos no guardan | Verifica credenciales PostgreSQL |
| Tablas no existen | Reinicia la aplicación |

---

## ✨ ¡Listo para usar!

Ahora puedes:
1. Crear múltiples usuarios
2. Cada usuario puede elegir su plan
3. Los cambios de plan se registran automáticamente
4. Toda la información se guarda en PostgreSQL
5. Puedes ver el historial de auditoría cuando quieras

**¡Disfruta tu plataforma SaaS!** 🚀
