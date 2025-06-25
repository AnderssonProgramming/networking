package edu.eci.arsw.networking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.net.Socket;
import java.io.*;

/**
 * Test class for SquareServer
 * Verifies that the server correctly calculates squares and handles various input scenarios
 */
class SquareServerTest {
    
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 35000;
    private SquareServer server;
    private Thread serverThread;
    
    @BeforeEach
    void setUp() throws InterruptedException {
        // Start server in a separate thread for testing
        server = new SquareServer();
        serverThread = new Thread(() -> server.startServer());
        serverThread.setDaemon(true); // Daemon thread will not prevent JVM from exiting
        serverThread.start();
        
        // Give server time to start
        Thread.sleep(1000);
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
    void testPositiveIntegerSquare() throws IOException {
        // Test with positive integer
        String result = sendMessageToServer("5");
        assertEquals("Respuesta: 25", result);
    }
    
    @Test
    void testNegativeIntegerSquare() throws IOException {
        // Test with negative integer
        String result = sendMessageToServer("-3");
        assertEquals("Respuesta: 9", result);
    }
    
    @Test
    void testZeroSquare() throws IOException {
        // Test with zero
        String result = sendMessageToServer("0");
        assertEquals("Respuesta: 0", result);
    }
    
    @Test
    void testDecimalSquare() throws IOException {
        // Test with decimal number
        String result = sendMessageToServer("2.5");
        assertEquals("Respuesta: 6.250000", result);
    }
    
    @Test
    void testInvalidInput() throws IOException {
        // Test with invalid input
        String result = sendMessageToServer("abc");
        assertTrue(result.startsWith("Respuesta: Error"));
        assertTrue(result.contains("no es un número válido"));
    }
    
    @Test
    void testEmptyInput() throws IOException {
        // Test with empty input
        String result = sendMessageToServer("");
        assertEquals("Respuesta: Error - Entrada vacía", result);
    }
    
    @Test
    void testWhitespaceInput() throws IOException {
        // Test with whitespace
        String result = sendMessageToServer("   ");
        assertEquals("Respuesta: Error - Entrada vacía", result);
    }
    
    @Test
    void testLargeNumber() throws IOException {
        // Test with large number
        String result = sendMessageToServer("100");
        assertEquals("Respuesta: 10000", result);
    }
    
    @Test
    void testFractionalResult() throws IOException {
        // Test with number that results in fractional square
        String result = sendMessageToServer("1.5");
        assertEquals("Respuesta: 2.250000", result);
    }
    
    /**
     * Helper method to send a message to the server and get the response
     * @param message The message to send
     * @return The server's response
     * @throws IOException If there's an I/O error
     */
    private String sendMessageToServer(String message) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(message);
            return in.readLine();
        }
    }
}
