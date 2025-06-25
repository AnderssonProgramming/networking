# EJERCICIO 2 - Simple Browser Application

## Descripción

Este programa implementa la solución al **Ejercicio 2** del taller de networking en Java. La aplicación actúa como un navegador simple que pregunta al usuario por una dirección URL, lee los datos de esa dirección y los almacena en un archivo llamado `resultado.html`.

## Funcionalidad

El programa demuestra los siguientes conceptos de networking en Java:

1. **Lectura de páginas web** usando `URLConnection`
2. **Manejo de flujos de datos (streams)** para leer contenido de internet
3. **Escritura de archivos** para guardar el contenido descargado
4. **Manejo de excepciones** para conexiones de red
5. **Configuración de conexiones** (timeouts, user-agent)

## Características Implementadas

### Funcionalidades Principales

- **Entrada interactiva**: Solicita URL al usuario mediante consola
- **Validación de URL**: Agrega protocolo HTTP si no está presente
- **Descarga de contenido**: Lee páginas web completas
- **Guardado automático**: Almacena el contenido en `resultado.html`
- **Progreso visual**: Muestra puntos durante la descarga
- **Información detallada**: Muestra tipo de contenido, tamaño, líneas leídas

### Características Técnicas

- **Try-with-resources**: Manejo automático de recursos
- **StandardCharsets.UTF_8**: Codificación apropiada de caracteres
- **Timeouts configurables**: Evita conexiones colgadas
- **User-Agent personalizado**: Evita bloqueos por parte de servidores
- **Manejo robusto de errores**: Mensajes descriptivos para diferentes tipos de error

## Estructura del Código

### SimpleBrowser.java

```java
public class SimpleBrowser {
    private static final String OUTPUT_FILE = "resultado.html";
    
    // Métodos principales:
    // - main(): Interfaz de usuario y control principal
    // - downloadWebPage(): Descarga y guardado del contenido
}
```

### Clases y Conceptos Utilizados

- **`URL`**: Representación de direcciones web
- **`URLConnection`**: Conexión a recursos web
- **`BufferedReader`**: Lectura eficiente de texto
- **`InputStreamReader`**: Conversión de bytes a caracteres
- **`PrintWriter`**: Escritura de texto a archivos
- **`FileWriter`**: Escritura específica a archivos

## Ejecución

### Comando de Ejecución
```bash
cd networking
mvn compile
java -cp target/classes edu.eci.arsw.networking.SimpleBrowser
```

### Ejemplos de Uso

1. **URL simple**:
   ```
   Ingrese la dirección URL: www.example.com
   ```

2. **URL con protocolo**:
   ```
   Ingrese la dirección URL: https://httpbin.org/html
   ```

3. **URL local**:
   ```
   Ingrese la dirección URL: localhost:8080/index.html
   ```

## Salida Esperada

```
=== SIMPLE BROWSER APPLICATION ===
Ingrese la dirección URL: httpbin.org/html
Agregando protocolo HTTP: http://httpbin.org/html
Conectando a: http://httpbin.org/html
Tipo de contenido: text/html; charset=utf-8
Tamaño del contenido: 3741 bytes
Descargando contenido...
¡Descarga completada!
Líneas leídas: 14
Bytes aproximados: 3742
Archivo guardado como: C:\Users\Usuario\networking\resultado.html

Puede abrir el archivo 'resultado.html' en su navegador web.
```

## Manejo de Errores

El programa maneja varios tipos de errores de forma robusta:

### Errores de URL
- **URL malformada**: Mensaje descriptivo sobre formato correcto
- **URL vacía**: Validación de entrada del usuario

### Errores de Conexión
- **Connection refused**: Servidor no disponible
- **UnknownHostException**: Host no encontrado
- **Timeout**: Conexión o lectura tardó demasiado

### Errores de E/S
- **FileNotFoundException**: Problemas creando archivo de salida
- **IOException**: Problemas generales de lectura/escritura

## Testing

Se incluyen pruebas unitarias en `SimpleBrowserTest.java` que verifican:

- **Creación de archivos**: Verifica que se crea el archivo de salida
- **Configuración de conexiones**: Prueba timeouts y headers
- **Procesamiento de strings**: Validación y formateo de URLs
- **Operaciones de archivos**: Escritura y lectura de contenido
- **Manejo de excepciones**: Validación de URLs malformadas

### Ejecutar las Pruebas
```bash
mvn test
```

## Archivos Generados

### resultado.html
- **Ubicación**: Directorio raíz del proyecto
- **Contenido**: HTML completo de la página descargada
- **Codificación**: UTF-8
- **Uso**: Puede abrirse directamente en navegadores web

## Buenas Prácticas Implementadas

1. **Gestión de Recursos**
   - Try-with-resources para cierre automático de streams
   - Manejo seguro de excepciones

2. **Configuración de Conexión**
   - User-Agent personalizado para evitar bloqueos
   - Timeouts para evitar conexiones colgadas
   - Codificación UTF-8 explícita

3. **Experiencia de Usuario**
   - Mensajes informativos durante el proceso
   - Validación y corrección automática de URLs
   - Progreso visual durante la descarga

4. **Robustez**
   - Manejo específico de diferentes tipos de errores
   - Validación de entrada del usuario
   - Mensajes de error descriptivos

## Conceptos de Networking Demostrados

### Flujos de Datos (Streams)
- **InputStream**: Lectura de bytes desde la red
- **InputStreamReader**: Conversión de bytes a caracteres
- **BufferedReader**: Lectura eficiente con buffer

### Conexiones de Red
- **URLConnection**: Conexión a recursos HTTP/HTTPS
- **Request Headers**: Configuración de User-Agent
- **Response Headers**: Lectura de Content-Type y Content-Length

### Protocolos Web
- **HTTP/HTTPS**: Protocolos de transferencia web
- **Content-Type**: Identificación del tipo de contenido
- **User-Agent**: Identificación del cliente

## Extensiones Posibles

1. **Soporte para HTTPS**: Ya implementado automáticamente
2. **Descarga de imágenes**: Modificar para archivos binarios
3. **Múltiples archivos**: Descargar varios URLs en secuencia
4. **Interface gráfica**: Convertir a aplicación GUI
5. **Configuración avanzada**: Proxies, autenticación, cookies

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Conexión a internet para descargar páginas web
- Permisos de escritura en el directorio del proyecto

## Autor

Implementación del ejercicio basado en el tutorial de networking de Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería - Arquitectura Empresarial.
