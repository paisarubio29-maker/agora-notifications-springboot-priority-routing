# Agora Notifications - Multi-module (Java 21)
Este repositorio contiene:

- **agora-notifications-core**: la biblioteca reutilizable (sincronización, asincronía y proveedores simulados).

- **agora-notifications-api**: una API REST de Spring Boot que **reutiliza la biblioteca principal** (sin lógica de dominio duplicada).
## Run

```bash
mvn test
mvn -pl agora-notifications-api spring-boot:run
```

## Endpoints
- POST `/api/notifications/send`
- POST `/api/notifications/send-async`
- POST `/api/notifications/send-batch-async`


## Selección de proveedor (por canal)

La aplicación soporta **múltiples proveedores por canal** (por ejemplo, Email: `SENDGRID` o `MAILGUN`).
La selección se realiza por request usando el campo opcional `provider`. Internamente la API
lo guarda en `metadata["provider"]` para que la librería (core) seleccione el proveedor.

Ejemplo (Email con Mailgun):

```bash
curl -X POST http://localhost:8080/api/notifications/send   -H "Content-Type: application/json"   -d '{
    "type": "EMAIL",
    "provider": "MAILGUN",
    "to": "user@example.com",
    "subject": "Hola",
    "body": "Enviado con Mailgun",
    "priority": "HIGH",
    "metadata": {}
  }'
```

Si no envías `provider`, se usa el proveedor por defecto configurado para ese canal.


## Selección de proveedor por reglas (NotificationPriority)

Además de soportar múltiples proveedores por canal, la aplicación puede seleccionar el proveedor
automáticamente usando reglas basadas en `NotificationPriority`.

Ejemplo (Email):
- `CRITICAL` / `HIGH`  → `SENDGRID`
- `NORMAL` / `LOW`     → `MAILGUN`

> Nota: el campo opcional `provider` en el request sigue existiendo como **override manual**.
> Si envías `"provider": "MAILGUN"`, se usa ese proveedor aunque la prioridad sea `CRITICAL`.

Ejemplo (Email CRITICAL que se enruta a SendGrid por regla):

```bash
curl -X POST http://localhost:8080/api/notifications/send   -H "Content-Type: application/json"   -d '{
    "type": "EMAIL",
    "to": "user@example.com",
    "subject": "Alerta crítica",
    "body": "Esto debe ir por el proveedor principal",
    "priority": "CRITICAL",
    "metadata": {}
  }'
```

Override manual (forzar Mailgun):

```bash
curl -X POST http://localhost:8080/api/notifications/send   -H "Content-Type: application/json"   -d '{
    "type": "EMAIL",
    "provider": "MAILGUN",
    "to": "user@example.com",
    "subject": "Forzado a Mailgun",
    "body": "Aunque sea CRITICAL, usa Mailgun",
    "priority": "CRITICAL",
    "metadata": {}
  }'
```

Asincrónico:

```bash
curl -X POST http://localhost:8080/api/notifications/send-async \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EMAIL",
    "provider": "MAILGUN",
    "to": "user@example.com",
    "subject": "Forzado a Mailgun (async)",
    "body": "Aunque sea CRITICAL, usa Mailgun",
    "priority": "CRITICAL",
    "metadata": {}
  }'

```

## Clases principales del proyecto (guía rápida)

Esta sección resume las clases más importantes y cómo se conectan entre sí, para entender el flujo completo del sistema.

---

### 1) Core (librería `agora-notifications-core`)

#### **`NotificationClient`**
Fachada principal para enviar notificaciones sin conocer detalles internos (validaciones, providers, etc.).

- `send(NotificationMessage)` → envío síncrono
- `sendAsync(NotificationMessage)` → envío asíncrono (CompletableFuture + Virtual Threads)
- `sendBatchAsync(List<NotificationMessage>)` → envío por lotes (ordenado por prioridad)

---

#### **`NotificationMessage` (sealed interface)**
Modelo base de mensajes soportados. Implementado por:

- **`EmailMessage`** (record): `to`, `subject`, `body`, `priority`, `metadata`
- **`SmsMessage`** (record): `to`, `body`, `priority`, `metadata`
- **`PushMessage`** (record): `to`, `title`, `body`, `priority`, `metadata`

> `metadata` es un mapa flexible para pasar información extra (por ejemplo `provider`).

---

#### **`SendResult`**
Resultado del envío (sin lanzar excepciones para casos esperados):

- **`SendSuccess`** → envío exitoso (contiene `notificationId`)
- **`SendFailure`** → fallo (contiene `notificationId` + `SendError`)

Errores:
- **`ValidationError`** → problemas de validación (lista de mensajes)
- **`ProviderError`** → error del proveedor (provider + mensaje + causa)

---

#### **`Validator<T>` + `CommonValidators`**
Validaciones funcionales y componibles usando `.and(...)`.

Ejemplos:
- email válido, subject no vacío, body no vacío
- phone E.164 para SMS
- token no vacío para Push

---

#### **Senders (Strategy por canal)**
Clases responsables de:
1) Validar el mensaje
2) Delegar al provider
3) Exponer API sync + async

- **`EmailSender`**
- **`SmsSender`**
- **`PushSender`**

Base común:
- **`AbstractAsyncSender<M>`** → implementa `sendAsync(...)` usando un `Executor`

---

#### **Async (Virtual Threads)**
- **`AsyncExecutors`** → crea el `ExecutorService` con `Executors.newVirtualThreadPerTaskExecutor()`

---

#### **Providers (interfaces por canal)**
Abstracción para intercambiar proveedores sin cambiar el código cliente:

- **`EmailProvider`**
- **`SmsProvider`**
- **`PushProvider`**

Simulados (para el challenge):
- `SimulatedEmailProvider`
- `SimulatedSmsProvider`
- `SimulatedPushProvider`

---

#### **Routing (múltiples proveedores por canal)**
Permite tener varios providers por canal (ej. Email: SendGrid/Mailgun) y escoger uno:

- **`ProviderSelector<M, P>`** → selector genérico (type-safe)
- **`ProviderKey`** → clave estándar `metadata["provider"]`

Routing simple:
- `RoutingEmailProvider`
- `RoutingSmsProvider`
- `RoutingPushProvider`

Routing por reglas de prioridad:
- **`PriorityRules`** → mapea `NotificationPriority -> providerKey`
- **`PriorityRoutingEmailProvider`**
- **`PriorityRoutingSmsProvider`**
- **`PriorityRoutingPushProvider`**

Precedencia de selección:
1) Si llega `metadata["provider"]` (override manual) → usa ese provider
2) Si no, usa reglas por `NotificationPriority`
3) Si no aplica, usa provider default del canal

---

### 2) API (Spring Boot `agora-notifications-api`)

#### **`NotificationsApiApplication`**
Clase principal de Spring Boot (arranque de la aplicación).

---

#### **`NotificationsConfig`**
Wiring de Spring (beans) donde se define:

- providers disponibles por canal (ej. `SENDGRID`, `MAILGUN`)
- reglas de ruteo por `NotificationPriority`
- executor con Virtual Threads
- creación de Senders + NotificationRegistry + NotificationClient

---

#### **`NotificationController`**
Controlador REST con endpoints típicos:

- `POST /api/notifications/send`
- `POST /api/notifications/send-async`
- `POST /api/notifications/send-batch-async`

---

#### **DTOs y mapeo**
- **`NotificationRequest`** → request JSON de entrada (incluye `type`, `to`, `subject`, `body`, `priority`, `metadata`, y opcional `provider`)
- **`NotificationMapper`** → convierte `NotificationRequest` a `EmailMessage` / `SmsMessage` / `PushMessage`  
  y si viene `provider`, lo inserta en `metadata["provider"]` para que el core haga routing.

---

### 3) Flujo completo (alto nivel)

1) Llega request a `NotificationController`
2) `NotificationMapper` convierte el JSON a un `NotificationMessage`
3) `NotificationClient` decide el sender correcto mediante `NotificationRegistry`
4) El sender valida (`Validator`)
5) El provider se selecciona por routing (override / reglas por prioridad / default)
6) Se retorna `SendSuccess` o `SendFailure` (con errores claros)


## Seguridad

Esta sección describe las mejores prácticas para manejar credenciales y datos sensibles
al utilizar Agora Notifications.

---

### Manejo de credenciales

⚠️ **Nunca** incluyas credenciales (API Keys, tokens, secretos) directamente en el código fuente.

Buenas prácticas recomendadas:

- Usar **variables de entorno** para credenciales de proveedores externos
- Usar archivos de configuración externos (`application.yml`, `.env`) que **no se versionen**
- Mantener los secretos fuera del repositorio (`.gitignore`)


## Docker

La aplicación puede ejecutarse usando Docker, sin necesidad de instalar Java o Maven
en la máquina local.

### Construir la imagen

Desde la raíz del proyecto (donde se encuentra el `Dockerfile`):

```bash
docker build -t agora-notifications .
docker run -p 8080:8080 agora-notifications
```