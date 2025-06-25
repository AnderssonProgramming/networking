# EJERCICIO 5.2.1 - UDP Datagram Time Server/Client

## Descripción

Implementación de un sistema cliente-servidor usando **datagramas UDP** que permite solicitar la hora actual del servidor. El cliente actualiza la hora cada 5 segundos y maneja desconexiones del servidor gracefully.

---

## Características Implementadas

### 🟦 TimeServer (Servidor UDP)
- **Puerto**: 45000
- **Protocolo**: UDP (DatagramSocket)
- **Funcionalidad**: Responde con la hora actual del servidor
- **Formato de respuesta**: `yyyy-MM-dd HH:mm:ss`
- **Manejo de errores**: Robusto manejo de excepciones de red

### 🟦 TimeClient (Cliente UDP)
- **Actualización automática**: Cada 5 segundos
- **Timeout de conexión**: 3 segundos
- **Manejo de desconexiones**: Mantiene última hora conocida
- **Reconexión automática**: Continúa cuando el servidor vuelve online
- **Interfaz interactiva**: Permite salir con 'q' + Enter

### 🟦 Funcionalidades del Sistema

1. **✅ Comunicación UDP**: Protocolo de datagramas sin garantía de entrega
2. **✅ Actualizaciones periódicas**: Cliente solicita tiempo cada 5 segundos
3. **✅ Tolerancia a fallos**: Cliente continúa funcionando si servidor está offline
4. **✅ Reconexión automática**: Cliente detecta cuando servidor vuelve online
5. **✅ Logging detallado**: Registro de todas las transacciones
6. **✅ Manejo graceful**: Cierre limpio de recursos

---

## Protocolo de Comunicación

### Solicitud del Cliente
```
Mensaje: "GET_TIME"
Destino: localhost:45000
Protocolo: UDP
```

### Respuesta del Servidor
```
Formato: "2025-06-25 14:30:15"
Fuente: servidor:45000
Protocolo: UDP
```

---

## Estructura del Código

### TimeServer.java
```java
public class TimeServer {
    private static final int SERVER_PORT = 45000;
    private DatagramSocket socket;
    
    // Métodos principales:
    // - startServer(): Inicia servidor UDP y escucha solicitudes
    // - getCurrentTimeString(): Genera timestamp actual
    // - stopServer(): Detiene servidor gracefully
}
```

### TimeClient.java
```java
public class TimeClient {
    private static final int UPDATE_INTERVAL = 5000; // 5 segundos
    private static final int SOCKET_TIMEOUT = 3000;  // 3 segundos timeout
    private String lastKnownTime;
    
    // Métodos principales:
    // - startClient(): Inicia cliente con loop de actualización
    // - requestTimeFromServer(): Solicita tiempo via UDP
    // - startInputMonitorThread(): Monitorea entrada del usuario
}
```

---

## Uso y Ejecución

### 1. Compilar el proyecto
```bash
mvn compile
```

### 2. Ejecutar el servidor (Terminal 1)
```bash
java -cp target/classes edu.eci.arsw.networking.TimeServer
```

**Salida esperada:**
```
=== UDP TIME SERVER ===
Servidor de tiempo iniciado en puerto: 45000
Esperando solicitudes de tiempo...
Presione Ctrl+C para detener el servidor
```

### 3. Ejecutar el cliente (Terminal 2)
```bash
java -cp target/classes edu.eci.arsw.networking.TimeClient
```

**Salida esperada:**
```
=== UDP TIME CLIENT ===
Cliente de tiempo iniciado
Servidor: localhost:45000
Actualizando cada 5 segundos
Timeout de conexión: 3 segundos
Presione 'q' + Enter para salir

✅ [2025-06-25 14:30:15] Tiempo del servidor: 2025-06-25 14:30:15
✅ [2025-06-25 14:30:20] Tiempo del servidor: 2025-06-25 14:30:20
```

### 4. Demo automatizado
```bash
java -cp target/classes edu.eci.arsw.networking.TimeDemo
```

---

## Pruebas de Funcionalidad

### Escenario 1: Funcionamiento Normal
1. ✅ Servidor responde a solicitudes UDP
2. ✅ Cliente recibe actualizaciones cada 5 segundos
3. ✅ Timestamps son precisos y actuales

### Escenario 2: Servidor Offline
1. ✅ Cliente detecta timeout de conexión
2. ✅ Cliente mantiene última hora conocida
3. ✅ Cliente muestra mensaje de servidor no disponible

### Escenario 3: Reconexión del Servidor
1. ✅ Cliente detecta que servidor está online nuevamente
2. ✅ Cliente reanuda actualizaciones normales
3. ✅ Cliente muestra nueva hora del servidor

---

## Tests Unitarios

### TimeServerTest.java
- ✅ `testServerStartsAndResponds()`: Servidor inicia y responde
- ✅ `testServerResponseFormat()`: Formato de respuesta correcto
- ✅ `testMultipleRequests()`: Manejo de múltiples solicitudes
- ✅ `testServerHandlesInvalidRequests()`: Manejo de solicitudes inválidas
- ✅ `testServerStopGracefully()`: Cierre graceful del servidor

### TimeClientTest.java
- ✅ `testClientCanRequestTime()`: Cliente puede solicitar tiempo
- ✅ `testClientHandlesServerTimeout()`: Manejo de timeouts
- ✅ `testClientConfiguration()`: Configuración correcta del cliente

### Ejecutar tests
```bash
mvn test -Dtest=TimeServerTest
mvn test -Dtest=TimeClientTest
```

---

## Conceptos de Networking Demostrados

1. **📡 Datagramas UDP**: Comunicación sin conexión y sin garantías
2. **⏱️ Timeouts de socket**: Manejo de no-respuesta del servidor
3. **🔄 Polling periódico**: Solicitudes automáticas cada intervalo
4. **🛡️ Tolerancia a fallos**: Funcionamiento continuo ante fallos de red
5. **🔌 Reconexión automática**: Detección y recuperación de conectividad
6. **🧵 Threading**: Manejo concurrente de entrada del usuario

---

## Diferencias con TCP

| Característica | TCP (Ejercicios 4.x) | UDP (Ejercicio 5.2.1) |
|---|---|---|
| **Conexión** | Orientado a conexión | Sin conexión |
| **Confiabilidad** | Garantizada | No garantizada |
| **Orden** | Preservado | No preservado |
| **Overhead** | Mayor | Menor |
| **Uso ideal** | Datos críticos | Datos en tiempo real |

---

## Arquitectura del Sistema

```
┌─────────────────┐         UDP Datagram         ┌─────────────────┐
│   TimeClient    │ ─────────────────────────────▶│   TimeServer    │
│                 │         "GET_TIME"            │                 │
│                 │◀───────────────────────────── │                 │
│ - Actualiza 5s  │      "2025-06-25 14:30:15"   │ - Puerto 45000  │
│ - Mantiene hora │                               │ - Formato fecha │
│ - Reconecta     │                               │ - Multirequests │
└─────────────────┘                               └─────────────────┘
```

---

## Requisitos

- ✅ Java 17 o superior
- ✅ Maven 3.6 o superior
- ✅ Puerto 45000 disponible
- ✅ Acceso a localhost/127.0.0.1

---

## Mejoras Implementadas

1. **🎯 Cumplimiento exacto del ejercicio**: Actualización cada 5 segundos
2. **🛡️ Manejo robusto de errores**: Timeouts, excepciones de red
3. **🔧 Configuración flexible**: Constantes para puertos, timeouts, intervalos
4. **📊 Logging detallado**: Registro de todas las transacciones
5. **🧪 Testing completo**: Suite de pruebas unitarias
6. **🎮 Demo interactivo**: Demostración automatizada de funcionalidad

---

## Autor

Implementación del Ejercicio 5.2.1 basado en el tutorial de datagramas UDP de Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería - Arquitectura Empresarial.

**Fecha**: 25 de Junio, 2025  
**Versión**: 1.0
