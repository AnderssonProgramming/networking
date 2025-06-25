# EJERCICIO 6 - RMI Programming Exercise

## Descripción General

Este archivo documenta la solución al ejercicio 6.4.1 del taller de RMI (Remote Method Invocation) en Java. El ejercicio implementa una aplicación de chat bidireccional usando Java RMI, donde cada instancia puede actuar como cliente y servidor simultáneamente.

---

# EJERCICIO 6.4.1 - RMI Chat Application

## Descripción

Aplicación de chat que utiliza Java RMI para comunicación bidireccional entre múltiples participantes. Cada instancia puede enviar y recibir mensajes, actuando como cliente y servidor al mismo tiempo.

### Características Principales

- **Comunicación RMI**: Utiliza Java Remote Method Invocation para comunicación distribuida
- **Bidireccional**: Cada instancia puede enviar y recibir mensajes
- **Configuración Flexible**: Permite especificar puertos locales y remotos
- **Interfaz de Usuario**: Consola interactiva para envío de mensajes
- **Manejo de Errores**: Detección de desconexiones y reconexión automática
- **Timestamps**: Marca de tiempo en todos los mensajes

### Protocolo de Comunicación

- **Método remoto**: `receiveMessage(String senderName, String message)`
- **Identificación**: `getParticipantName()` para obtener el nombre del participante
- **Ping**: `ping()` para verificar conectividad

### Archivos Implementados

1. **`ChatService.java`** - Interfaz remota que define los métodos RMI
2. **`RMIChatApplication.java`** - Aplicación principal de chat RMI
3. **`RMIChatDemo.java`** - Demostración automática e interactiva
4. **`RMIChatApplicationTest.java`** - Pruebas unitarias completas

## Funcionalidad Según el Ejercicio

El programa cumple exactamente con los requisitos especificados:

1. **Prompt para IP y puerto**: La aplicación solicita IP y puerto para conectarse a un peer remoto
2. **Puerto local**: Solicita puerto para publicar el objeto remoto propio
3. **Chat bidireccional**: Permite enviar y recibir mensajes entre instancias
4. **RMI**: Utiliza Java RMI para toda la comunicación distribuida

## Arquitectura del Sistema

### Componentes RMI

1. **Interfaz Remota (`ChatService`)**:
   - Define métodos que pueden ser llamados remotamente
   - Extiende `java.rmi.Remote`
   - Todos los métodos lanzan `RemoteException`

2. **Implementación (`RMIChatApplication`)**:
   - Extiende `UnicastRemoteObject`
   - Implementa la interfaz `ChatService`
   - Maneja el registro RMI y la conexión a peers remotos

3. **Registro RMI**:
   - Cada instancia crea su propio registro RMI
   - Publica su servicio para que otros se conecten
   - Se conecta a registros remotos para acceder a otros participantes

### Flujo de Comunicación

```
Instancia A                    Instancia B
    |                              |
    |-- Publica servicio --------->|
    |    en puerto 1099            |
    |                              |
    |<----- Se conecta ------------|
    |       a puerto 1099          |
    |                              |
    |-- receiveMessage("B", ------>|
    |    "Hola desde A")           |
    |                              |
    |<----- receiveMessage("A", ---|
    |       "Hola desde B")        |
```

## Características Implementadas

### Funcionalidades de la Aplicación

- **Registro RMI Automático**: Crea y configura el registro RMI automáticamente
- **Conexión a Peers**: Se conecta a otros participantes via IP y puerto
- **Chat en Tiempo Real**: Envío y recepción inmediata de mensajes
- **Interfaz Interactiva**: Comandos de chat intuitivos
- **Manejo de Estados**: Verificación de conexión y estado del peer
- **Cierre Graceful**: Limpieza apropiada de recursos RMI

### Comandos de Chat

- **Escribir mensaje**: Cualquier texto se envía como mensaje
- **`exit`**: Termina la aplicación
- **`status`**: Verifica el estado de la conexión
- **Timestamps**: Todos los mensajes incluyen hora de recepción

### Características Técnicas

- **Try-with-resources**: Manejo automático de Scanner y recursos
- **Exception handling**: Manejo específico para `RemoteException`
- **Input validation**: Validación de puertos y direcciones IP
- **Thread safety**: Operaciones RMI seguras para concurrencia
- **Registry management**: Gestión automática de registros RMI

## Estructura del Código

### ChatService.java (Interfaz Remota)

```java
public interface ChatService extends Remote {
    void receiveMessage(String senderName, String message) throws RemoteException;
    String getParticipantName() throws RemoteException;
    boolean ping() throws RemoteException;
}
```

### RMIChatApplication.java (Implementación Principal)

```java
public class RMIChatApplication extends UnicastRemoteObject implements ChatService {
    // Configuración RMI
    private String participantName;
    private ChatService remoteChatService;
    
    // Métodos principales
    public void startRMIService(int port);
    public void connectToRemotePeer(String host, int port);
    public void sendMessage(String message);
    public void startChatLoop();
}
```

## Casos de Uso

### Caso 1: Configuración Cliente-Servidor

1. **Instancia A (Servidor)**:
   - Nombre: "Alice"
   - Puerto local: 1099
   - No se conecta a nadie inicialmente

2. **Instancia B (Cliente)**:
   - Nombre: "Bob"
   - Puerto local: 1100
   - Se conecta a Alice en localhost:1099

### Caso 2: Red de Múltiples Participantes

Cada participante puede conectarse a cualquier otro:
- Alice (puerto 1099) ↔ Bob (puerto 1100)
- Bob (puerto 1100) ↔ Charlie (puerto 1101)
- Alice puede conectarse también a Charlie

## Compilación y Ejecución

### Compilar el Proyecto

```bash
mvn compile
```

### Ejecutar las Pruebas

```bash
mvn test -Dtest=RMIChatApplicationTest
```

### Ejecutar la Aplicación Principal

```bash
java -cp target/classes edu.eci.arsw.networking.RMIChatApplication
```

### Ejecutar la Demostración

```bash
# Demostración automática
java -cp target/classes edu.eci.arsw.networking.RMIChatDemo auto

# Demostración interactiva
java -cp target/classes edu.eci.arsw.networking.RMIChatDemo interactive

# Guía manual de configuración
java -cp target/classes edu.eci.arsw.networking.RMIChatDemo manual
```

## Ejemplo de Sesión de Chat

### Terminal 1 (Alice)
```
=== RMI Chat Application ===
Exercise 6.4.1 - Java RMI Bidirectional Chat

Enter your name: Alice
Enter local port to publish RMI service (e.g., 1099): 1099
Chat service 'Alice' published on port 1099
Connect to a remote peer? (y/n): n
Waiting for incoming connections...
Other instances can connect to: localhost:1099

=== RMI Chat Application ===
Type your messages and press Enter to send.
Type 'exit' to quit the application.
Type 'status' to check connection status.
===========================

> [14:30:25] Bob: Hello Alice! How are you?
> Hi Bob! I'm doing great
[14:30:35] Alice (you): Hi Bob! I'm doing great
> 
```

### Terminal 2 (Bob)
```
=== RMI Chat Application ===
Exercise 6.4.1 - Java RMI Bidirectional Chat

Enter your name: Bob
Enter local port to publish RMI service (e.g., 1099): 1100
Chat service 'Bob' published on port 1100
Connect to a remote peer? (y/n): y
Enter remote host IP (or press Enter for localhost): 
Enter remote port: 1099
Connected to remote peer: Alice at localhost:1099

=== RMI Chat Application ===
Type your messages and press Enter to send.
Type 'exit' to quit the application.
Type 'status' to check connection status.
===========================

> Hello Alice! How are you?
[14:30:25] Bob (you): Hello Alice! How are you?
> [14:30:35] Alice: Hi Bob! I'm doing great
> 
```

## Manejo de Errores

### Errores de Conexión
- **Puerto ocupado**: Informa si el puerto está en uso
- **Peer no disponible**: Detecta cuando el peer remoto no responde
- **Conexión perdida**: Maneja desconexiones durante la conversación

### Recuperación de Errores
- **Reconexión**: Permite intentar reconectar manualmente
- **Estado de conexión**: Comando `status` para verificar conectividad
- **Limpieza de recursos**: Cierre limpio de registros RMI

## Pruebas Implementadas

### RMIChatApplicationTest.java

1. **testChatServiceBasicFunctionality**: Verifica métodos básicos de la interfaz
2. **testRMIServiceRegistration**: Prueba registro y búsqueda de servicios RMI
3. **testRemoteServiceConnection**: Verifica conexión a servicios remotos
4. **testBidirectionalCommunication**: Prueba comunicación bidireccional completa
5. **testMultipleConsecutiveMessages**: Verifica múltiples mensajes consecutivos
6. **testConnectionFailureHandling**: Manejo de errores de conexión
7. **testEmptyAndSpecialCharacterMessages**: Prueba caracteres especiales y emojis

### Resultados de Pruebas

Todas las pruebas pasan exitosamente, verificando:
- Funcionalidad RMI básica
- Comunicación bidireccional
- Manejo de errores
- Múltiples mensajes
- Caracteres especiales

## Tecnologías Utilizadas

- **Java RMI**: Remote Method Invocation para comunicación distribuida
- **Java Registry**: Servicio de nombres para objetos remotos
- **UnicastRemoteObject**: Implementación de objetos remotos
- **Maven**: Gestión de dependencias y compilación
- **JUnit 5**: Framework de pruebas unitarias

## Conceptos RMI Demostrados

1. **Interfaz Remota**: Definición de métodos que pueden ser llamados remotamente
2. **Stub y Skeleton**: Generación automática de proxies para comunicación
3. **Registry**: Servicio de nombres para localizar objetos remotos
4. **Serialización**: Paso de parámetros y valores de retorno entre JVMs
5. **Exception Handling**: Manejo de `RemoteException` en operaciones remotas
6. **Security**: Configuración de políticas de seguridad para RMI

## Ventajas de RMI

- **Transparencia**: Los métodos remotos se llaman como si fueran locales
- **Orientado a Objetos**: Mantiene el paradigma de POO en sistemas distribuidos
- **Type Safety**: Verificación de tipos en tiempo de compilación
- **Garbage Collection**: Recolección automática de objetos remotos no referenciados
- **Security**: Marco de seguridad integrado para operaciones remotas

## Diferencias con Otros Protocolos

### RMI vs Sockets TCP
- **RMI**: Alto nivel, orientado a objetos, transparente
- **Sockets**: Bajo nivel, basado en streams, control manual

### RMI vs UDP
- **RMI**: Confiable, orientado a conexión (usa TCP internamente)
- **UDP**: No confiable, sin conexión, más rápido

### RMI vs Web Services
- **RMI**: Java-específico, mejor rendimiento, tight coupling
- **Web Services**: Multi-plataforma, estándares web, loose coupling

---

## Conclusión

El ejercicio 6.4.1 demuestra exitosamente el uso de Java RMI para crear aplicaciones distribuidas de chat. La implementación incluye todas las características requeridas: configuración de puertos, conexión a peers remotos, y comunicación bidireccional. Las pruebas comprueban la robustez del sistema y la demostración muestra su uso práctico.

La aplicación RMI Chat es un ejemplo completo de programación distribuida en Java, mostrando cómo RMI simplifica la comunicación entre objetos remotos manteniendo la semántica de llamadas a métodos locales.
