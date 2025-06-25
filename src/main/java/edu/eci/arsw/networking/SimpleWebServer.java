package edu.eci.arsw.networking;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Exercise 4.5.1: Simple Web Server
 * This web server supports multiple sequential requests (non-concurrent) and serves
 * all requested files including HTML pages and images.
 * 
 * Features:
 * - HTTP/1.1 protocol support
 * - Static file serving (HTML, images, CSS, JS, etc.)
 * - Proper MIME type detection
 * - HTTP status codes (200, 404, 500)
 * - Basic HTTP headers
 * - Sequential request handling
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class SimpleWebServer {
    
    private static final int SERVER_PORT = 8081;
    private static final String WEB_ROOT = "webroot";
    private static final String DEFAULT_FILE = "index.html";
    private static final String HTTP_VERSION = "HTTP/1.1";
    
    // MIME type mappings
    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("zip", "application/zip");
    }
    
    private boolean isRunning = true;
    
    /**
     * Main method to start the web server
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SimpleWebServer server = new SimpleWebServer();
        server.startServer();
    }
    
    /**
     * Starts the web server and listens for client connections
     */
    public void startServer() {
        System.out.println("=== SIMPLE WEB SERVER ===");
        System.out.println("Servidor web iniciado en puerto: " + SERVER_PORT);
        System.out.println("Directorio web: " + WEB_ROOT);
        System.out.println("URL: http://localhost:" + SERVER_PORT);
        System.out.println("Presione Ctrl+C para detener el servidor");
        System.out.println();
        
        // Create web root directory if it doesn't exist
        createWebRoot();
        
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            
            while (isRunning) {
                try {
                    // Wait for client connection (sequential, not concurrent)
                    Socket clientSocket = serverSocket.accept();
                    String clientAddress = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("Nueva conexión HTTP desde: " + clientAddress);
                    
                    // Handle the HTTP request
                    handleHttpRequest(clientSocket);
                    
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error aceptando conexión del cliente: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error iniciando el servidor web: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles a single HTTP request
     * @param clientSocket The socket connection to the client
     */
    private void handleHttpRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream();
             PrintWriter headerOut = new PrintWriter(out, true)) {
            
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            
            // Read the HTTP request line
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                System.out.println("Solicitud vacía desde: " + clientAddress);
                return;
            }
            
            System.out.println("Solicitud HTTP: " + requestLine);
            
            // Parse the request line (e.g., "GET /index.html HTTP/1.1")
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendErrorResponse(headerOut, out, 400, "Bad Request", "Solicitud HTTP malformada");
                return;
            }
            
            String method = requestParts[0];
            String requestedPath = requestParts[1];
            
            // Only support GET method for this simple server
            if (!"GET".equals(method)) {
                sendErrorResponse(headerOut, out, 405, "Method Not Allowed", "Método no soportado: " + method);
                return;
            }
            
            // Read and skip remaining headers (not processing them in this simple server)
            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                // Skip headers for this simple implementation
            }
            
            // Serve the requested file
            serveFile(requestedPath, headerOut, out, clientAddress);
            
        } catch (IOException e) {
            System.err.println("Error procesando solicitud HTTP: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Conexión HTTP cerrada");
            } catch (IOException e) {
                System.err.println("Error cerrando socket HTTP: " + e.getMessage());
            }
        }
    }
    
    /**
     * Serves a file to the client
     * @param requestedPath The path requested by the client
     * @param headerOut PrintWriter for sending HTTP headers
     * @param out OutputStream for sending file content
     * @param clientAddress Client IP address for logging
     */
    private void serveFile(String requestedPath, PrintWriter headerOut, OutputStream out, String clientAddress) {
        try {
            // Clean and resolve the requested path
            String filePath = resolveFilePath(requestedPath);
            Path fullPath = Paths.get(WEB_ROOT, filePath);
            
            System.out.println("Solicitando archivo: " + filePath);
            System.out.println("Ruta completa: " + fullPath.toAbsolutePath());
            
            // Check if file exists and is readable
            if (!Files.exists(fullPath) || !Files.isReadable(fullPath)) {
                System.out.println("Archivo no encontrado: " + fullPath);
                sendErrorResponse(headerOut, out, 404, "Not Found", 
                                "El archivo solicitado no fue encontrado: " + filePath);
                return;
            }
            
            // Check if it's a directory
            if (Files.isDirectory(fullPath)) {
                // Try to serve index.html from the directory
                Path indexPath = fullPath.resolve(DEFAULT_FILE);
                if (Files.exists(indexPath) && Files.isReadable(indexPath)) {
                    fullPath = indexPath;
                    filePath = filePath + (filePath.endsWith("/") ? "" : "/") + DEFAULT_FILE;
                } else {
                    sendErrorResponse(headerOut, out, 403, "Forbidden", 
                                    "Acceso a directorio no permitido: " + filePath);
                    return;
                }
            }
            
            // Read file content
            byte[] fileContent = Files.readAllBytes(fullPath);
            String mimeType = getMimeType(filePath);
            
            // Send HTTP response headers
            headerOut.println(HTTP_VERSION + " 200 OK");
            headerOut.println("Date: " + new Date());
            headerOut.println("Server: SimpleWebServer/1.0");
            headerOut.println("Content-Type: " + mimeType);
            headerOut.println("Content-Length: " + fileContent.length);
            headerOut.println("Connection: close");
            headerOut.println(); // Empty line to end headers
            headerOut.flush();
            
            // Send file content
            out.write(fileContent);
            out.flush();
            
            System.out.println("Archivo servido exitosamente: " + filePath + 
                             " (" + fileContent.length + " bytes, " + mimeType + ")");
            
        } catch (IOException e) {
            System.err.println("Error sirviendo archivo: " + e.getMessage());
            sendErrorResponse(headerOut, out, 500, "Internal Server Error", 
                            "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Sends an HTTP error response
     * @param headerOut PrintWriter for headers
     * @param out OutputStream for content
     * @param statusCode HTTP status code
     * @param statusText HTTP status text
     * @param message Error message to display
     */
    private void sendErrorResponse(PrintWriter headerOut, OutputStream out, 
                                 int statusCode, String statusText, String message) {
        try {
            String htmlContent = generateErrorPage(statusCode, statusText, message);
            byte[] contentBytes = htmlContent.getBytes("UTF-8");
            
            // Send HTTP headers
            headerOut.println(HTTP_VERSION + " " + statusCode + " " + statusText);
            headerOut.println("Date: " + new Date());
            headerOut.println("Server: SimpleWebServer/1.0");
            headerOut.println("Content-Type: text/html; charset=UTF-8");
            headerOut.println("Content-Length: " + contentBytes.length);
            headerOut.println("Connection: close");
            headerOut.println(); // Empty line to end headers
            headerOut.flush();
            
            // Send HTML content
            out.write(contentBytes);
            out.flush();
            
            System.out.println("Error HTTP " + statusCode + " enviado: " + message);
            
        } catch (IOException e) {
            System.err.println("Error enviando respuesta de error: " + e.getMessage());
        }
    }
    
    /**
     * Generates an HTML error page
     * @param statusCode HTTP status code
     * @param statusText HTTP status text
     * @param message Error message
     * @return HTML content as string
     */
    private String generateErrorPage(int statusCode, String statusText, String message) {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"es\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Error " + statusCode + " - " + statusText + "</title>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }\n" +
               "        .error-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
               "        h1 { color: #d32f2f; margin-bottom: 20px; }\n" +
               "        p { color: #555; line-height: 1.6; }\n" +
               "        .error-code { font-size: 72px; font-weight: bold; color: #d32f2f; margin: 0; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"error-container\">\n" +
               "        <div class=\"error-code\">" + statusCode + "</div>\n" +
               "        <h1>" + statusText + "</h1>\n" +
               "        <p>" + message + "</p>\n" +
               "        <hr>\n" +
               "        <p><small>SimpleWebServer/1.0 - Puerto " + SERVER_PORT + "</small></p>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    /**
     * Resolves and cleans the file path from the HTTP request
     * @param requestedPath The raw path from the HTTP request
     * @return Cleaned and resolved file path
     */
    private String resolveFilePath(String requestedPath) {
        // Remove query parameters
        int queryIndex = requestedPath.indexOf('?');
        if (queryIndex != -1) {
            requestedPath = requestedPath.substring(0, queryIndex);
        }
        
        // Remove leading slash
        if (requestedPath.startsWith("/")) {
            requestedPath = requestedPath.substring(1);
        }
        
        // If empty, use default file
        if (requestedPath.isEmpty()) {
            requestedPath = DEFAULT_FILE;
        }
        
        // Basic security: prevent directory traversal
        requestedPath = requestedPath.replace("..", "").replace("//", "/");
        
        return requestedPath;
    }
    
    /**
     * Determines the MIME type based on file extension
     * @param filePath The file path
     * @return MIME type string
     */
    private String getMimeType(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot != -1 && lastDot < filePath.length() - 1) {
            String extension = filePath.substring(lastDot + 1).toLowerCase();
            return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
        }
        return "application/octet-stream";
    }
    
    /**
     * Creates the web root directory and sample files if they don't exist
     */
    private void createWebRoot() {
        try {
            Path webRootPath = Paths.get(WEB_ROOT);
            if (!Files.exists(webRootPath)) {
                Files.createDirectories(webRootPath);
                System.out.println("Directorio web creado: " + webRootPath.toAbsolutePath());
                
                // Create a sample index.html file
                createSampleFiles();
            }
        } catch (IOException e) {
            System.err.println("Error creando directorio web: " + e.getMessage());
        }
    }
    
    /**
     * Creates sample files for testing the web server
     */
    private void createSampleFiles() {
        try {
            // Create index.html
            String indexContent = generateSampleIndexPage();
            Files.write(Paths.get(WEB_ROOT, "index.html"), indexContent.getBytes("UTF-8"));
            
            // Create a sample CSS file
            String cssContent = generateSampleCSS();
            Files.write(Paths.get(WEB_ROOT, "style.css"), cssContent.getBytes("UTF-8"));
            
            // Create a sample about page
            String aboutContent = generateSampleAboutPage();
            Files.write(Paths.get(WEB_ROOT, "about.html"), aboutContent.getBytes("UTF-8"));
            
            System.out.println("Archivos de ejemplo creados en " + WEB_ROOT);
            
        } catch (IOException e) {
            System.err.println("Error creando archivos de ejemplo: " + e.getMessage());
        }
    }
    
    /**
     * Generates sample index.html content
     * @return HTML content as string
     */
    private String generateSampleIndexPage() {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"es\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>SimpleWebServer - Ejercicio 4.5.1</title>\n" +
               "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"container\">\n" +
               "        <header>\n" +
               "            <h1>🌐 SimpleWebServer</h1>\n" +
               "            <p>Ejercicio 4.5.1 - Servidor Web Básico</p>\n" +
               "        </header>\n" +
               "        \n" +
               "        <main>\n" +
               "            <section class=\"welcome\">\n" +
               "                <h2>¡Bienvenido!</h2>\n" +
               "                <p>Este es un servidor web simple implementado en Java que soporta:</p>\n" +
               "                <ul>\n" +
               "                    <li>✅ Múltiples solicitudes secuenciales</li>\n" +
               "                    <li>✅ Archivos HTML</li>\n" +
               "                    <li>✅ Imágenes (JPG, PNG, GIF, SVG)</li>\n" +
               "                    <li>✅ CSS y JavaScript</li>\n" +
               "                    <li>✅ Detección automática de MIME types</li>\n" +
               "                    <li>✅ Códigos de estado HTTP</li>\n" +
               "                </ul>\n" +
               "            </section>\n" +
               "            \n" +
               "            <section class=\"features\">\n" +
               "                <h2>Funcionalidad Implementada</h2>\n" +
               "                <div class=\"feature-grid\">\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>📄 Servir Archivos</h3>\n" +
               "                        <p>Sirve archivos estáticos desde el directorio webroot/</p>\n" +
               "                    </div>\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>🔗 Enlaces</h3>\n" +
               "                        <p><a href=\"about.html\">Ir a página About</a></p>\n" +
               "                        <p><a href=\"nonexistent.html\">Probar error 404</a></p>\n" +
               "                    </div>\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>🎨 Estilos CSS</h3>\n" +
               "                        <p>Esta página usa <a href=\"style.css\">CSS externo</a></p>\n" +
               "                    </div>\n" +
               "                </div>\n" +
               "            </section>\n" +
               "            \n" +
               "            <section class=\"info\">\n" +
               "                <h2>Información del Servidor</h2>\n" +
               "                <table>\n" +
               "                    <tr><td><strong>Puerto:</strong></td><td>" + SERVER_PORT + "</td></tr>\n" +
               "                    <tr><td><strong>Protocolo:</strong></td><td>HTTP/1.1</td></tr>\n" +
               "                    <tr><td><strong>Directorio web:</strong></td><td>" + WEB_ROOT + "/</td></tr>\n" +
               "                    <tr><td><strong>Fecha:</strong></td><td><script>document.write(new Date().toLocaleString());</script></td></tr>\n" +
               "                </table>\n" +
               "            </section>\n" +
               "        </main>\n" +
               "        \n" +
               "        <footer>\n" +
               "            <p>SimpleWebServer/1.0 - Ejercicio de Networking en Java<br>\n" +
               "            <small>Andersson David Sánchez Méndez - Escuela Colombiana de Ingeniería</small></p>\n" +
               "        </footer>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    /**
     * Generates sample CSS content
     * @return CSS content as string
     */
    private String generateSampleCSS() {
        return "/* SimpleWebServer Sample CSS */\n" +
               "* {\n" +
               "    margin: 0;\n" +
               "    padding: 0;\n" +
               "    box-sizing: border-box;\n" +
               "}\n" +
               "\n" +
               "body {\n" +
               "    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
               "    line-height: 1.6;\n" +
               "    color: #333;\n" +
               "    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
               "    min-height: 100vh;\n" +
               "}\n" +
               "\n" +
               ".container {\n" +
               "    max-width: 1200px;\n" +
               "    margin: 0 auto;\n" +
               "    padding: 20px;\n" +
               "    background: white;\n" +
               "    margin-top: 20px;\n" +
               "    margin-bottom: 20px;\n" +
               "    border-radius: 12px;\n" +
               "    box-shadow: 0 10px 30px rgba(0,0,0,0.2);\n" +
               "}\n" +
               "\n" +
               "header {\n" +
               "    text-align: center;\n" +
               "    padding: 30px 0;\n" +
               "    border-bottom: 2px solid #eee;\n" +
               "    margin-bottom: 30px;\n" +
               "}\n" +
               "\n" +
               "header h1 {\n" +
               "    font-size: 2.5em;\n" +
               "    color: #4a5568;\n" +
               "    margin-bottom: 10px;\n" +
               "}\n" +
               "\n" +
               "header p {\n" +
               "    color: #718096;\n" +
               "    font-size: 1.1em;\n" +
               "}\n" +
               "\n" +
               "section {\n" +
               "    margin-bottom: 30px;\n" +
               "}\n" +
               "\n" +
               "h2 {\n" +
               "    color: #2d3748;\n" +
               "    margin-bottom: 15px;\n" +
               "    font-size: 1.8em;\n" +
               "}\n" +
               "\n" +
               "h3 {\n" +
               "    color: #4a5568;\n" +
               "    margin-bottom: 10px;\n" +
               "}\n" +
               "\n" +
               "ul {\n" +
               "    margin-left: 20px;\n" +
               "    margin-bottom: 15px;\n" +
               "}\n" +
               "\n" +
               "li {\n" +
               "    margin-bottom: 8px;\n" +
               "}\n" +
               "\n" +
               ".feature-grid {\n" +
               "    display: grid;\n" +
               "    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));\n" +
               "    gap: 20px;\n" +
               "    margin-top: 20px;\n" +
               "}\n" +
               "\n" +
               ".feature {\n" +
               "    background: #f7fafc;\n" +
               "    padding: 20px;\n" +
               "    border-radius: 8px;\n" +
               "    border-left: 4px solid #667eea;\n" +
               "}\n" +
               "\n" +
               "table {\n" +
               "    width: 100%;\n" +
               "    border-collapse: collapse;\n" +
               "    margin-top: 15px;\n" +
               "}\n" +
               "\n" +
               "table td {\n" +
               "    padding: 12px;\n" +
               "    border-bottom: 1px solid #e2e8f0;\n" +
               "}\n" +
               "\n" +
               "table td:first-child {\n" +
               "    background: #f7fafc;\n" +
               "    width: 200px;\n" +
               "}\n" +
               "\n" +
               "a {\n" +
               "    color: #667eea;\n" +
               "    text-decoration: none;\n" +
               "    font-weight: 500;\n" +
               "}\n" +
               "\n" +
               "a:hover {\n" +
               "    color: #764ba2;\n" +
               "    text-decoration: underline;\n" +
               "}\n" +
               "\n" +
               "footer {\n" +
               "    text-align: center;\n" +
               "    padding: 20px 0;\n" +
               "    border-top: 2px solid #eee;\n" +
               "    margin-top: 30px;\n" +
               "    color: #718096;\n" +
               "}\n" +
               "\n" +
               "@media (max-width: 768px) {\n" +
               "    .container {\n" +
               "        margin: 10px;\n" +
               "        padding: 15px;\n" +
               "    }\n" +
               "    \n" +
               "    header h1 {\n" +
               "        font-size: 2em;\n" +
               "    }\n" +
               "    \n" +
               "    .feature-grid {\n" +
               "        grid-template-columns: 1fr;\n" +
               "    }\n" +
               "}";
    }
    
    /**
     * Generates sample about page content
     * @return HTML content as string
     */
    private String generateSampleAboutPage() {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"es\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Acerca de - SimpleWebServer</title>\n" +
               "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"container\">\n" +
               "        <header>\n" +
               "            <h1>📋 Acerca del Proyecto</h1>\n" +
               "            <p>Ejercicio 4.5.1 - Servidor Web Básico</p>\n" +
               "        </header>\n" +
               "        \n" +
               "        <main>\n" +
               "            <section>\n" +
               "                <h2>Descripción del Ejercicio</h2>\n" +
               "                <p>Este proyecto implementa un <strong>servidor web simple</strong> como parte del Ejercicio 4.5.1 del taller de networking en Java.</p>\n" +
               "                \n" +
               "                <h3>Requisitos Cumplidos:</h3>\n" +
               "                <ul>\n" +
               "                    <li>✅ Soporta múltiples solicitudes seguidas (no concurrentes)</li>\n" +
               "                    <li>✅ Retorna todos los archivos solicitados</li>\n" +
               "                    <li>✅ Incluye páginas HTML</li>\n" +
               "                    <li>✅ Incluye imágenes y otros archivos estáticos</li>\n" +
               "                </ul>\n" +
               "            </section>\n" +
               "            \n" +
               "            <section>\n" +
               "                <h2>Características Técnicas</h2>\n" +
               "                <div class=\"feature-grid\">\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>🔧 Protocolo HTTP</h3>\n" +
               "                        <p>Implementa HTTP/1.1 básico con headers estándar</p>\n" +
               "                    </div>\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>📁 Archivos Estáticos</h3>\n" +
               "                        <p>Sirve archivos desde el directorio webroot/</p>\n" +
               "                    </div>\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>🎯 MIME Types</h3>\n" +
               "                        <p>Detección automática del tipo de contenido</p>\n" +
               "                    </div>\n" +
               "                    <div class=\"feature\">\n" +
               "                        <h3>🛡️ Manejo de Errores</h3>\n" +
               "                        <p>Códigos de estado HTTP apropiados (200, 404, 500)</p>\n" +
               "                    </div>\n" +
               "                </div>\n" +
               "            </section>\n" +
               "            \n" +
               "            <section>\n" +
               "                <h2>Navegación</h2>\n" +
               "                <p><a href=\"index.html\">← Volver al inicio</a></p>\n" +
               "                <p><a href=\"style.css\">Ver archivo CSS</a></p>\n" +
               "            </section>\n" +
               "        </main>\n" +
               "        \n" +
               "        <footer>\n" +
               "            <p>SimpleWebServer/1.0 - Implementación de Servidor Web<br>\n" +
               "            <small>Basado en el tutorial de networking de Andersson David Sánchez Méndez</small></p>\n" +
               "        </footer>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    /**
     * Stops the server gracefully
     */
    public void stopServer() {
        isRunning = false;
        System.out.println("Deteniendo servidor web...");
    }
}
