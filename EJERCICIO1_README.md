# EJERCICIO 1 - URL Analysis Program

## Descripción

Este programa implementa la solución al **Ejercicio 1** del taller de networking en Java. El programa crea un objeto URL e imprime en pantalla cada uno de los datos que retornan los 8 métodos principales de la clase URL.

## Funcionalidad

El programa demuestra el uso de los siguientes 8 métodos de la clase `java.net.URL`:

1. **`getProtocol()`** - Retorna el protocolo de la URL (http, https, ftp, etc.)
2. **`getAuthority()`** - Retorna la autoridad de la URL (host:puerto)
3. **`getHost()`** - Retorna el nombre del host
4. **`getPort()`** - Retorna el número de puerto (o -1 si usa puerto por defecto)
5. **`getPath()`** - Retorna la ruta del recurso
6. **`getQuery()`** - Retorna la cadena de consulta (parámetros)
7. **`getFile()`** - Retorna la ruta + consulta concatenadas
8. **`getRef()`** - Retorna la referencia del fragmento (#anchor)

## Estructura del Código

### URLAnalyzer.java

```java
package edu.eci.arsw.networking;

import java.net.URL;
import java.net.MalformedURLException;
```

La clase principal contiene:
- **`main()`** - Método principal que crea dos ejemplos de URL diferentes
- **`analyzeURL()`** - Método auxiliar que imprime todos los componentes de una URL

### Ejemplos de URLs utilizadas

1. **URL completa**: `https://www.example.com:8080/path/to/resource?param1=value1&param2=value2#section1`
   - Incluye todos los componentes posibles para demostrar cada método
   
2. **URL simple**: `http://localhost:3000/api/users`
   - Ejemplo más básico para mostrar valores null en algunos componentes

## Ejecución

### Opción 1: Usando Maven y Java directamente
```bash
cd networking
mvn compile
java -cp target/classes edu.eci.arsw.networking.URLAnalyzer
```

### Opción 2: Usando únicamente Maven (si se configura exec plugin)
```bash
mvn compile exec:java -Dexec.mainClass=edu.eci.arsw.networking.URLAnalyzer
```

## Salida Esperada

```
=== URL ANALYSIS ===
URL: https://www.example.com:8080/path/to/resource?param1=value1&param2=value2#section1

Protocol       : https
Authority      : www.example.com:8080
Host           : www.example.com
Port           : 8080
Path           : /path/to/resource
Query          : param1=value1&param2=value2
File           : /path/to/resource?param1=value1&param2=value2
Ref            : section1

==================================================
=== SECOND EXAMPLE ===
URL: http://localhost:3000/api/users

Protocol       : http
Authority      : localhost:3000
Host           : localhost
Port           : 3000
Path           : /api/users
Query          : null
File           : /api/users
Ref            : null
```

## Testing

Se incluyen pruebas unitarias en `URLAnalyzerTest.java` que verifican:

- Funcionamiento correcto de todos los métodos de URL
- Manejo de URLs con puerto por defecto
- Casos donde algunos componentes son null

Para ejecutar las pruebas:
```bash
mvn test
```

## Buenas Prácticas Implementadas

1. **Separación de responsabilidades**: Método auxiliar `analyzeURL()` para evitar duplicación de código
2. **Manejo de excepciones**: Captura de `MalformedURLException`
3. **Formato legible**: Uso de `printf` para alineación consistente
4. **Documentación**: Comentarios explicativos en el código
5. **Testing**: Pruebas unitarias que verifican la funcionalidad
6. **Supresión de warnings**: Uso de `@SuppressWarnings("deprecation")` documentado

## Conceptos de Networking Demostrados

- **URL (Uniform Resource Locator)**: Identificador de recursos en la web
- **Componentes de una URL**: Protocolo, autoridad, host, puerto, ruta, consulta, referencia
- **Puertos por defecto**: HTTP (80), HTTPS (443)
- **Parámetros de consulta**: Información adicional pasada al servidor
- **Fragmentos/Referencias**: Identificadores de secciones dentro de un documento

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Dependencias del proyecto Spring Boot (definidas en pom.xml)

## Autor

Implementación del ejercicio basado en el tutorial de networking de Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería.
