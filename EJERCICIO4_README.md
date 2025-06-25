# EJERCICIO 4.3.1 - Square Calculator Socket Server

## Descripción

Este programa implementa la solución al **Ejercicio 4.3.1** del taller de sockets en Java. El programa consiste en un servidor que recibe un número de un cliente y responde con el cuadrado de ese número, siguiendo el protocolo cliente-servidor usando sockets TCP.

## Funcionalidad

El programa demuestra los siguientes conceptos de sockets y networking en Java:

1. **Comunicación Cliente-Servidor** usando `Socket` y `ServerSocket`
2. **Manejo de flujos de datos (streams)** para comunicación bidireccional
3. **Protocolo de comunicación** estructurado
4. **Manejo de múltiples clientes** con hilos concurrentes
5. **Manejo robusto de errores** de red y validación de entrada

## Arquitectura del Sistema

### Protocolo de Comunicación

- **Cliente envía**: Un número (como string)
- **Servidor responde**: `"Respuesta: [cuadrado del número]"`
- **Manejo de errores**: `"Respuesta: Error - [descripción del error]"`

### Componentes

1. **`SquareServer.java`** - Servidor que calcula cuadrados
2. **`SquareClient.java`** - Cliente para probar el servidor
3. **`SquareServerTest.java`** - Pruebas unitarias completas

## Características Implementadas

### Funcionalidades del Servidor

- **Puerto configurable**: Escucha en puerto 35000 por defecto
- **Conexiones concurrentes**: Maneja múltiples clientes simultáneamente
- **Validación de entrada**: Verifica que la entrada sea un número válido
- **Logging detallado**: Registra todas las conexiones y transacciones
- **Cierre graceful**: Limpieza apropiada de recursos

### Funcionalidades del Cliente

- **Conexión automática**: Se conecta al servidor local
- **Interfaz interactiva**: Permite enviar múltiples números
- **Comandos de salida**: Acepta "exit" o "quit" para desconectar
- **Manejo de errores**: Detecta desconexiones del servidor

### Características Técnicas

- **Try-with-resources**: Manejo automático de sockets y streams
- **Threading**: Un hilo por cliente para concurrencia
- **Exception handling**: Manejo específico para diferentes tipos de error
- **Input validation**: Validación robusta de números enteros y decimales
- **Response formatting**: Formato consistente de respuestas

## Estructura del Código

### SquareServer.java

```java
public class SquareServer {
    private static final int SERVER_PORT = 35000;
    
    // Métodos principales:
    // - startServer(): Inicia el servidor y acepta conexiones
    // - handleClient(): Maneja comunicación con un cliente específico
    // - processInput(): Procesa entrada y calcula cuadrado
}
```

### SquareClient.java

```java
public class SquareClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 35000;
    
    // Métodos principales:
    // - startClient(): Conecta al servidor y maneja la comunicación
}
```

### Clases y Conceptos Utilizados

- **`ServerSocket`**: Escucha conexiones en un puerto específico
- **`Socket`**: Representa la conexión entre cliente y servidor
- **`BufferedReader`**: Lectura eficiente de texto desde el socket
- **`PrintWriter`**: Escritura de texto al socket
- **`Thread`**: Manejo concurrente de múltiples clientes

## Ejecución

### Paso 1: Iniciar el Servidor
```bash
cd networking
mvn compile
java -cp target/classes edu.eci.arsw.networking.SquareServer
```

### Paso 2: Conectar Cliente(s)
```bash
# En otra terminal
java -cp target/classes edu.eci.arsw.networking.SquareClient
```

### Ejemplos de Uso

1. **Número positivo**:
   ```
   Ingrese un número: 5
   Servidor responde: Respuesta: 25
   ```

2. **Número negativo**:
   ```
   Ingrese un número: -3
   Servidor responde: Respuesta: 9
   ```

3. **Número decimal**:
   ```
   Ingrese un número: 2.5
   Servidor responde: Respuesta: 6.250000
   ```

4. **Entrada inválida**:
   ```
   Ingrese un número: abc
   Servidor responde: Respuesta: Error - 'abc' no es un número válido
   ```

## Salida Esperada

### Servidor
```
=== SQUARE CALCULATOR SERVER ===
Servidor iniciado en puerto: 35000
Esperando conexiones de clientes...
Presione Ctrl+C para detener el servidor

Nueva conexión desde: 127.0.0.1
Cliente conectado desde: 127.0.0.1
Mensaje recibido de 127.0.0.1: 5
Respuesta enviada a 127.0.0.1: Respuesta: 25
Conexión cerrada con cliente: 127.0.0.1
```

### Cliente
```
=== SQUARE CALCULATOR CLIENT ===
Conectando al servidor en 127.0.0.1:35000
Conectado al servidor exitosamente!
Ingrese números para calcular su cuadrado.
Escriba 'exit' o 'quit' para salir.

Ingrese un número: 5
Servidor responde: Respuesta: 25

Ingrese un número: exit
Desconectando del servidor...
```

## Testing

Se incluyen pruebas unitarias comprensivas en `SquareServerTest.java`:

- ✅ Números enteros positivos y negativos
- ✅ Números decimales
- ✅ Cero
- ✅ Números grandes
- ✅ Entrada inválida (texto)
- ✅ Entrada vacía
- ✅ Espacios en blanco

Para ejecutar las pruebas:
```bash
mvn test -Dtest=SquareServerTest
```

## Manejo de Errores

### Errores del Servidor
- **Puerto ocupado**: `BindException: Address already in use`
- **Error de I/O**: Problemas de red durante comunicación
- **Conexión interrumpida**: Cliente desconecta inesperadamente

### Errores del Cliente
- **Servidor no disponible**: `Connection refused`
- **Timeout de conexión**: Servidor no responde
- **Pérdida de conexión**: Servidor se desconecta durante comunicación

### Validación de Entrada
- **Números inválidos**: Texto que no se puede parsear
- **Entrada vacía**: String vacío o solo espacios
- **Formato especial**: Notación científica, infinitos, etc.

## Conceptos de Networking Demostrados

- **Sockets TCP**: Comunicación confiable y ordenada
- **Protocolo Cliente-Servidor**: Arquitectura de petición-respuesta
- **Concurrencia**: Manejo de múltiples clientes simultáneos
- **Streams de I/O**: Flujos de entrada y salida para comunicación
- **Puertos de red**: Identificación de servicios específicos
- **Localhost**: Comunicación en la misma máquina (127.0.0.1)

## Mejoras Implementadas

1. **Concurrencia**: Cada cliente se maneja en un hilo separado
2. **Logging**: Registro detallado de todas las operaciones
3. **Validation**: Validación robusta de entrada con mensajes descriptivos
4. **Resource Management**: Uso de try-with-resources para limpieza automática
5. **Error Handling**: Manejo específico para diferentes tipos de error
6. **Testing**: Suite completa de pruebas unitarias

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Puerto 35000 disponible
- Dependencias del proyecto Spring Boot (definidas en pom.xml)

## Autor

Implementación del ejercicio basado en el tutorial de sockets de Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería - Arquitectura Empresarial.
