# 🛒 TechLab E-Commerce API — Bitácora de Desarrollo

> **Nota del autor:** Esta bitácora documenta la evolución del proyecto tal como fue surgiendo, con sus idas y vueltas, los bugs que nos hicieron renegar y las pequeñas victorias del día a día.

---

## 📅 Historial de Versiones

### [1.0.0] — 2026-07-05 — "MVP listo para production"

**Estado:** ✅ Lanzamiento inicial

Después de bastante laburo,终于 podemos decir que la API estáfuncional y lista para usarse. No es perfecta, pero hace lo que tiene que hacer.

**Qué se hizo:**

- 🎯 **Arquitectura Hexagonal implementada**
  - Sepamos sido limpios desde el principio: dominio, aplicación e infraestructura bien separados
  - Los ports (in/out) nos van a facilitar agregar adapters sin romper todo
  - Todavía queda pendiente refactorizar algunos servicios que se nos fueron por la ventana

- 🔐 **Sistema de autenticación JWT**
  - Login y registro de usuarios funcionando
  - Tokens con expiración configurable (default 24h, sufficient por ahora)
  - RBAC básico: ADMIN y USER con permisos diferenciados
  - *Acción de frente: el secreto del JWT va por variable de entorno, no hardcodearlo NUNCA*

- 📦 **Gestión de Productos (CRUD completo)**
  - Alta, baja, modificación (soft delete para no perder historial)
  - Búsqueda por texto y filtro por categoría
  - Validaciones de stock y datos de producto
  - Excepciones custom bien definidas: ProductNotFoundException, InvalidProductDataException, InsufficientStockException

- 🛒 **Carrito de Compras**
  - Agregar, actualizar, quitar items
  - Checkout que genera la orden directamente
  - Sincronización con el stock (resta cuando se confirma)
  - El carrito persiste por usuario, así que no se pierde al cerrar el navegador

- 📋 **Pedidos (Orders)**
  - Máquina de estados: PENDIENTE → CONFIRMADO → ENVIADO → ENTREGADO (o CANCELADO)
  - Historial de pedidos por usuario
  - Cancelación (solo por el owner o admin)
  - Actualización de estado restricted a admins

- 👥 **Gestión de Usuarios**
  - Registro con validación de email
  - Login que devuelve el JWT
  - Listado de usuarios (admin only)
  - Perfil de usuario

- 📚 **Documentación con OpenAPI/Swagger**
  - Swagger UI disponible en `/swagger-ui.html`
  - Schema completo en `/v3/api-docs`
  - Annotations en los controllers para que quede prolijo
  - *Mejorable: faltan descriptions más detalladas en varios endpoints*

- 🐳 **Dockerización completa**
  - docker-compose con MySQL 8.0, phpMyAdmin y la app
  - Healthchecks para que MySQL esté ready antes de levantar el backend
  - Multi-stage build en Dockerfile para images optimizadas
  - Variables de entorno para todo (máquinas, ¡las máquinas no mienten!)

- ✅ **Testing setup**
  - Unit tests para ProductService y OrderService
  - Integration tests para ProductController y OrderController
  - H2 como base de datos in-memory para tests
  - *Deuda: coverage bajo, hay que agregar más tests en siguientes iteraciones*

- 🚀 **Data Initializer**
  - Se crean usuarios y productos de prueba al iniciar
  - Credenciales: admin/Admin123! y user/User123!
  - Sirve para development y testing rápido

**Bugfixes registrados:**
- Fixed: El stock no se decrementaba correctamente en el checkout
- Fixed: Excepciones que no eran capturadas por el handler global
- Fixed: JWT expiraba antes de lo esperado por problema de timezone

**Known issues:**
- ⚠️ No hay rate limiting (un bot podría romper la API)
- ⚠️ Los passwords en logs aparecen en texto plano cuando hay errores (hay que sanitizar)
- ⚠️ No hay paginación en listados (con muchos productos va a ser lento)

---

### [0.5.0] — 2026-06-28 — "La base está, falta pulir"

**Estado:** 🔨 En desarrollo (no released)

Esta versión nunca se taggeó porque era más un spike técnico, pero la registro para tener trazabilidad.

**Qué se hizo:**

- Setup inicial del proyecto con Maven
- Spring Boot 3.2.5 + Java 21
- Estructura de paquetes según arquitectura hexagonal
- Conexión básica a MySQL
- Primeros modelos de dominio: Product, User, Order, Cart
- Security config básica (permit all, después vino JWT)

**Lecciones aprendidas:**
- Pattern matching con switch en Java 21 es una maravilla para los OrderStatus
- MapStruct + Lombok requieren configuración especial en annotation processor paths
- SpringDoc 2.5.0 tiene un bug con las security schemes, hay que configurar a mano

---

### [0.1.0] — 2026-06-15 — "Primer commit"

**Estado:** 💀 Abandonado

Solo existía la estructura del proyecto, ningún código funcional. Me pasó que empecé con un enfoque anémico (todo en controllers) y tuve que rehacer medio proyecto cuando me di cuenta de que no iba a escalar.

**Moraleja:** Invertir tiempo en arquitectura al principio ahorra mucho tiempo después.

---

## 🗺️ Roadmap — Hacia dónde vamos

> Estas son las features que tenemos en el backlog, priorizadas por utilidad vs. complejidad. Nada de esto es promesa, es más bien un "si todo sale bien, esto debería estar".

### 🔴 Alta Prioridad (esperemos atacarlos en Q3 2026)

- [ ] **Paginación en endpoints de listado**
  - Hoy devuelven todo de una, lo cual va a matar la base
  - Pageable de Spring Data es straightforward, debería tomar 1-2 días

- [ ] **Rate Limiting**
  - Proteger la API de abuse
  - Bucket4j o implementar un filter custom con Redis
  - Es crítico si esto va a production real

- [ ] **Email notifications**
  - Cuando se crea un pedido, cuando cambia de estado
  - Interface NotificationPort ya existe, solo falta implementarla
  - Enviar emails reales con Spring Mail

- [ ] **Refresco de tokens JWT**
  - Hoy si se vence el token hay que hacer login de nuevo
  - Refresh tokens nos salvarían de esto
  - Un poco más de laburo porque hay que pensar el flujo completo

### 🟡 Media Prioridad (Q4 2026 si hay tiempo)

- [ ] **Sistema de pagos integrado**
  - MercadoPago o Stripe como provider
  - Webhooks para confirmar pagos
  - Esto ya es otro nivel, requiere mucho testing

- [ ] **Upload de imágenes de productos**
  - Hoy solo es una URL, pero estaría bueno tener upload real
  - S3 o MinIO para storage
  - Multer o similar para recibir archivos

- [ ] **API de reseñas/valoraciones**
  - Los usuarios pueden calificar productos comprados
  - Promedio de rating visible en el producto
  - Requiere verificar que el usuario haya comprado para reseñar

- [ ] **Métricas y monitoring**
  - Prometheus + Grafana
  - Micrometer para métricas de Spring
  - Health checks más completos

### 🟢 Baja Prioridad / Nice to have (2027 maybe)

- [ ] **Multi-idioma (i18n)**
  - Ya está la estructura para hacerlo
  - Solo falta externalizar los mensajes

- [ ] **Historial de precios**
  - Trackear cómo varió el precio de un producto
  - Útil para analytics y transparencia

- [ ] **API pública (v2)**
  - Versión pública con menos datos
  - Rate limiting por API key
  - Para que terceros puedan integrar

- [ ] **GraphQL endpoint**
  - Alternative a REST para queries más flexibles
  - Solo si hay demanda real

---

## 📊 Estadísticas del Proyecto

```
Líneas de código (aprox): ~3,500
Archivos Java: ~45
Paquetes: 8 dominios principales
Test coverage: ~35% (hay que mejorar)
```

---

## 🤓 Notas del Developer

**Cosas que me gustaron del approach hexagonal:**
- Los servicios son muy fáciles de testear
- Cambiar de JPA a algo más exotic es casi trivial
- El código de dominio está limpio de infrastructure concerns

**Cosas para mejorar:**
- Validation constraints分散 en varios lugares, hay que centralizar
- Los DTOs Request/Response están bien pero hay boilerplate repetido
- Falta un global exception handler más elegante

**Dependencies que podrían mejorar:**
- Considerar agregar Flyway para migrations (hoy usamos auto-create)
- Actuator para health checks más finos
- Resilience4j para circuit breaker patterns

---

## 📞 Contacto y Contribuir

Este proyecto es parte del programa **TechLab - BUE (Buenos Aires Universidad Empresa)**.

Si encontrás un bug o querés proponer una feature, abrí un issue en el repo.

**Maintainer:** TechLab Team  
**Email:** techlab@bue.edu.ar  
**Año:** 2026

---

*Last updated: 2026-07-05*
