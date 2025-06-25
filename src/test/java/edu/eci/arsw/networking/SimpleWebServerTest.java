package edu.eci.arsw.networking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.net.Socket;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class for SimpleWebServer
 * Verifies that the web server correctly serves files and handles HTTP requests
 * according to Exercise 4.5.1 requirements
 */
class SimpleWebServerTest {
    
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8081;
    private static final String BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT;
    
    private SimpleWebServer server;
    private Thread serverThread;
    
    @BeforeEach
    void setUp() throws InterruptedException {
        // Start server in a separate thread for testing
        server = new SimpleWebServer();
        serverThread = new Thread(() -> server.startServer());
        serverThread.setDaemon(true); // Daemon thread will not prevent JVM from exiting
        serverThread.start();
        
        // Give server time to start
        Thread.sleep(2000);
    }
    
    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stopServer();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }
    
    @Test
    void testIndexPageServed() throws IOException {
        // Test that index.html is served correctly
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/").openConnection();
        
        assertEquals(200, connection.getResponseCode());
        assertEquals("text/html", connection.getContentType());
        
        String content = readResponse(connection);
        assertTrue(content.contains("SimpleWebServer"));
        assertTrue(content.contains("Ejercicio 4.5.1"));
    }
    
    @Test
    void testSpecificHtmlFile() throws IOException {
        // Test serving a specific HTML file
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/about.html").openConnection();
        
        assertEquals(200, connection.getResponseCode());
        assertEquals("text/html", connection.getContentType());
        
        String content = readResponse(connection);
        assertTrue(content.contains("Acerca del Proyecto"));
    }
    
    @Test
    void testCssFileServed() throws IOException {
        // Test that CSS files are served with correct MIME type
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/style.css").openConnection();
        
        assertEquals(200, connection.getResponseCode());
        assertEquals("text/css", connection.getContentType());
        
        String content = readResponse(connection);
        assertTrue(content.contains("SimpleWebServer Sample CSS"));
    }
    
    @Test
    void test404ErrorHandling() throws IOException {
        // Test 404 error for non-existent files
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/nonexistent.html").openConnection();
        
        assertEquals(404, connection.getResponseCode());
        
        String content = readErrorResponse(connection);
        assertTrue(content.contains("404"));
        assertTrue(content.contains("Not Found"));
    }
    
    @Test
    void testSequentialRequests() throws IOException {
        // Test multiple sequential requests (non-concurrent as per exercise)
        
        // Request 1: Index page
        HttpURLConnection conn1 = (HttpURLConnection) new URL(BASE_URL + "/").openConnection();
        assertEquals(200, conn1.getResponseCode());
        conn1.disconnect();
        
        // Request 2: About page
        HttpURLConnection conn2 = (HttpURLConnection) new URL(BASE_URL + "/about.html").openConnection();
        assertEquals(200, conn2.getResponseCode());
        conn2.disconnect();
        
        // Request 3: CSS file
        HttpURLConnection conn3 = (HttpURLConnection) new URL(BASE_URL + "/style.css").openConnection();
        assertEquals(200, conn3.getResponseCode());
        assertEquals("text/css", conn3.getContentType());
        conn3.disconnect();
        
        // Request 4: Non-existent file
        HttpURLConnection conn4 = (HttpURLConnection) new URL(BASE_URL + "/missing.html").openConnection();
        assertEquals(404, conn4.getResponseCode());
        conn4.disconnect();
    }
    
    @Test
    void testHttpHeaders() throws IOException {
        // Test that proper HTTP headers are sent
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/").openConnection();
        
        assertEquals(200, connection.getResponseCode());
        assertNotNull(connection.getHeaderField("Date"));
        assertNotNull(connection.getHeaderField("Server"));
        assertEquals("SimpleWebServer/1.0", connection.getHeaderField("Server"));
        assertEquals("close", connection.getHeaderField("Connection"));
        assertTrue(Integer.parseInt(connection.getHeaderField("Content-Length")) > 0);
    }
    
    @Test
    void testMimeTypeDetection() throws IOException {
        // Test MIME type detection for different file types
        
        // HTML file
        HttpURLConnection htmlConn = (HttpURLConnection) new URL(BASE_URL + "/index.html").openConnection();
        assertEquals("text/html", htmlConn.getContentType());
        htmlConn.disconnect();
        
        // CSS file
        HttpURLConnection cssConn = (HttpURLConnection) new URL(BASE_URL + "/style.css").openConnection();
        assertNotEquals("text/css", cssConn.getContentType());
        cssConn.disconnect();
    }
    
    @Test
    void testDirectoryAccessDenied() throws IOException {
        // Test that directory access is properly handled
        // Create a subdirectory for testing
        try {
            Files.createDirectories(Paths.get("webroot/testdir"));
            
            HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/testdir/").openConnection();
            
            // Should return 403 Forbidden or redirect, depending on implementation
            int responseCode = connection.getResponseCode();
            assertTrue(responseCode == 403 || responseCode == 404);
            
        } finally {
            // Clean up
            try {
                Files.deleteIfExists(Paths.get("webroot/testdir"));
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    @Test
    void testLargeFileHandling() throws IOException {
        // Test handling of larger files (CSS file should be reasonably sized)
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/style.css").openConnection();
        
        assertEquals(200, connection.getResponseCode());
        
        String content = readResponse(connection);
        // CSS file should be more than 1000 characters
        assertTrue(content.length() > 1000);
    }
    
    /**
     * Helper method to read successful HTTP response
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }
    
    /**
     * Helper method to read error HTTP response
     */
    private String readErrorResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }
}
